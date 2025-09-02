package cn.cug.sxy.domain.event.service;

import cn.cug.sxy.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/15 09:37
 * @Description 消息状态同步器接口
 * @Author jerryhotton
 */

public interface IMessageStateSynchronizer {

    /**
     * 同步消息状态
     * 将数据库中的最新状态同步到消息对象中
     *
     * @param eventMessage 事件消息对象
     * @return 同步后的事件对象
     */
    <T> BaseEvent.EventMessage<T> syncMessageState(BaseEvent.EventMessage<T> eventMessage);

    /**
     * 验证消息状态一致性
     * 检查消息对象与数据库状态是否一致
     *
     * @param eventMessage 事件消息对象
     * @return 一致性检查结果
     */
    <T> ConsistencyCheckResult checkConsistency(BaseEvent.EventMessage<T> eventMessage);

    /**
     * 强制同步消息状态
     * 当发现不一致时，强制以数据库状态为准
     *
     * @param messageId 消息ID
     * @return 同步后的事件对象
     */
    BaseEvent.EventMessage<?> forceSyncFromDatabase(String messageId);

    /**
     * 批量同步消息状态
     *
     * @param messageIds 消息ID列表
     * @return 同步成功的数量
     */
    int batchSyncMessageStates(List<String> messageIds);

    /**
     * 一致性检查结果
     */
    @Data
    @AllArgsConstructor
    class ConsistencyCheckResult {
        private final boolean consistent;
        private final String messageId;
        private final String messageState;
        private final String databaseState;
        private final String inconsistencyReason;

        @Override
        public String toString() {
            return String.format("ConsistencyCheck[messageId=%s, consistent=%s, messageState=%s, dbState=%s, reason=%s]",
                    messageId, consistent, messageState, databaseState, inconsistencyReason);
        }

    }

    /**
     * 状态同步策略
     */
    enum SyncStrategy {
        /**
         * 以数据库状态为准
         */
        DATABASE_FIRST,

        /**
         * 以消息状态为准
         */
        MESSAGE_FIRST,

        /**
         * 智能合并（根据时间戳等因素决定）
         */
        SMART_MERGE
    }

    /**
     * 设置同步策略
     *
     * @param strategy 同步策略
     */
    void setSyncStrategy(SyncStrategy strategy);

    /**
     * 获取当前同步策略
     *
     * @return 当前策略
     */
    SyncStrategy getCurrentStrategy();

}
