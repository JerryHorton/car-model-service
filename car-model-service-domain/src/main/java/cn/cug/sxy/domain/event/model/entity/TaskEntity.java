package cn.cug.sxy.domain.event.model.entity;

import cn.cug.sxy.domain.event.model.valobj.TaskState;
import cn.cug.sxy.types.event.BaseEvent;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @version 1.0
 * @Date 2025/8/12 15:45
 * @Description 任务实体
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {

    /**
     * 任务ID
     */
    private String id;
    /**
     * 消息ID
     */
    private String messageId;
    /**
     * 消息主题
     */
    private String topic;
    /**
     * 消息主体
     */
    private String message;
    /**
     * CREATE-创建、PUBLISHED-已发布、PROCESSING-处理中、completed-完成、FAILED-失败、RETRY-重试中
     */
    private TaskState state;
    /**
     * 重试次数
     */
    private Integer retryCount;
    /**
     * 最大重试次数
     */
    private Integer maxRetries;
    /**
     * 错误信息
     */
    private String errorMessage;
    /**
     * 最后重试时间
     */
    private LocalDateTime lastRetryTime;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 创建新任务
     */
    public static TaskEntity create(BaseEvent.EventMessage<?> eventMessage) {
        return TaskEntity.builder()
                .messageId(eventMessage.getId())
                .topic(eventMessage.getTopic())
                .message(JSON.toJSONString(eventMessage))
                .state(TaskState.CREATE)
                .retryCount(0)
                .maxRetries(3)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    /**
     * 解析消息为事件消息对象
     */
    public <T> BaseEvent.EventMessage<T> parseToEventMessage() {
        try {
            return JSON.parseObject(this.message, new TypeReference<BaseEvent.EventMessage<T>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("解析消息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 标记为已发布
     */
    public void markAsPublished() {
        this.state = TaskState.PUBLISHED;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 标记为处理中
     */
    public void markAsProcessing() {
        this.state = TaskState.PROCESSING;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 标记为已完成
     */
    public void markAsCompleted() {
        this.state = TaskState.COMPLETED;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 标记为失败
     */
    public void markAsFailed(String errorMessage) {
        this.state = TaskState.FAILED;
        this.errorMessage = errorMessage;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 增加重试次数
     */
    public void incrementRetry(String errorMessage) {
        this.retryCount = (this.retryCount == null ? 0 : this.retryCount) + 1;
        this.errorMessage = errorMessage;
        this.lastRetryTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();

        if (this.retryCount < this.maxRetries) {
            this.state = TaskState.RETRY;
        } else {
            this.state = TaskState.FAILED;
        }
    }

    /**
     * 是否可以重试
     */
    public boolean canRetry() {
        return this.retryCount != null && this.maxRetries != null
                && this.retryCount < this.maxRetries
                && (this.state == TaskState.FAILED || this.state == TaskState.RETRY);
    }

    /**
     * 是否已超时（处理中状态超过指定分钟数）
     */
    public boolean isTimeout(int timeoutMinutes) {
        if (this.state != TaskState.PROCESSING) {
            return false;
        }
        long timeoutMillis = timeoutMinutes * 60 * 1000L;
        long currentTimeMillis = System.currentTimeMillis();
        LocalDateTime lastUpdate = (this.updatedTime != null) ? this.updatedTime : this.createdTime;
        long lastUpdateMillis = lastUpdate.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        return (currentTimeMillis - lastUpdateMillis) > timeoutMillis;
    }

    /**
     * 获取任务处理耗时（毫秒）
     */
    public long getProcessingDuration() {
        if (this.createdTime == null) {
            return 0;
        }
        LocalDateTime endTime = this.state == TaskState.COMPLETED ? this.updatedTime : LocalDateTime.now();

        return Duration.between(this.createdTime, endTime).toMillis();
    }

}
