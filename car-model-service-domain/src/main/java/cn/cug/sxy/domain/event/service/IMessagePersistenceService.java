package cn.cug.sxy.domain.event.service;

import cn.cug.sxy.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/12 15:55
 * @Description 消息持久化服务接口
 * @Author jerryhotton
 */

public interface IMessagePersistenceService {

    /**
     * 持久化消息到数据库
     * 在发送到MQ之前先保存到数据库，确保消息不丢失
     *
     * @param eventMessage 事件消息对象
     * @param <T> 事件数据类型
     * @return 是否持久化成功
     */
    <T> boolean persistMessage(BaseEvent.EventMessage<T> eventMessage);

    /**
     * 标记消息为已发布状态
     * 消息成功发送到MQ后调用
     *
     * @param messageId 消息ID
     * @return 是否更新成功
     */
    boolean markAsPublished(String messageId);

    /**
     * 标记消息为处理中状态
     * 消费者开始处理消息时调用
     *
     * @param messageId 消息ID
     * @return 是否更新成功
     */
    boolean markAsProcessing(String messageId);

    /**
     * 标记消息为已完成状态
     * 消息处理成功后调用
     *
     * @param messageId 消息ID
     * @return 是否更新成功
     */
    boolean markAsCompleted(String messageId);

    /**
     * 标记消息为失败状态
     * 消息处理失败后调用
     *
     * @param messageId 消息ID
     * @param errorMessage 错误信息
     * @return 是否更新成功
     */
    boolean markAsFailed(String messageId, String errorMessage);

    /**
     * 更新消息重试信息
     *
     * @param messageId 消息ID
     * @param retryCount 重试次数
     * @param errorMessage 错误信息
     * @return 是否更新成功
     */
    boolean updateRetryInfo(String messageId, Integer retryCount, String errorMessage);

    /**
     * 获取未发布的消息列表
     * 用于故障恢复，重新发送未成功发布的消息
     *
     * @param limit 限制数量
     * @return 未发布的消息列表
     */
    List<BaseEvent.EventMessage<?>> getUnpublishedMessages(int limit);

    /**
     * 获取处理中但超时的消息列表
     * 用于故障恢复，重新处理超时的消息
     *
     * @param timeoutMinutes 超时分钟数
     * @param limit 限制数量
     * @return 超时的消息列表
     */
    List<BaseEvent.EventMessage<?>> getTimeoutProcessingMessages(int timeoutMinutes, int limit);

    /**
     * 获取需要重试的失败消息列表
     *
     * @param limit 限制数量
     * @return 需要重试的消息列表
     */
    List<BaseEvent.EventMessage<?>> getRetryableFailedMessages(int limit);

    /**
     * 删除已完成的历史消息
     *
     * @param beforeDays 保留天数
     * @return 删除的消息数量
     */
    int cleanupCompletedMessages(int beforeDays);

    /**
     * 根据消息ID获取消息
     *
     * @param messageId 消息ID
     * @return 消息对象
     */
    BaseEvent.EventMessage<?> getMessageById(String messageId);

    /**
     * 获取任务的基本信息（包含重试次数等）
     * 用于重试逻辑中获取准确的任务状态
     *
     * @param messageId 消息ID
     * @return 任务信息
     */
    TaskInfo getTaskInfo(String messageId);

    /**
     * 增加重试次数
     * 原子性地增加重试次数并更新状态
     *
     * @param messageId 消息ID
     * @param errorMessage 错误信息
     * @return 是否更新成功
     */
    boolean incrementRetryCount(String messageId, String errorMessage);

    /**
     * 批量修复任务状态
     * 一次性修复任务的多个异常字段
     *
     * @param messageId 消息ID
     * @param fixInfo 修复信息
     * @return 是否修复成功
     */
    boolean batchFixTaskState(String messageId, Object fixInfo);

    /**
     * 任务信息DTO
     */
    @Getter
    @AllArgsConstructor
    class TaskInfo {

        private String messageId;
        private String state;
        private Integer retryCount;
        private Integer maxRetries;
        private String errorMessage;

    }

}
