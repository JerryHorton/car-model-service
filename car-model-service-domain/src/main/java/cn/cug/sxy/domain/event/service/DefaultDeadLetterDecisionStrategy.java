package cn.cug.sxy.domain.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @Date 2025/8/15 14:33
 * @Description 默认死信处理决策策略
 * @Author jerryhotton
 */

@Slf4j
public class DefaultDeadLetterDecisionStrategy implements IDeadLetterDecisionStrategy {

    @Override
    public IDeadLetterActionExecutor.AlertLevel determineAlertLevel(IDeadLetterHandler.DeadLetterContext context) {
        // 最基础的决策逻辑：基于重试次数
        int retryCount = context.getRetryCount();
        if (retryCount >= 5) {
            return IDeadLetterActionExecutor.AlertLevel.CRITICAL;
        } else if (retryCount >= 3) {
            return IDeadLetterActionExecutor.AlertLevel.HIGH;
        } else if (retryCount >= 1) {
            return IDeadLetterActionExecutor.AlertLevel.MEDIUM;
        } else {
            return IDeadLetterActionExecutor.AlertLevel.LOW;
        }
    }

    @Override
    public String determineTargetQueue(IDeadLetterHandler.DeadLetterContext context) {
        // 最基础的决策逻辑：统一转发到通用队列
        return "dead-letter-archive-queue";
    }

    @Override
    public String determineCustomHandler(IDeadLetterHandler.DeadLetterContext context) {
        // 最基础的决策逻辑：返回通用处理器
        return "DefaultCustomHandler";
    }

    @Override
    public IDeadLetterActionExecutor.StorageType determineStorageType(IDeadLetterHandler.DeadLetterContext context) {
        // 最基础的决策逻辑：统一存储到死信表
        return IDeadLetterActionExecutor.StorageType.DEAD_LETTER_TABLE;
    }

    @Override
    public long determineRequeueDelay(IDeadLetterHandler.DeadLetterContext context) {
        // 最基础的决策逻辑：基于重试次数递增延迟
        int retryCount = context.getRetryCount();
        if (retryCount >= 10) {
            return -1; // 不允许重新入队
        } else if (retryCount >= 5) {
            return 10000; // 10秒
        } else if (retryCount >= 3) {
            return 5000; // 5秒
        } else {
            return 3000;  // 3秒
        }
    }

    @Override
    public Object determineCustomParams(IDeadLetterHandler.DeadLetterContext context, String handlerName) {
        // 最基础的决策逻辑：返回空参数
        return null;
    }

}
