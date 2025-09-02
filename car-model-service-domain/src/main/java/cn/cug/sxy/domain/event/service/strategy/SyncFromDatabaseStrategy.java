package cn.cug.sxy.domain.event.service.strategy;

import cn.cug.sxy.domain.event.service.IMessagePersistenceService;
import cn.cug.sxy.domain.event.service.MessageStateSynchronizer;
import cn.cug.sxy.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @Date 2025/8/15 10:36
 * @Description
 * @Author jerryhotton
 */

@Slf4j
@Component(value = "DATABASE_FIRST")
public class SyncFromDatabaseStrategy implements IMessageStateSyncStrategy {

    private final IMessagePersistenceService messagePersistenceService;

    public SyncFromDatabaseStrategy(IMessagePersistenceService messagePersistenceService) {
        this.messagePersistenceService = messagePersistenceService;
    }

    @Override
    public <T> BaseEvent.EventMessage<T> syncState(BaseEvent.EventMessage<T> eventMessage, IMessagePersistenceService.TaskInfo taskInfo) {
        String messageId = eventMessage.getId();
        try {
            // 分析需要修复的问题
            MessageStateSynchronizer.TaskFixInfo fixInfo = analyzeTaskProblems(taskInfo);
            if (!fixInfo.needsFix()) {
                log.debug("任务状态正常，无需修复，messageId: {}", messageId);
                return eventMessage;
            }
            // 一次性批量修复
            boolean fixed = messagePersistenceService.batchFixTaskState(messageId, fixInfo);
            if (fixed) {
                log.info("数据库状态一次性修复完成，messageId: {}, 修复内容: {}",
                        messageId, fixInfo.getFixDescription());
            } else {
                log.warn("数据库状态修复失败，messageId: {}", messageId);
            }

            return eventMessage;
        } catch (Exception e) {
            log.error("数据库状态同步异常，messageId: {}", messageId, e);
            return eventMessage;
        }
    }

    /**
     * 分析任务问题，准备修复信息
     */
    private MessageStateSynchronizer.TaskFixInfo analyzeTaskProblems(IMessagePersistenceService.TaskInfo taskInfo) {
        MessageStateSynchronizer.TaskFixInfo fixInfo = new MessageStateSynchronizer.TaskFixInfo();
        // 1. 检查空状态
        if (taskInfo.getState() == null || taskInfo.getState().trim().isEmpty()) {
            fixInfo.setNewState("PROCESSING");
            fixInfo.addFixReason("修复空状态为PROCESSING");
        }
        // 2. 检查负重试次数
        if (taskInfo.getRetryCount() != null && taskInfo.getRetryCount() < 0) {
            fixInfo.setNewRetryCount(0);
            fixInfo.addFixReason("修复负重试次数为0");
        }
        // 3. 检查失败任务缺少错误信息
        if ("FAILED".equals(taskInfo.getState()) &&
                (taskInfo.getErrorMessage() == null || taskInfo.getErrorMessage().trim().isEmpty())) {
            fixInfo.setNewErrorMessage("状态同步修复：补充错误信息");
            fixInfo.addFixReason("补充失败任务错误信息");
        }

        return fixInfo;
    }

}
