package cn.cug.sxy.domain.event.service.strategy;

import cn.cug.sxy.domain.event.model.valobj.TaskState;
import cn.cug.sxy.domain.event.service.IMessagePersistenceService;
import cn.cug.sxy.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @Date 2025/8/15 10:41
 * @Description 智能合并同步策略
 * @Author jerryhotton
 */

@Slf4j
@Component(value = "SMART_MERGE")
public class SmartMergeSyncStrategy implements IMessageStateSyncStrategy {

    private final SyncFromDatabaseStrategy syncFromDatabaseStrategy;

    public SmartMergeSyncStrategy(
            SyncFromDatabaseStrategy syncFromDatabaseStrategy) {
        this.syncFromDatabaseStrategy = syncFromDatabaseStrategy;
    }

    @Override
    public <T> BaseEvent.EventMessage<T> syncState(BaseEvent.EventMessage<T> eventMessage, IMessagePersistenceService.TaskInfo taskInfo) {
        String messageId = eventMessage.getId();
        String currentState = taskInfo.getState();
        try {
            // 根据当前状态智能选择同步策略
            if (currentState == null || TaskState.CREATE.getCode().equals(currentState) || TaskState.PROCESSING.getCode().equals(currentState)) {
                // 对于初始状态或处理中状态，以数据库为准进行修复
                return syncFromDatabaseStrategy.syncState(eventMessage, taskInfo);
            } else if (TaskState.FAILED.getCode().equals(currentState) && taskInfo.getRetryCount() != null && taskInfo.getRetryCount() > 0) {
                // 对于有重试记录的失败任务，检查是否需要重置
                log.info("智能同步：失败任务有重试记录，保持当前状态，messageId: {}", messageId);
                return eventMessage;
            } else if (TaskState.COMPLETED.getCode().equals(currentState)) {
                // 已完成的任务通常不需要同步
                log.debug("智能同步：任务已完成，无需同步，messageId: {}", messageId);
                return eventMessage;
            } else {
                // 其他情况，以数据库为准
                return syncFromDatabaseStrategy.syncState(eventMessage, taskInfo);
            }
        } catch (Exception e) {
            log.error("智能合并同步异常，messageId: {}", messageId, e);
            return syncFromDatabaseStrategy.syncState(eventMessage, taskInfo); // 降级到数据库同步
        }
    }

}
