package cn.cug.sxy.domain.event.service;

import cn.cug.sxy.types.event.BaseEvent;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @version 1.0
 * @Date 2025/8/15 15:13
 * @Description 死信队列处理器
 * @Author jerryhotton
 */

@Slf4j
@Component
@ConditionalOnProperty(name = "spring.rabbitmq.deadletter.handler.type", havingValue = "dynamic")
public class DeadLetterQueueHandler {

    private final IDeadLetterManager deadLetterManager;

    public DeadLetterQueueHandler(IDeadLetterManager deadLetterManager) {
        this.deadLetterManager = deadLetterManager;
    }

    /**
     * 动态监听多个死信队列
     * 通过配置文件指定队列名称
     */
    @RabbitListener(queues = {"file.upload.dlq", "default.dlq"})
    public void handleDeadLetterMessage(BaseEvent.EventMessage<?> eventMessage, Message message, Channel channel) throws IOException {
        String queueName = message.getMessageProperties().getConsumerQueue();
        try {
            log.warn("收到死信消息，messageId: {}, topic: {}, queue: {}",
                    eventMessage.getId(), eventMessage.getTopic(), queueName);
            // 委托给死信管理器处理
            deadLetterManager.handleDeadLetter(eventMessage, message, channel);
            log.info("死信消息处理完成，messageId: {}, queue: {}", eventMessage.getId(), queueName);
        } catch (Exception e) {
            log.error("死信消息处理异常，messageId: {}, queue: {}", eventMessage.getId(), queueName, e);
            // 确认消息，避免死信消息无限循环
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            channel.basicAck(deliveryTag, false);
        }
    }

}
