package cn.cug.sxy.domain.event.service;

import cn.cug.sxy.domain.event.model.entity.TaskEntity;
import cn.cug.sxy.types.event.BaseEvent;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/12 15:59
 * @Description 任务管理服务接口
 * @Author jerryhotton
 */

public interface ITaskService {

    /**
     * 创建任务记录
     *
     * @param <T>          事件数据类型
     * @param eventMessage 事件消息对象
     * @return 是否创建成功
     */
    <T> boolean createTask(BaseEvent.EventMessage<T> eventMessage);

    /**
     * 更新任务状态为完成
     *
     * @param messageId 消息ID
     * @return 是否更新成功
     */
    boolean completeTask(String messageId);

    /**
     * 更新任务状态为失败
     *
     * @param messageId 消息ID
     * @return 是否更新成功
     */
    boolean failTask(String messageId);

    /**
     * 更新任务状态为已发布
     *
     * @param messageId 消息ID
     */
    void publishTask(String messageId);

    /**
     * 根据消息ID查询任务
     *
     * @param messageId 消息ID
     * @return 任务实体
     */
    TaskEntity getTaskByMessageId(String messageId);

    /**
     * 查询失败的任务列表
     *
     * @param limit 限制数量
     * @return 任务实体列表
     */
    List<TaskEntity> getFailedTasks(int limit);

    /**
     * 查询待处理的任务列表
     *
     * @param limit 限制数量
     * @return 任务实体列表
     */
    List<TaskEntity> getPendingTasks(int limit);

    /**
     * 删除任务记录
     *
     * @param messageId 消息ID
     * @return 是否删除成功
     */
    boolean deleteTask(String messageId);

    /**
     * 清理历史任务数据
     *
     * @param beforeDays 保留天数
     * @return 清理的任务数量
     */
    int cleanupHistoryTasks(int beforeDays);

}
