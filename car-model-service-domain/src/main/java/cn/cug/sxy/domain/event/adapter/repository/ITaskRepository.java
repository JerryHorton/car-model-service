package cn.cug.sxy.domain.event.adapter.repository;

import cn.cug.sxy.domain.event.model.entity.TaskEntity;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/12 16:37
 * @Description 任务仓储接口
 * @Author jerryhotton
 */

public interface ITaskRepository {

    /**
     * 保存任务
     *
     * @param task 任务实体
     * @return 是否保存成功
     */
    boolean save(TaskEntity task);

    /**
     * 根据消息ID查找任务
     *
     * @param messageId 消息ID
     * @return 任务实体
     */
    TaskEntity findByMessageId(String messageId);

    /**
     * 根据状态查找任务列表
     *
     * @param state 任务状态
     * @param limit 限制数量
     * @return 任务列表
     */
    List<TaskEntity> findByState(String state, int limit);

    /**
     * 根据主题查找任务列表
     *
     * @param topic 消息主题
     * @param limit 限制数量
     * @return 任务列表
     */
    List<TaskEntity> findByTopic(String topic, int limit);

    /**
     * 查找失败的任务
     *
     * @param limit 限制数量
     * @return 失败任务列表
     */
    List<TaskEntity> findFailedTasks(int limit);

    /**
     * 查找待处理的任务
     *
     * @param limit 限制数量
     * @return 待处理任务列表
     */
    List<TaskEntity> findPendingTasks(int limit);

    /**
     * 查找可重试的失败任务
     *
     * @param limit 限制数量
     * @return 可重试的失败任务列表
     */
    List<TaskEntity> findRetryableFailedTasks(int limit);

    /**
     * 查找超时的处理中任务
     *
     * @param timeoutSeconds 超时秒数
     * @param limit 限制数量
     * @return 超时任务列表
     */
    List<TaskEntity> findTimeoutProcessingTasks(int timeoutSeconds, int limit);

    /**
     * 更新任务状态
     *
     * @param task 任务实体
     * @return 是否更新成功
     */
    boolean updateState(TaskEntity task);

    /**
     * 更新任务重试信息
     *
     * @param messageId 消息ID
     * @param retryCount 重试次数
     * @param errorMessage 错误信息
     * @return 是否更新成功
     */
    boolean updateRetryInfo(String messageId, Integer retryCount, String errorMessage);

    /**
     * 删除任务
     *
     * @param messageId 消息ID
     * @return 是否删除成功
     */
    boolean delete(String messageId);

    /**
     * 清理已完成的历史任务
     *
     * @param beforeDays 保留天数
     * @return 清理的任务数量
     */
    int cleanupCompletedTasks(int beforeDays);

    /**
     * 统计任务数量
     *
     * @param state 任务状态
     * @return 任务数量
     */
    long countByState(String state);

}
