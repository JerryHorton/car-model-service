package cn.cug.sxy.config;

import cn.cug.sxy.domain.event.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @Date 2025/8/12 17:25
 * @Description RabbitMQ配置类
 * @Author jerryhotton
 */

@Slf4j
@Configuration
@EnableConfigurationProperties(RabbitMQConfigProperties.class)
public class RabbitMQConfig {

    private final RabbitMQConfigProperties properties;
    private final ITaskService taskService;

    public RabbitMQConfig(RabbitMQConfigProperties properties, ITaskService taskService) {
        this.properties = properties;
        this.taskService = taskService;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    /**
     * 动态注册交换机
     */
    @Bean
    public Map<String, Exchange> dynamicExchanges() {
        Map<String, Exchange> exchanges = new HashMap<>();
        properties.getModules().forEach((moduleName, props) -> {
            exchanges.put(moduleName + ".exchange",
                    ExchangeBuilder.directExchange(props.getExchange()).durable(true).build());
            exchanges.put(moduleName + ".retryExchange",
                    ExchangeBuilder.directExchange(props.getRetryExchange()).durable(true).build());
            exchanges.put(moduleName + ".dlxExchange",
                    ExchangeBuilder.directExchange(props.getDlxExchange()).durable(true).build());
        });
        return exchanges;
    }

    /**
     * 动态注册队列
     */
    @Bean
    @DependsOn("dynamicExchanges")
    public Map<String, Queue> dynamicQueues() {
        Map<String, Queue> queues = new HashMap<>();
        properties.getModules().forEach((moduleName, props) -> {
            queues.put(moduleName + ".queue",
                    QueueBuilder.durable(props.getQueue())
                            .withArgument("x-dead-letter-exchange", props.getDlxExchange())
                            .withArgument("x-dead-letter-routing-key", props.getDlqRoutingKey())
                            .withArgument("x-message-ttl", props.getTtl().getMain())
                            .build());

            queues.put(moduleName + ".retryQueue",
                    QueueBuilder.durable(props.getRetryQueue())
                            .withArgument("x-dead-letter-exchange", props.getExchange())
                            .withArgument("x-dead-letter-routing-key", props.getRoutingKey())
                            .withArgument("x-message-ttl", props.getTtl().getRetry())
                            .build());

            queues.put(moduleName + ".dlq",
                    QueueBuilder.durable(props.getDlq()).build());
        });
        return queues;
    }

    /**
     * 动态注册绑定
     */
    @Bean
    @DependsOn({"dynamicExchanges", "dynamicQueues"})
    public Map<String, Binding> dynamicBindings(Map<String, Exchange> dynamicExchanges, Map<String, Queue> dynamicQueues) {
        Map<String, Binding> bindings = new HashMap<>();
        properties.getModules().forEach((moduleName, props) -> {
            bindings.put(moduleName + ".mainBinding",
                    BindingBuilder.bind(dynamicQueues.get(moduleName + ".queue"))
                            .to((DirectExchange) dynamicExchanges.get(moduleName + ".exchange"))
                            .with(props.getRoutingKey()));

            bindings.put(moduleName + ".retryBinding",
                    BindingBuilder.bind(dynamicQueues.get(moduleName + ".retryQueue"))
                            .to((DirectExchange) dynamicExchanges.get(moduleName + ".retryExchange"))
                            .with(props.getRetryRoutingKey()));

            bindings.put(moduleName + ".dlqBinding",
                    BindingBuilder.bind(dynamicQueues.get(moduleName + ".dlq"))
                            .to((DirectExchange) dynamicExchanges.get(moduleName + ".dlxExchange"))
                            .with(props.getDlqRoutingKey()));
        });
        return bindings;
    }

    /**
     * RabbitTemplate配置（增强持久化保证）
     * 结合RabbitMQ原生持久化和应用层持久化
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        // 设置JSON消息转换器，并默认设置消息为持久化
        template.setMessageConverter(new Jackson2JsonMessageConverter() {
            @Override
            protected Message createMessage(Object object, MessageProperties messageProperties) {
                // 设置消息为RabbitMQ原生持久化
                messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return super.createMessage(object, messageProperties);
            }
        });
        // 开启发送确认模式
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.debug("消息发送确认成功，correlationData: {}", correlationData);
                // 更新数据库中的任务状态为已发布
                if (correlationData != null && correlationData.getId() != null) {
                    updateTaskToPublished(correlationData.getId());
                }
            } else {
                log.error("消息发送确认失败，correlationData: {}, cause: {}", correlationData, cause);
                // 更新数据库中的任务状态为失败
                if (correlationData != null && correlationData.getId() != null) {
                    updateTaskToFailed(correlationData.getId(), "发送确认失败: " + cause);
                }
            }
        });
        // 开启返回确认模式
        template.setReturnsCallback(returned -> {
            log.error("消息路由失败，message: {}, replyCode: {}, replyText: {}, exchange: {}, routingKey: {}",
                    returned.getMessage(), returned.getReplyCode(), returned.getReplyText(),
                    returned.getExchange(), returned.getRoutingKey());
            // 从消息中提取messageId并更新任务状态
            String messageId = extractMessageIdFromMessage(returned.getMessage());
            if (messageId != null) {
                updateTaskToFailed(messageId, "消息路由失败: " + returned.getReplyText());
            }
        });
        // 设置强制路由（确保消息能被路由到队列）
        template.setMandatory(true);

        return template;
    }

    /**
     * 更新任务状态为已发布
     */
    private void updateTaskToPublished(String messageId) {
        try {
            taskService.publishTask(messageId);
            log.info("任务发布成功，messageId: {}", messageId);
        } catch (Exception e) {
            log.error("更新任务发布状态失败，messageId: {}", messageId, e);
        }
    }

    /**
     * 更新任务状态为失败
     */
    private void updateTaskToFailed(String messageId, String errorMessage) {
        try {
            // 这里需要注入TaskService，暂时用日志记录
            taskService.failTask(messageId);
            log.error("任务发布失败，messageId: {}, error: {}", messageId, errorMessage);
        } catch (Exception e) {
            log.error("更新任务失败状态失败，messageId: {}", messageId, e);
        }
    }

    /**
     * 从消息中提取messageId
     */
    private String extractMessageIdFromMessage(Message message) {
        try {
            MessageProperties messageProperties = message.getMessageProperties();
            return messageProperties.getMessageId();
        } catch (Exception e) {
            log.error("提取messageId失败", e);
        }
        return null;
    }

    /**
     * 消息监听器容器工厂
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        // 设置并发消费者数量
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(5);
        // 设置预取数量
        factory.setPrefetchCount(10);
        // 设置确认模式
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        return factory;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(properties.getHost());
        connectionFactory.setPort(properties.getPort());
        connectionFactory.setUsername(properties.getUsername());
        connectionFactory.setPassword(properties.getPassword());
        return connectionFactory;
    }

    /**
     * 默认死信动作执行器
     * 当没有其他实现时使用，或者通过配置指定使用默认实现
     */
    @Bean
    @ConditionalOnProperty(name = "deadletter.executor.type", havingValue = "default", matchIfMissing = true)
    public IDeadLetterActionExecutor defaultDeadLetterActionExecutor(
            RabbitTemplate rabbitTemplate,
            IEventPublisher eventPublisher,
            IMessagePersistenceService messagePersistenceService) {

        log.info("配置默认死信动作执行器");
        return new DefaultDeadLetterActionExecutor(rabbitTemplate, eventPublisher, messagePersistenceService);
    }

    /**
     * 默认死信决策策略
     * 当没有其他实现时使用，或者通过配置指定使用默认策略
     */
    @Bean
    @ConditionalOnProperty(name = "deadletter.decision.strategy", havingValue = "default", matchIfMissing = true)
    public IDeadLetterDecisionStrategy defaultDeadLetterDecisionStrategy() {
        log.info("配置默认死信决策策略");
        return new DefaultDeadLetterDecisionStrategy();
    }

}
