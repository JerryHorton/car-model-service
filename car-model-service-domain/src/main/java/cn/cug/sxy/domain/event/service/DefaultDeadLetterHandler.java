package cn.cug.sxy.domain.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @Date 2025/8/15 15:06
 * @Description 默认死信处理器
 * @Author jerryhotton
 */

@Slf4j
@Component
public class DefaultDeadLetterHandler implements IDeadLetterHandler {

    @Override
    public DeadLetterAction handleDeadLetter(DeadLetterContext context) {
        String messageId = context.getMessageId();
        String messageTopic = context.getMessageTopic();
        String failureReason = context.getFailureReason();
        int retryCount = context.getRetryCount();
        long failureDuration = context.getFailureDuration();
        log.warn("默认死信处理：messageId={}, topic={}, retryCount={}, duration={}ms, reason={}",
                messageId, messageTopic, retryCount, failureDuration, failureReason);
        // 根据重试次数和失败持续时间决定处理策略
        if (retryCount >= 5) {
            // 重试次数过多，发送告警
            log.error("消息重试次数过多，发送告警：messageId={}, retryCount={}", messageId, retryCount);
            return DeadLetterAction.SEND_ALERT;
        }
        if (failureDuration > 3600000) { // 1小时
            // 失败时间过长，发送告警
            log.error("消息失败时间过长，发送告警：messageId={}, duration={}ms", messageId, failureDuration);
            return DeadLetterAction.SEND_ALERT;
        }
        // 默认策略：存储到数据库
        log.info("默认死信处理，存储到数据库：messageId={}", messageId);

        return DeadLetterAction.STORE_TO_DATABASE;
    }

    @Override
    public boolean supports(String messageTopic) {
        // 默认处理器支持所有消息类型
        return true;
    }

    @Override
    public int getPriority() {
        // 默认处理器优先级最低
        return Integer.MAX_VALUE;
    }

    @Override
    public String getHandlerName() {
        return "DefaultDeadLetterHandler";
    }

}
