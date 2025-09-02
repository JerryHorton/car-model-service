package cn.cug.sxy.domain.event.service.strategy;

import cn.cug.sxy.domain.event.service.IMessagePersistenceService;
import cn.cug.sxy.types.event.BaseEvent;

/**
 * @version 1.0
 * @Date 2025/8/15 10:34
 * @Description 消息状态同步策略
 * @Author jerryhotton
 */

public interface IMessageStateSyncStrategy {

    /**
     * 同步消息状态
     *
     * @param eventMessage 事件消息
     * @param taskInfo 任务信息
     * @return 同步后的事件消息
     */
    <T> BaseEvent.EventMessage<T> syncState(BaseEvent.EventMessage<T> eventMessage, IMessagePersistenceService.TaskInfo taskInfo);

}
