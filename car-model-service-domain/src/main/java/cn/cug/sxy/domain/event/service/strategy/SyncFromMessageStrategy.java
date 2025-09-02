package cn.cug.sxy.domain.event.service.strategy;

import cn.cug.sxy.domain.event.service.IMessagePersistenceService;
import cn.cug.sxy.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @Date 2025/8/15 10:39
 * @Description 从消息同步状态策略
 * @Author jerryhotton
 */

@Slf4j
@Component(value = "MESSAGE_FIRST")
public class SyncFromMessageStrategy implements IMessageStateSyncStrategy {

    private final IMessagePersistenceService messagePersistenceService;

    public SyncFromMessageStrategy(IMessagePersistenceService messagePersistenceService) {
        this.messagePersistenceService = messagePersistenceService;
    }

    @Override
    public <T> BaseEvent.EventMessage<T> syncState(BaseEvent.EventMessage<T> eventMessage, IMessagePersistenceService.TaskInfo taskInfo) {
        String messageId = eventMessage.getId();
        try {
            // 如果消息是新的或者更权威，可以重置数据库状态
            // 这种情况比较少见，通常用于数据恢复场景

            // 检查消息的时间戳是否比数据库记录更新
            // 这里简化处理，主要是确保消息能被正确处理
            log.info("以消息为准同步，重置任务状态，messageId: {}", messageId);
            // 重置为初始状态，让消息重新处理
            messagePersistenceService.markAsProcessing(messageId);

            return eventMessage;
        } catch (Exception e) {
            log.error("消息状态同步异常，messageId: {}", messageId, e);
            return eventMessage;
        }
    }

}
