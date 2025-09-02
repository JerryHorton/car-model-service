package cn.cug.sxy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @version 1.0
 * @Date 2025/6/11 15:12
 * @Description
 * @Author jerryhotton
 */

@Data
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitMQConfigProperties {

    private String host;

    private Integer port;

    private String username;

    private String password;

    private Map<String, MqModuleProperties> modules;

    @Data
    public static class MqModuleProperties {
        private String exchange;
        private String retryExchange;
        private String dlxExchange;

        private String queue;
        private String retryQueue;
        private String dlq;

        private String routingKey;
        private String retryRoutingKey;
        private String dlqRoutingKey;

        private Ttl ttl;

        @Data
        public static class Ttl {
            private long main;
            private long retry;
        }
    }

}
