package cn.cug.sxy.domain.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @version 1.0
 * @Date 2025/8/15 13:37
 * @Description 死信处理动作执行器实现
 * @Author jerryhotton
 */

@Slf4j
public abstract class AbstractDeadLetterActionExecutor implements IDeadLetterActionExecutor {

    protected final RabbitTemplate rabbitTemplate;
    protected final IEventPublisher eventPublisher;
    protected final IMessagePersistenceService messagePersistenceService;

    public AbstractDeadLetterActionExecutor(
            RabbitTemplate rabbitTemplate,
            IEventPublisher eventPublisher,
            IMessagePersistenceService messagePersistenceService) {
        this.rabbitTemplate = rabbitTemplate;
        this.eventPublisher = eventPublisher;
        this.messagePersistenceService = messagePersistenceService;
    }

}
