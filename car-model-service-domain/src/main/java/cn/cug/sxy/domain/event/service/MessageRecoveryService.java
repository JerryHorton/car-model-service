package cn.cug.sxy.domain.event.service;

import cn.cug.sxy.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/13 11:01
 * @Description 消息恢复服务实现
 * @Author jerryhotton
 */

@Slf4j
@Service
public class MessageRecoveryService implements IMessageRecoveryService {

    private final IMessagePersistenceService messagePersistenceService;
    private final IEventPublisher eventPublisher;

    public MessageRecoveryService(
            IMessagePersistenceService messagePersistenceService,
            IEventPublisher eventPublisher) {
        this.messagePersistenceService = messagePersistenceService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public int recoverUnpublishedMessages() {
        try {
            log.info("开始恢复未发布的消息");
            // 获取未发布的消息（限制100条，避免一次处理过多）
            List<BaseEvent.EventMessage<?>> unpublishedMessages = messagePersistenceService.getUnpublishedMessages(100);
            int recoveredCount = 0;
            for (BaseEvent.EventMessage<?> event : unpublishedMessages) {
                try {
                    boolean published = eventPublisher.publishEvent(event);
                    if (published) {
                        recoveredCount++;
                        log.info("恢复未发布消息成功，messageId: {}", event.getId());
                    } else {
                        log.error("恢复未发布消息失败，messageId: {}", event.getId());
                        messagePersistenceService.markAsFailed(event.getId(), "恢复发布失败");
                    }
                } catch (Exception e) {
                    log.error("恢复未发布消息异常，messageId: {}", event.getId(), e);
                    messagePersistenceService.markAsFailed(event.getId(), "恢复发布异常: " + e.getMessage());
                }
            }
            log.info("恢复未发布消息完成，总数: {}, 成功: {}", unpublishedMessages.size(), recoveredCount);

            return recoveredCount;
        } catch (Exception e) {
            log.error("恢复未发布消息异常", e);
            return 0;
        }
    }

    @Override
    public int recoverTimeoutProcessingMessages(int timeoutSeconds) {
        try {
            log.info("开始恢复超时处理中的消息，超时秒数: {}", timeoutSeconds);
            // 获取超时的处理中消息
            List<BaseEvent.EventMessage<?>> timeoutMessages = messagePersistenceService.getTimeoutProcessingMessages(timeoutSeconds, 50);
            int recoveredCount = 0;
            for (BaseEvent.EventMessage<?> event : timeoutMessages) {
                try {
                    // 重新发布超时的消息
                    boolean published = eventPublisher.publishEvent(event);
                    if (published) {
                        recoveredCount++;
                        log.info("恢复超时消息成功，messageId: {}", event.getId());
                    } else {
                        log.error("恢复超时消息失败，messageId: {}", event.getId());
                        messagePersistenceService.markAsFailed(event.getId(), "恢复超时消息失败");
                    }
                } catch (Exception e) {
                    log.error("恢复超时消息异常，messageId: {}", event.getId(), e);
                    messagePersistenceService.markAsFailed(event.getId(), "恢复超时消息异常: " + e.getMessage());
                }
            }
            log.info("恢复超时处理中消息完成，总数: {}, 成功: {}", timeoutMessages.size(), recoveredCount);

            return recoveredCount;
        } catch (Exception e) {
            log.error("恢复超时处理中消息异常", e);
            return 0;
        }
    }

    @Override
    public int retryFailedMessages() {
        try {
            log.info("开始重试失败的消息");
            // 获取可重试的失败消息
            List<BaseEvent.EventMessage<?>> retryableMessages = messagePersistenceService.getRetryableFailedMessages(50);
            int retriedCount = 0;
            for (BaseEvent.EventMessage<?> event : retryableMessages) {
                try {
                    // 重新发布失败的消息
                    boolean published = eventPublisher.publishEvent(event);
                    if (published) {
                        retriedCount++;
                        log.info("重试失败消息成功，messageId: {}", event.getId());
                    } else {
                        log.error("重试失败消息失败，messageId: {}", event.getId());
                    }
                } catch (Exception e) {
                    log.error("重试失败消息异常，messageId: {}", event.getId(), e);
                }
            }
            log.info("重试失败消息完成，总数: {}, 成功: {}", retryableMessages.size(), retriedCount);

            return retriedCount;
        } catch (Exception e) {
            log.error("重试失败消息异常", e);
            return 0;
        }
    }

    @Override
    public int fullMessageRecovery() {
        try {
            log.info("开始全面消息恢复");
            int totalRecovered = 0;
            // 1. 恢复未发布的消息
            totalRecovered += recoverUnpublishedMessages();
            // 2. 恢复超时处理中的消息（1分钟超时）
            totalRecovered += recoverTimeoutProcessingMessages(60);
            // 3. 重试失败的消息
            totalRecovered += retryFailedMessages();
            log.info("全面消息恢复完成，总共恢复: {}", totalRecovered);

            return totalRecovered;
        } catch (Exception e) {
            log.error("全面消息恢复异常", e);
            return 0;
        }
    }

    @Override
    public String checkMessageConsistency() {
        try {
            log.info("开始检查消息一致性");
            StringBuilder report = new StringBuilder();
            report.append("=== 消息一致性检查报告 ===\n");
            report.append("检查时间: ").append(new java.util.Date()).append("\n\n");
            // 检查未发布的消息
            List<BaseEvent.EventMessage<?>> unpublishedMessages = messagePersistenceService.getUnpublishedMessages(1000);
            report.append("未发布消息数量: ").append(unpublishedMessages.size()).append("\n");
            // 检查超时处理中的消息
            List<BaseEvent.EventMessage<?>> timeoutMessages = messagePersistenceService.getTimeoutProcessingMessages(30, 1000);
            report.append("超时处理中消息数量: ").append(timeoutMessages.size()).append("\n");
            // 检查可重试的失败消息
            List<BaseEvent.EventMessage<?>> retryableMessages = messagePersistenceService.getRetryableFailedMessages(1000);
            report.append("可重试失败消息数量: ").append(retryableMessages.size()).append("\n");
            // 计算总的问题消息数量
            int totalProblems = unpublishedMessages.size() + timeoutMessages.size() + retryableMessages.size();
            report.append("\n总问题消息数量: ").append(totalProblems).append("\n");
            if (totalProblems > 0) {
                report.append("建议: 运行消息恢复任务\n");
            } else {
                report.append("状态: 消息状态正常\n");
            }
            report.append("=== 检查完成 ===");
            String reportStr = report.toString();
            log.info("消息一致性检查完成:\n{}", reportStr);

            return reportStr;
        } catch (Exception e) {
            log.error("检查消息一致性异常", e);
            return "检查消息一致性失败: " + e.getMessage();
        }
    }

}
