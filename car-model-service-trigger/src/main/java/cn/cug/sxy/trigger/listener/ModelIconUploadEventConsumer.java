package cn.cug.sxy.trigger.listener;

import cn.cug.sxy.domain.event.service.*;
import cn.cug.sxy.domain.series.adapter.event.ModelIconUploadEvent;
import cn.cug.sxy.types.event.BaseEvent;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @version 1.0
 * @Date 2025/8/13 13:55
 * @Description 车型图标上传事件消费者
 * @Author jerryhotton
 */

@Slf4j
@Component
public class ModelIconUploadEventConsumer extends AbstractEventConsumer<ModelIconUploadEvent.ModelIconData> {

    // 重试延迟配置（毫秒）
    private static final long[] RETRY_DELAYS = {60000, 300000, 900000}; // 1分钟, 5分钟, 15分钟

    private final Map<String, IModelIconUploadHandler> handlerMap;

    public ModelIconUploadEventConsumer(
            IMessagePersistenceService messagePersistenceService,
            IRetryStateManager retryStateManager,
            IMessageStateSynchronizer messageStateSynchronizer,
            Map<String, IModelIconUploadHandler> handlerMap) {
        super(messagePersistenceService, retryStateManager, messageStateSynchronizer);
        this.handlerMap = handlerMap;
    }

    @RabbitListener(queues = "${spring.rabbitmq.modules.file-upload.queue}")
    public void consumeModelIconUploadEvent(BaseEvent.EventMessage<ModelIconUploadEvent.ModelIconData> eventMessage, Message message, Channel channel) throws IOException {
        super.handleEvent(eventMessage, message, channel);
    }

    @Override
    protected boolean processBusinessLogic(BaseEvent.EventMessage<ModelIconUploadEvent.ModelIconData> eventMessage) {
        return processFileUploadEvent(eventMessage);
    }

    /**
     * 处理文件上传事件的具体逻辑
     */
    private boolean processFileUploadEvent(BaseEvent.EventMessage<ModelIconUploadEvent.ModelIconData> eventMessage) {
        ModelIconUploadEvent.ModelIconData data = eventMessage.getData();
        try {
            IModelIconUploadHandler handler = handlerMap.get(data.getUploadType());
            if (handler == null) {
                log.error("未找到处理车型图标上传事件的处理器，uploadType: {}, modelId: {}",
                        data.getUploadType(), data.getModelId());
                return false;
            }
            handler.handle(eventMessage.getData());

            return true;
        } catch (Exception e) {
            log.error("处理车型图标上传事件失败，uploadType: {}, modelId: {}",
                    data.getUploadType(), data.getModelId(), e);
            return false;
        }
    }

}
