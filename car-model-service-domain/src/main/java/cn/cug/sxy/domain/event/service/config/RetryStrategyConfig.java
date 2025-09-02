package cn.cug.sxy.domain.event.service.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @Date 2025/8/14 17:08
 * @Description 重试策略配置
 * @Author jerryhotton
 */

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "task-retry")
public class RetryStrategyConfig {

    /**
     * 重试策略映射 - 直接使用配置文件中的结构
     * key: 消息主题或类型标识
     * value: 对应的重试策略
     */
    private Map<String, RetryPolicy> strategies = new HashMap<>();

    /**
     * 根据消息主题获取重试策略
     */
    public RetryPolicy getRetryPolicy(String messageTopic) {
        if (StringUtils.isBlank(messageTopic)) {
            return getDefaultPolicy();
        }
        // 直接从配置的策略映射中获取
        RetryPolicy policy = strategies.get(messageTopic);
        if (policy != null) {
            return policy;
        }
        // 如果没有找到精确匹配，返回默认策略
        return getDefaultPolicy();
    }

    /**
     * 获取默认重试策略
     */
    public RetryPolicy getDefaultPolicy() {
        return strategies.getOrDefault("default", new RetryPolicy());
    }

    @Data
    public static class RetryPolicy {
        /**
         * 最大重试次数
         */
        private int maxAttempts = 3;
        /**
         * 重试延迟时间数组（毫秒）
         */
        private List<Long> delays = List.of(60000L, 300000L, 900000L);
        /**
         * 退避倍数
         */
        private double backoffMultiplier = 2.0;
        /**
         * 最大延迟时间（毫秒）
         */
        private long maxDelay = 1800000L; // 30分钟

        /**
         * 获取指定重试次数的延迟时间
         */
        public long getDelay(int retryCount) {
            if (retryCount <= 0) {
                return 0;
            }
            // 如果重试次数超过预定义的延迟数组长度，使用最后一个延迟时间
            if (retryCount > delays.size()) {
                long baseDelay = delays.get(delays.size() - 1);
                // 应用退避倍数
                long calculatedDelay = (long) (baseDelay * Math.pow(backoffMultiplier, retryCount - delays.size()));
                return Math.min(calculatedDelay, maxDelay);
            }

            return delays.get(retryCount - 1);
        }

        /**
         * 是否可以重试
         */
        public boolean canRetry(int currentRetryCount) {
            return currentRetryCount < maxAttempts;
        }
    }

}
