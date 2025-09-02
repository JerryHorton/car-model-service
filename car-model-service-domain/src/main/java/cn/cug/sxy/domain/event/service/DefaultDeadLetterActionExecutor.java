package cn.cug.sxy.domain.event.service;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @version 1.0
 * @Date 2025/8/15 14:28
 * @Description 默认死信处理动作执行器实现
 * @Author jerryhotton
 */

@Slf4j
public class DefaultDeadLetterActionExecutor extends AbstractDeadLetterActionExecutor {

    public DefaultDeadLetterActionExecutor(RabbitTemplate rabbitTemplate,
                                           IEventPublisher eventPublisher,
                                           IMessagePersistenceService messagePersistenceService) {
        super(rabbitTemplate, eventPublisher, messagePersistenceService);
    }

    @Override
    public boolean forwardToQueue(IDeadLetterHandler.DeadLetterContext context, String targetQueue, Channel channel, long deliveryTag) throws IOException {
        try {
            // 最基础的实现：直接转发到指定队列
            rabbitTemplate.convertAndSend(targetQueue, context.getEventMessage());
            channel.basicAck(deliveryTag, false);
            log.info("消息已转发到队列: {}, messageId: {}", targetQueue, context.getMessageId());

            return true;
        } catch (Exception e) {
            log.error("转发消息失败，messageId: {}, targetQueue: {}",
                    context.getMessageId(), targetQueue, e);
            return false;
        }
    }

    @Override
    public boolean sendAlert(IDeadLetterHandler.DeadLetterContext context, AlertLevel alertLevel) {
        // 最基础的实现：只记录日志
        String alertMessage = String.format(
                "[%s] 死信告警 - 消息ID: %s, 主题: %s, 失败原因: %s",
                alertLevel.getDescription(),
                context.getMessageId(),
                context.getMessageTopic(),
                context.getFailureReason()
        );
        log.warn("死信告警: {}", alertMessage);

        return true;
    }

    @Override
    public boolean storeToDatabase(IDeadLetterHandler.DeadLetterContext context, StorageType storageType) {
        try {
            // 最基础的实现：更新任务状态为失败
            messagePersistenceService.markAsFailed(context.getMessageId(),
                    "死信处理：" + context.getFailureReason());
            log.info("死信消息已标记为失败，messageId: {}, storageType: {}",
                    context.getMessageId(), storageType);

            return true;
        } catch (Exception e) {
            log.error("存储死信消息失败，messageId: {}, storageType: {}",
                    context.getMessageId(), storageType, e);
            return false;
        }
    }

    @Override
    public boolean requeue(IDeadLetterHandler.DeadLetterContext context, Channel channel, long deliveryTag, long delayMillis) throws IOException {
        try {
            // 检查重试次数限制
            if (context.getRetryCount() >= 10) {
                log.warn("重试次数过多，拒绝重新入队，messageId: {}", context.getMessageId());
                return false;
            }
            if (delayMillis > 0) {
                // 延迟重新入队
                eventPublisher.publishDelayedEvent(context.getEventMessage(), delayMillis);
                channel.basicAck(deliveryTag, false);
            } else {
                // 立即重新入队
                channel.basicNack(deliveryTag, false, true);
            }
            log.info("死信消息重新入队，messageId: {}, delay: {}ms",
                    context.getMessageId(), delayMillis);

            return true;
        } catch (Exception e) {
            log.error("重新入队失败，messageId: {}", context.getMessageId(), e);
            return false;
        }
    }

    @Override
    public boolean customHandle(IDeadLetterHandler.DeadLetterContext context, String handlerName, Object customParams) {
        // 最基础的实现：记录日志
        log.info("自定义死信处理，messageId: {}, handler: {}, params: {}",
                context.getMessageId(), handlerName, customParams);
        // 默认返回成功，具体逻辑由使用者实现
        return true;
    }

}
