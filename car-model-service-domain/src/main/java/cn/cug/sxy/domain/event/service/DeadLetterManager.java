package cn.cug.sxy.domain.event.service;

import cn.cug.sxy.types.event.BaseEvent;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/8/15 11:41
 * @Description 死信管理器实现
 * @Author jerryhotton
 */

@Slf4j
@Service
public class DeadLetterManager implements IDeadLetterManager {

    private final List<IDeadLetterHandler> handlers;
    private final IMessagePersistenceService messagePersistenceService;
    private final IDeadLetterActionExecutor actionExecutor;
    private final IDeadLetterDecisionStrategy decisionStrategy;

    public DeadLetterManager(
            List<IDeadLetterHandler> handlers,
            IMessagePersistenceService messagePersistenceService,
            IDeadLetterActionExecutor actionExecutor,
            IDeadLetterDecisionStrategy decisionStrategy) {
        this.handlers = handlers;
        this.messagePersistenceService = messagePersistenceService;
        this.actionExecutor = actionExecutor;
        this.decisionStrategy = decisionStrategy;
        // 按优先级排序
        this.handlers.sort(Comparator.comparingInt(IDeadLetterHandler::getPriority));
        log.info("初始化死信管理器，共加载 {} 个处理器", handlers.size());
        handlers.forEach(handler ->
                log.info("加载死信处理器: {}, 优先级: {}", handler.getHandlerName(), handler.getPriority())
        );
    }

