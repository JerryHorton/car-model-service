package cn.cug.sxy.domain.event.service;

import cn.cug.sxy.domain.event.service.config.RetryStrategyConfig;
import cn.cug.sxy.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @Date 2025/8/14 16:00
 * @Description 通用重试状态管理器实现
 * @Author jerryhotton
 */

@Slf4j
@Service
public class RetryStateManager implements IRetryStateManager {

    private final IMessagePersistenceService messagePersistenceService;
    private final IEventPublisher eventPublisher;
    private final RetryStrategyConfig retryStrategyConfig;

    public RetryStateManager(IMessagePersistenceService messagePersistenceService,
                             IEventPublisher eventPublisher,
                             RetryStrategyConfig retryStrategyConfig) {
        this.messagePersistenceService = messagePersistenceService;
        this.eventPublisher = eventPublisher;
        this.retryStrategyConfig = retryStrategyConfig;
    }

    @Override
    public <T> RetryContext evaluateRetry(BaseEvent.EventMessage<T> eventMessage, String errorMessage) {
        String messageId = eventMessage.getId();
        String messageType = extractMessageType(eventMessage);
        try {
            // 从数据库获取真实的任务状态（而不是从消息对象中获取）
            IMessagePersistenceService.TaskInfo taskInfo = messagePersistenceService.getTaskInfo(messageId);
            if (taskInfo == null) {
                log.error("无法获取任务信息，messageId: {}", messageId);
                return new RetryContext(messageId, messageType, 0, 0, 0,
                        "无法获取任务信息", RetryDecision.ERROR);
            }
            // 获取重试策略
            RetryStrategyConfig.RetryPolicy retryPolicy = getRetryPolicy(messageType);
            int currentRetryCount = taskInfo.getRetryCount() != null ? taskInfo.getRetryCount() : 0;
            int maxRetries = retryPolicy.getMaxAttempts();
            // 判断是否可以重试
            if (retryPolicy.canRetry(currentRetryCount)) {
                int nextRetryCount = currentRetryCount + 1;
                long delayMillis = retryPolicy.getDelay(nextRetryCount);
                log.info("评估重试结果：可以重试，messageId: {}, currentRetry: {}, nextRetry: {}, delay: {}ms",
                        messageId, currentRetryCount, nextRetryCount, delayMillis);

                return new RetryContext(messageId, messageType, nextRetryCount, maxRetries,
                        delayMillis, errorMessage, RetryDecision.RETRY);
            } else {
                log.warn("评估重试结果：超过最大重试次数，messageId: {}, currentRetry: {}, maxRetries: {}",
                        messageId, currentRetryCount, maxRetries);

                return new RetryContext(messageId, messageType, currentRetryCount, maxRetries,
                        0, errorMessage, RetryDecision.FAIL);
            }
        } catch (Exception e) {
            log.error("评估重试异常，messageId: {}", messageId, e);
            return new RetryContext(messageId, messageType, 0, 0, 0,
                    "评估重试异常: " + e.getMessage(), RetryDecision.ERROR);
        }
    }

    @Override
    public <T> boolean executeRetry(RetryContext context, BaseEvent.EventMessage<T> eventMessage) {
        String messageId = context.getMessageId();
        try {
            // 1. 原子性地增加重试次数并更新状态
            String retryMessage = String.format("第%d次重试，延迟%dms，原因: %s",
                    context.getCurrentRetryCount(), context.getDelayMillis(), context.getErrorMessage());
            boolean dbUpdated = messagePersistenceService.incrementRetryCount(messageId, retryMessage);
            if (!dbUpdated) {
                log.error("更新重试状态失败，messageId: {}", messageId);
                return false;
            }
            // 2. 发送延迟重试消息（消息本身不包含重试信息）
            boolean retryScheduled = eventPublisher.publishDelayedEvent(eventMessage, context.getDelayMillis());
            if (retryScheduled) {
                log.info("执行重试成功，messageId: {}, retryCount: {}, delay: {}ms",
                        messageId, context.getCurrentRetryCount(), context.getDelayMillis());
                return true;
            } else {
                log.error("发送延迟重试消息失败，messageId: {}", messageId);
                // 回滚数据库状态
                messagePersistenceService.markAsFailed(messageId, "发送延迟重试消息失败");
                return false;
            }
        } catch (Exception e) {
            log.error("执行重试异常，messageId: {}", messageId, e);
            messagePersistenceService.markAsFailed(messageId, "执行重试异常: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean markFinalFailure(RetryContext context) {
        String messageId = context.getMessageId();
        String failureReason = String.format("超过最大重试次数(%d)，最后错误: %s",
                context.getMaxRetries(), context.getErrorMessage());
        try {
            boolean marked = messagePersistenceService.markAsFailed(messageId, failureReason);
            if (marked) {
                log.error("标记最终失败成功，messageId: {}, reason: {}", messageId, failureReason);
            } else {
                log.error("标记最终失败失败，messageId: {}", messageId);
            }
            return marked;
        } catch (Exception e) {
            log.error("标记最终失败异常，messageId: {}", messageId, e);
            return false;
        }
    }

    @Override
    public RetryStats getRetryStats(String messageId) {
        try {
            IMessagePersistenceService.TaskInfo taskInfo = messagePersistenceService.getTaskInfo(messageId);
            if (taskInfo == null) {
                return null;
            }
            // 获取重试策略以确定最大重试次数
            String messageType = getMessageTopicByMessageId(messageId);
            RetryStrategyConfig.RetryPolicy retryPolicy = getRetryPolicy(messageType);

            return new RetryStats(
                    messageId,
                    taskInfo.getRetryCount() != null ? taskInfo.getRetryCount() : 0,
                    retryPolicy.getMaxAttempts(),
                    taskInfo.getErrorMessage(),
                    taskInfo.getState()
            );
        } catch (Exception e) {
            log.error("获取重试统计异常，messageId: {}", messageId, e);
            return null;
        }
    }

    /**
     * 根据消息ID获取消息主题
     */
    private String getMessageTopicByMessageId(String messageId) {
        try {
            // 从数据库获取消息内容，解析出消息主题
            BaseEvent.EventMessage<?> eventMessage = messagePersistenceService.getMessageById(messageId);
            if (eventMessage != null) {
                // 实际上返回的是topic
                return extractMessageType(eventMessage);
            }
            return "default";
        } catch (Exception e) {
            log.error("获取消息主题异常，messageId: {}", messageId, e);
            return "default";
        }
    }

    /**
     * 提取消息类型 - 直接使用消息主题
     */
    private String extractMessageType(BaseEvent.EventMessage<?> eventMessage) {
        String topic = eventMessage.getTopic();
        if (StringUtils.isNotBlank(topic)) {
            return topic;
        }
        // 如果topic为空，使用默认类型
        return "default";
    }

    /**
     * 获取重试策略
     */
    private RetryStrategyConfig.RetryPolicy getRetryPolicy(String messageType) {
        // 直接使用消息主题获取对应的重试策略
        RetryStrategyConfig.RetryPolicy policy = retryStrategyConfig.getRetryPolicy(messageType);
        // 如果没有找到对应的策略，使用默认策略
        if (policy == null) {
            log.debug("未找到消息类型 {} 的重试策略，使用默认策略", messageType);
            return retryStrategyConfig.getDefaultPolicy();
        }

        return policy;
    }

}
