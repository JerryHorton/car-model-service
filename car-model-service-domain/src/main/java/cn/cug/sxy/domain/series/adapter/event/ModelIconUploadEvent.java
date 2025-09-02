package cn.cug.sxy.domain.series.adapter.event;

import cn.cug.sxy.types.event.BaseEvent;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @version 1.0
 * @Date 2025/8/13 13:38
 * @Description 车型图标上传事件
 * @Author jerryhotton
 */

@Component
public class ModelIconUploadEvent extends BaseEvent<ModelIconUploadEvent.ModelIconData> {

    @Value("${spring.rabbitmq.topic.model-icon-upload}")
    private String topic;

    @Override
    public EventMessage<ModelIconData> buildEventMessage(ModelIconData data) {
        return EventMessage.<ModelIconData>builder()
                .id(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .data(data)
                .topic(topic)
                .build();
    }

    @Override
    public String topic() {
        return topic;
    }

    @Data
    public static class ModelIconData {
        /**
         * 车型ID
         */
        private Long modelId;
        /**
         * 文件内容类型
         */
        private String contentType;
        /**
         * 文件名
         */
        private String fileName;
        /**
         * 文件数据（Base64编码）
         */
        private String fileData;
        /**
         * 上传类型：CREATE, UPDATE, DELETE
         */
        private String uploadType;
        /**
         * 旧文件路径（更新时使用）
         */
        private String oldFilePath;

    }

}
