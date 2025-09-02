package cn.cug.sxy.domain.event.service;

import cn.cug.sxy.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @version 1.0
 * @Date 2025/8/14 15:45
 * @Description 重试状态管理器接口
 * @Author jerryhotton
 */

public interface IRetryStateManager {

    /**
     * 评估是否应该重试
     *
     * @param eventMessage 事件消息对象
     * @param errorMessage 错误信息
     * @return 重试上下文
     */
    <T> RetryContext evaluateRetry(BaseEvent.EventMessage<T> eventMessage, String errorMessage);

    /**
     * 执行重试
     * 包括更新数据库状态和发送延迟消息
     *
     * @param context 重试上下文
     * @param eventMessage 事件消息对象
     * @return 是否执行成功
     */
    <T> boolean executeRetry(RetryContext context, BaseEvent.EventMessage<T> eventMessage);

    /**
     * 标记最终失败
     *
     * @param context 重试上下文
     * @return 是否标记成功
     */
    boolean markFinalFailure(RetryContext context);

    /**
     * 获取重试统计信息
     *
     * @param messageId 消息ID
     * @return 重试统计
     */
    RetryStats getRetryStats(String messageId);

    /**
     * 重试统计信息
     */
    @Data
    @AllArgsConstructor
    class RetryStats {

        private final String messageId;
        private final int currentRetryCount;
        private final int maxRetries;
        private final String lastErrorMessage;
        private final String taskState;

    }

    /**
     * 重试决策结果
     */
    @Getter
    @AllArgsConstructor
    enum RetryDecision {

        RETRY("RETRY", "可以重试"),
        FAIL("FAILED", "超过重试次数，标记失败"),
        ERROR("ERROR", "处理异常");

        private final String code;
        private final String info;

    }

    /**
     * 重试上下文
     */
    @Getter
    @AllArgsConstructor
    class RetryContext {

        private final String messageId;
        private final String messageType;
        private final int currentRetryCount;
        private final int maxRetries;
        private final long delayMillis;
        private final String errorMessage;
        private final RetryDecision decision;

    }

}
