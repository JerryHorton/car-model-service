package cn.cug.sxy.domain.event.service;

import cn.cug.sxy.domain.event.service.strategy.IMessageStateSyncStrategy;
import cn.cug.sxy.types.event.BaseEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @Date 2025/8/15 09:39
 * @Description 消息状态同步器实现
 * @Author jerryhotton
 */

@Slf4j
@Service
public class MessageStateSynchronizer implements IMessageStateSynchronizer {

    private final IMessagePersistenceService messagePersistenceService;
    private SyncStrategy currentStrategy = SyncStrategy.DATABASE_FIRST;
    private final Map<String, IMessageStateSyncStrategy> syncStrategyMap;

    public MessageStateSynchronizer(
            IMessagePersistenceService messagePersistenceService,
            Map<String, IMessageStateSyncStrategy> syncStrategyMap) {
        this.messagePersistenceService = messagePersistenceService;
        this.syncStrategyMap = syncStrategyMap;
    }

    @Override
    public <T> BaseEvent.EventMessage<T> syncMessageState(BaseEvent.EventMessage<T> eventMessage) {
        String messageId = eventMessage.getId();
        try {
            // 从数据库获取最新状态
            IMessagePersistenceService.TaskInfo taskInfo = messagePersistenceService.getTaskInfo(messageId);
            if (taskInfo == null) {
                log.warn("无法获取任务信息进行同步，messageId: {}", messageId);
                return eventMessage;
            }
            // 根据同步策略处理
            IMessageStateSyncStrategy strategy = syncStrategyMap.get(currentStrategy.name());
            if (strategy == null) {
                log.warn("未配置同步策略，messageId: {}", messageId);
                return eventMessage;
            }
            return strategy.syncState(eventMessage, taskInfo);
        } catch (Exception e) {
            log.error("同步消息状态异常，messageId: {}", messageId, e);
            return eventMessage;
        }
    }

    @Override
    public <T> ConsistencyCheckResult checkConsistency(BaseEvent.EventMessage<T> eventMessage) {
        String messageId = eventMessage.getId();
        try {
            IMessagePersistenceService.TaskInfo taskInfo = messagePersistenceService.getTaskInfo(messageId);
            if (taskInfo == null) {
                return new ConsistencyCheckResult(false, messageId, "UNKNOWN", "NOT_FOUND",
                        "数据库中未找到对应任务");
            }
            // 检查状态一致性
            // 注意：由于消息本身不再携带状态信息，这里主要检查消息是否存在于数据库中
            boolean consistent = true;
            String inconsistencyReason = null;
            // 可以根据业务需要添加更多一致性检查逻辑
            if (taskInfo.getState() == null) {
                consistent = false;
                inconsistencyReason = "数据库中任务状态为空";
            }

            return new ConsistencyCheckResult(consistent, messageId, "N/A",
                    taskInfo.getState(), inconsistencyReason);
        } catch (Exception e) {
            log.error("检查一致性异常，messageId: {}", messageId, e);
            return new ConsistencyCheckResult(false, messageId, "ERROR", "ERROR",
                    "检查异常: " + e.getMessage());
        }
    }

    @Override
    public BaseEvent.EventMessage<?> forceSyncFromDatabase(String messageId) {
        try {
            // 从数据库重新构建事件对象
            BaseEvent.EventMessage<?> eventMessage = messagePersistenceService.getMessageById(messageId);
            if (eventMessage != null) {
                log.info("强制从数据库同步消息成功，messageId: {}", messageId);
                return eventMessage;
            } else {
                log.warn("数据库中未找到消息，messageId: {}", messageId);
                return null;
            }
        } catch (Exception e) {
            log.error("强制从数据库同步异常，messageId: {}", messageId, e);
            return null;
        }
    }

    @Override
    public int batchSyncMessageStates(List<String> messageIds) {
        int successCount = 0;
        for (String messageId : messageIds) {
            try {
                BaseEvent.EventMessage<?> eventMessage = forceSyncFromDatabase(messageId);
                if (eventMessage != null) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("批量同步失败，messageId: {}", messageId, e);
            }
        }
        log.info("批量同步完成，总数: {}, 成功: {}", messageIds.size(), successCount);

        return successCount;
    }

    @Override
    public void setSyncStrategy(SyncStrategy strategy) {
        this.currentStrategy = strategy;
        log.info("设置同步策略: {}", strategy);
    }

    @Override
    public SyncStrategy getCurrentStrategy() {
        return currentStrategy;
    }

    /**
     * 任务修复信息
     */
    @Data
    public static class TaskFixInfo {
        private String newState;
        private Integer newRetryCount;
        private String newErrorMessage;
        private StringBuilder fixReasons = new StringBuilder();

        public boolean needsFix() {
            return newState != null || newRetryCount != null || newErrorMessage != null;
        }

        public String getFixDescription() {
            return fixReasons.toString();
        }

        public void addFixReason(String reason) {
            if (!fixReasons.isEmpty()) {
                fixReasons.append("; ");
            }
            fixReasons.append(reason);
        }

    }

}