    @Override
    public void handleDeadLetter(BaseEvent.EventMessage<?> eventMessage, Message message, Channel channel) throws IOException {
        String messageId = eventMessage.getId();
        String messageTopic = eventMessage.getTopic();
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.warn("处理死信消息，messageId: {}, topic: {}", messageId, messageTopic);
            // 构建死信上下文
            IDeadLetterHandler.DeadLetterContext context = buildDeadLetterContext(eventMessage, message);
            // 获取合适的处理器
            List<IDeadLetterHandler> suitableHandlers = getHandlersForTopic(messageTopic);
            if (suitableHandlers.isEmpty()) {
                log.warn("未找到合适的死信处理器，使用默认处理，messageId: {}, topic: {}", messageId, messageTopic);
                handleWithDefaultStrategy(context, channel, deliveryTag);
                return;
            }
            // 按优先级处理
            boolean handled = false;
            for (IDeadLetterHandler handler : suitableHandlers) {
                try {
                    IDeadLetterHandler.DeadLetterAction action = handler.handleDeadLetter(context);
                    if (executeAction(action, context, channel, deliveryTag)) {
                        log.info("死信消息处理成功，messageId: {}, handler: {}, action: {}",
                                messageId, handler.getHandlerName(), action);
                        handled = true;
                        break;
                    }
                } catch (Exception e) {
                    log.error("死信处理器执行异常，messageId: {}, handler: {}",
                            messageId, handler.getHandlerName(), e);
                }
            }
            if (!handled) {
                log.warn("所有死信处理器都无法处理消息，使用默认策略，messageId: {}", messageId);
                handleWithDefaultStrategy(context, channel, deliveryTag);
            }
        } catch (Exception e) {
            log.error("死信消息处理异常，messageId: {}", messageId, e);
            // 确认消息，避免无限循环
            channel.basicAck(deliveryTag, false);
        }
    }

    @Override
    public List<IDeadLetterHandler> getAllHandlers() {
        return new ArrayList<>(handlers);
    }

    @Override
    public List<IDeadLetterHandler> getHandlersForTopic(String messageTopic) {
        return handlers.stream()
                .filter(handler -> handler.supports(messageTopic))
                .collect(Collectors.toList()); // 已经按优先级排序了
    }

    /**
     * 构建死信上下文
     */
    private IDeadLetterHandler.DeadLetterContext buildDeadLetterContext(BaseEvent.EventMessage<?> eventMessage, Message message) {
        String messageId = eventMessage.getId();
        // 从消息属性中获取原始队列信息
        String originalQueue = getOriginalQueue(message);
        String originalExchange = getOriginalExchange(message);
        String originalRoutingKey = getOriginalRoutingKey(message);
        // 从数据库获取失败信息
        IMessagePersistenceService.TaskInfo taskInfo = messagePersistenceService.getTaskInfo(messageId);
        String failureReason = taskInfo != null ? taskInfo.getErrorMessage() : "未知错误";
        int retryCount = taskInfo != null && taskInfo.getRetryCount() != null ? taskInfo.getRetryCount() : 0;

        return new IDeadLetterHandler.DeadLetterContext(
                messageId, originalQueue, originalExchange, originalRoutingKey,
                failureReason, retryCount, System.currentTimeMillis(), eventMessage
        );
    }

    /**
     * 执行死信处理动作
     */
    private boolean executeAction(IDeadLetterHandler.DeadLetterAction action,
                                  IDeadLetterHandler.DeadLetterContext context,
                                  Channel channel, long deliveryTag) throws IOException {
        switch (action) {
            case LOG_AND_IGNORE:
                log.info("死信消息已记录并忽略，messageId: {}", context.getMessageId());
                channel.basicAck(deliveryTag, false);
                return true;
            case STORE_TO_DATABASE:
                // 使用决策策略确定存储类型
                IDeadLetterActionExecutor.StorageType storageType = decisionStrategy.determineStorageType(context);
                boolean stored = actionExecutor.storeToDatabase(context, storageType);
                if (stored) {
                    channel.basicAck(deliveryTag, false);
                    return true;
                }
                return false;
            case SEND_ALERT:
                // 使用决策策略确定告警级别
                IDeadLetterActionExecutor.AlertLevel alertLevel = decisionStrategy.determineAlertLevel(context);
                boolean alertSent = actionExecutor.sendAlert(context, alertLevel);
                if (alertSent) {
                    channel.basicAck(deliveryTag, false);
                    return true;
                }
                return false;
            case REQUEUE:
                // 使用决策策略确定延迟时间
                long delayMillis = decisionStrategy.determineRequeueDelay(context);
                if (delayMillis < 0) {
                    log.warn("决策策略不允许重新入队，messageId: {}", context.getMessageId());
                    return false;
                }
                return actionExecutor.requeue(context, channel, deliveryTag, delayMillis);
            case FORWARD_TO_QUEUE:
                // 转发到其他队列（需要具体实现）
                log.info("死信消息转发到其他队列，messageId: {}", context.getMessageId());
                channel.basicAck(deliveryTag, false);
                return true;
            case CUSTOM_HANDLE:
                // 使用决策策略确定自定义处理器和参数
                String handlerName = decisionStrategy.determineCustomHandler(context);
                Object customParams = decisionStrategy.determineCustomParams(context, handlerName);
                boolean customHandled = actionExecutor.customHandle(context, handlerName, customParams);
                if (customHandled) {
                    channel.basicAck(deliveryTag, false);
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    /**
     * 默认处理策略
     */
    private void handleWithDefaultStrategy(IDeadLetterHandler.DeadLetterContext context,
                                           Channel channel, long deliveryTag) throws IOException {
        // 默认策略：记录日志并存储到数据库
        log.error("默认死信处理：messageId={}, topic={}, reason={}",
                context.getMessageId(), context.getMessageTopic(), context.getFailureReason());
        messagePersistenceService.markAsFailed(context.getMessageId(),
                "默认死信处理：" + context.getFailureReason());
        channel.basicAck(deliveryTag, false);
    }

    // 辅助方法：从消息中提取原始信息
    private String getOriginalQueue(Message message) {
        return (String) message.getMessageProperties().getHeaders().get("x-first-death-queue");
    }

    private String getOriginalExchange(Message message) {
        return (String) message.getMessageProperties().getHeaders().get("x-first-death-exchange");
    }

    private String getOriginalRoutingKey(Message message) {
        return (String) message.getMessageProperties().getHeaders().get("x-first-death-reason");
    }

}
