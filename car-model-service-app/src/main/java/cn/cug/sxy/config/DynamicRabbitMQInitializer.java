package cn.cug.sxy.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @version 1.0
 * @Date 2025/8/19 10:05
 * @Description 动态RabbitMQ初始化器
 * @Author jerryhotton
 */

@Slf4j
@Configuration
@EnableConfigurationProperties(RabbitMQConfigProperties.class)
public class DynamicRabbitMQInitializer {

    private final RabbitMQConfigProperties properties;
    private final RabbitAdmin rabbitAdmin;

    public DynamicRabbitMQInitializer(
            RabbitMQConfigProperties properties,
            RabbitAdmin rabbitAdmin) {
        this.properties = properties;
        this.rabbitAdmin = rabbitAdmin;
    }

    @PostConstruct
    public void registerDynamicExchangesAndQueues() {
        log.info("开始动态注册交换机和队列...");
        properties.getModules().forEach((moduleName, props) -> {
            // 创建并声明交换机
            DirectExchange mainExchange = ExchangeBuilder.directExchange(props.getExchange()).durable(true).build();
            DirectExchange retryExchange = ExchangeBuilder.directExchange(props.getRetryExchange()).durable(true).build();
            DirectExchange dlxExchange = ExchangeBuilder.directExchange(props.getDlxExchange()).durable(true).build();

            rabbitAdmin.declareExchange(mainExchange);
            rabbitAdmin.declareExchange(retryExchange);
            rabbitAdmin.declareExchange(dlxExchange);

            // 创建并声明队列
            Queue mainQueue = QueueBuilder.durable(props.getQueue())
                    .withArgument("x-dead-letter-exchange", props.getDlxExchange())
                    .withArgument("x-dead-letter-routing-key", props.getDlqRoutingKey())
                    .withArgument("x-message-ttl", props.getTtl().getMain())
                    .build();

            Queue retryQueue = QueueBuilder.durable(props.getRetryQueue())
                    .withArgument("x-dead-letter-exchange", props.getExchange())
                    .withArgument("x-dead-letter-routing-key", props.getRoutingKey())
                    .withArgument("x-message-ttl", props.getTtl().getRetry())
                    .build();

            Queue dlq = QueueBuilder.durable(props.getDlq()).build();

            rabbitAdmin.declareQueue(mainQueue);
            rabbitAdmin.declareQueue(retryQueue);
            rabbitAdmin.declareQueue(dlq);

            // 创建并声明绑定
            Binding mainBinding = BindingBuilder.bind(mainQueue).to(mainExchange).with(props.getRoutingKey());
            Binding retryBinding = BindingBuilder.bind(retryQueue).to(retryExchange).with(props.getRetryRoutingKey());
            Binding dlqBinding = BindingBuilder.bind(dlq).to(dlxExchange).with(props.getDlqRoutingKey());

            rabbitAdmin.declareBinding(mainBinding);
            rabbitAdmin.declareBinding(retryBinding);
            rabbitAdmin.declareBinding(dlqBinding);

            log.info("注册模块 {} 的队列: {}, {}, {}", moduleName, props.getQueue(), props.getRetryQueue(), props.getDlq());
        });

        log.info("动态注册完成");
    }

}
