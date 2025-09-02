package cn.cug.sxy.domain.event.service;

import cn.cug.sxy.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/12 15:41
 * @Description 事件发布服务实现
 * @Author jerryhotton
 */

@Slf4j
@Service
public class EventPublisher implements IEventPublisher {

    @Value("${spring.rabbitmq.modules.default.exchange}")
    private String DEFAULT_EXCHANGE;

    @Value("${spring.rabbitmq.modules.default.exchange}")
    private String DEFAULT_RETRY_EXCHANGE;

    @Value("${spring.rabbitmq.modules.default.routing-key}")
    private String DEFAULT_ROUTING_KEY;

    @Value("${spring.rabbitmq.modules.default.retry-routing-key}")
    private String DEFAULT_RETRY_ROUTING_KEY;

    private final RabbitTemplate rabbitTemplate;
    private final IMessagePersistenceService messagePersistenceService;

    public EventPublisher(
            RabbitTemplate rabbitTemplate,
            IMessagePersistenceService messagePersistenceService) {
        this.rabbitTemplate = rabbitTemplate;
        this.messagePersistenceService = messagePersistenceService;
    }

    @Override
    public <T> boolean publishEvent(BaseEvent.EventMessage<T> eventMessage) {
        return publishEvent(eventMessage, DEFAULT_EXCHANGE, DEFAULT_ROUTING_KEY);
    }

    @Override
    public <T> boolean publishEvent(BaseEvent.EventMessage<T> eventMessage, String exchange, String routingKey) {
        try {
            log.info("发布持久化事件，messageId: {}, topic: {}, exchange: {}, routingKey: {}",
                    eventMessage.getId(), eventMessage.getTopic(), exchange, routingKey);
            // 1. 先持久化消息到数据库（事务性保证）
            boolean persisted = messagePersistenceService.persistMessage(eventMessage);
            if (!persisted) {
                log.error("消息持久化失败，messageId: {}", eventMessage.getId());
                return false;
            }
            // 2. 发送消息到队列（带确认机制）
            CorrelationData correlationData = new CorrelationData(eventMessage.getId());
            rabbitTemplate.convertAndSend(exchange, routingKey, eventMessage, correlationData);
            log.info("事件发布成功，messageId: {}, topic: {}", eventMessage.getId(), eventMessage.getTopic());

            return true;
        } catch (Exception e) {
            log.error("事件发布失败，messageId: {}, topic: {}", eventMessage.getId(), eventMessage.getTopic(), e);
            // 发布失败时，标记消息为失败状态
            messagePersistenceService.markAsFailed(eventMessage.getId(), "发布异常: " + e.getMessage());

            return false;
        }
    }

    @Override
    public <T> boolean publishDelayedEvent(BaseEvent.EventMessage<T> eventMessage, long delayMillis) {
        try {
            log.info("发布延迟事件，messageId: {}, topic: {}, delay: {}ms",
                    eventMessage.getId(), eventMessage.getTopic(), delayMillis);
            // 使用重试交换机发送延迟消息
            rabbitTemplate.convertAndSend(
                    DEFAULT_RETRY_EXCHANGE,
                    DEFAULT_RETRY_ROUTING_KEY,
                    eventMessage,
                    message -> {
                        // 设置消息TTL
                        message.getMessageProperties().setExpiration(String.valueOf(delayMillis));
                        return message;
                    }
            );
            log.info("延迟事件发布成功，messageId: {}, topic: {}", eventMessage.getId(), eventMessage.getTopic());

            return true;
        } catch (Exception e) {
            log.error("延迟事件发布失败，messageId: {}, topic: {}", eventMessage.getId(), eventMessage.getTopic(), e);

            return false;
        }
    }

    @Override
    public <T> boolean publishPersistentEvent(BaseEvent.EventMessage<T> eventMessage) {
        // 使用默认的交换机和路由键发布持久化事件
        return publishEvent(eventMessage, DEFAULT_EXCHANGE, DEFAULT_ROUTING_KEY);
    }

    @Override
    public <T> int publishBatchEvents(List<BaseEvent.EventMessage<T>> eventMessages) {
        int successCount = 0;
        for (BaseEvent.EventMessage<T> eventMessage : eventMessages) {
            try {
                if (publishEvent(eventMessage)) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("批量发布事件失败，messageId: {}", eventMessage.getId(), e);
            }
        }
        log.info("批量发布事件完成，总数: {}, 成功: {}", eventMessages.size(), successCount);

        return successCount;
    }

    @Override
    public boolean republishFailedEvent(String messageId) {
        try {
            log.info("重新发布失败事件，messageId: {}", messageId);
            // 1. 从数据库获取消息
            BaseEvent.EventMessage<?> eventMessage = messagePersistenceService.getMessageById(messageId);
            if (eventMessage == null) {
                log.error("未找到消息，messageId: {}", messageId);
                return false;
            }
            // 2. 重新发布事件
            boolean published = publishEvent(eventMessage);
            if (published) {
                log.info("重新发布事件成功，messageId: {}", messageId);
            } else {
                log.error("重新发布事件失败，messageId: {}", messageId);
            }

            return published;
        } catch (Exception e) {
            log.error("重新发布事件异常，messageId: {}", messageId, e);
            return false;
        }
    }

}
