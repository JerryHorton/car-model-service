package cn.cug.sxy.trigger.listener;

import cn.cug.sxy.domain.event.service.IMessagePersistenceService;
import cn.cug.sxy.domain.event.service.IMessageStateSynchronizer;
import cn.cug.sxy.domain.event.service.IRetryStateManager;
import cn.cug.sxy.types.event.BaseEvent;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

import java.io.IOException;

/**
 * @version 1.0
 * @Date 2025/8/14 15:43
 * @Description 象事件消费者基类
 * @Author jerryhotton
 */

@Slf4j
public abstract class AbstractEventConsumer<T> {

    protected final IMessagePersistenceService messagePersistenceService;
    protected final IRetryStateManager retryStateManager;
    protected final IMessageStateSynchronizer messageStateSynchronizer;

    public AbstractEventConsumer(IMessagePersistenceService messagePersistenceService,
                                 IRetryStateManager retryStateManager,
                                 IMessageStateSynchronizer messageStateSynchronizer) {
        this.messagePersistenceService = messagePersistenceService;
        this.retryStateManager = retryStateManager;
        this.messageStateSynchronizer = messageStateSynchronizer;
    }

    /**
     * 通用的消息处理模板方法
     */
    protected void handleEvent(BaseEvent.EventMessage<T> eventMessage, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String messageId = eventMessage.getId();
        try {
            log.info("开始处理事件，messageId: {}, topic: {}", messageId, eventMessage.getTopic());
            // 1. 消息状态同步（确保数据一致性）
            BaseEvent.EventMessage<T> syncedEventMessage = messageStateSynchronizer.syncMessageState(eventMessage);
            // 2. 标记消息为处理中状态
            messagePersistenceService.markAsProcessing(messageId);
            // 3. 调用具体的业务处理逻辑
            boolean success = processBusinessLogic(syncedEventMessage);
            if (success) {
                // 处理成功，确认消息并标记为完成
                channel.basicAck(deliveryTag, false);
                messagePersistenceService.markAsCompleted(messageId);
                log.info("事件处理成功，messageId: {}", messageId);
            } else {
                // 处理失败，进入重试逻辑
                handleRetry(eventMessage, channel, deliveryTag, "业务处理失败");
            }
        } catch (Exception e) {
            log.error("事件处理异常，messageId: {}", messageId, e);
            handleRetry(eventMessage, channel, deliveryTag, "处理异常: " + e.getMessage());
        }
    }

    /**
     * 抽象方法：具体的业务处理逻辑
     * 子类需要实现这个方法来处理具体的业务逻辑
     *
     * @param event 事件对象
     * @return 处理是否成功
     */
    protected abstract boolean processBusinessLogic(BaseEvent.EventMessage<T> event);

    /**
     * 通用的重试处理逻辑
     */
    protected void handleRetry(BaseEvent.EventMessage<T> eventMessage, Channel channel, long deliveryTag, String errorMessage) throws IOException {
        String messageId = eventMessage.getId();
        try {
            // 1. 评估是否可以重试（从数据库获取真实状态）
            IRetryStateManager.RetryContext retryContext = retryStateManager.evaluateRetry(eventMessage, errorMessage);
            switch (retryContext.getDecision()) {
                case RETRY:
                    // 可以重试，执行重试逻辑
                    boolean retryExecuted = retryStateManager.executeRetry(retryContext, eventMessage);
                    if (retryExecuted) {
                        log.info("重试执行成功，messageId: {}, retryCount: {}, delay: {}ms",
                                messageId, retryContext.getCurrentRetryCount(), retryContext.getDelayMillis());
                        channel.basicAck(deliveryTag, false);
                    } else {
                        log.error("重试执行失败，messageId: {}", messageId);
                        channel.basicNack(deliveryTag, false, false);
                    }
                    break;
                case FAIL:
                    // 超过最大重试次数，标记为最终失败
                    boolean marked = retryStateManager.markFinalFailure(retryContext);
                    if (marked) {
                        log.error("标记最终失败成功，messageId: {}, maxRetries: {}",
                                messageId, retryContext.getMaxRetries());
                    } else {
                        log.error("标记最终失败失败，messageId: {}", messageId);
                    }
                    channel.basicNack(deliveryTag, false, false);
                    break;
                case ERROR:
                    // 评估过程出错
                    log.error("重试评估出错，messageId: {}, error: {}",
                            messageId, retryContext.getErrorMessage());
                    channel.basicNack(deliveryTag, false, false);
                    messagePersistenceService.markAsFailed(messageId, retryContext.getErrorMessage());
                    break;
                default:
                    log.error("未知的重试决策，messageId: {}, decision: {}",
                            messageId, retryContext.getDecision());
                    channel.basicNack(deliveryTag, false, false);
                    break;
            }
        } catch (Exception e) {
            log.error("处理重试逻辑异常，messageId: {}", messageId, e);
            channel.basicNack(deliveryTag, false, false);
            messagePersistenceService.markAsFailed(messageId, "重试处理异常: " + e.getMessage());
        }
    }

    /**
     * 获取重试统计信息
     */
    protected IRetryStateManager.RetryStats getRetryStats(String messageId) {
        return retryStateManager.getRetryStats(messageId);
    }

    /**
     * 钩子方法：处理成功后的回调
     */
    protected void onProcessSuccess(BaseEvent.EventMessage<T> eventMessage) {
        // 子类可以重写这个方法来添加成功后的处理逻辑
        log.debug("事件处理成功回调，messageId: {}", eventMessage.getId());
    }

    /**
     * 钩子方法：处理失败后的回调
     */
    protected void onProcessFailure(BaseEvent.EventMessage<T> eventMessage, String errorMessage) {
        // 子类可以重写这个方法来添加失败后的处理逻辑
        log.debug("事件处理失败回调，messageId: {}, error: {}", eventMessage.getId(), errorMessage);
    }

}
