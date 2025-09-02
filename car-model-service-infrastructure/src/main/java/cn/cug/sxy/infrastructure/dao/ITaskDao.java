package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.TaskPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/12 14:46
 * @Description 任务数据访问层接口
 * @Author jerryhotton
 */

@Mapper
public interface ITaskDao {

    /**
     * 插入任务记录
     *
     * @param taskPO 任务PO
     * @return 影响的行数
     */
    int insert(TaskPO taskPO);

    /**
     * 更新任务状态
     *
     * @param taskPO 任务PO
     * @return 影响的行数
     */
    int updateState(TaskPO taskPO);

    /**
     * 更新任务重试信息
     *
     * @param taskPO@return 影响的行数
     */
    int updateRetryInfo(TaskPO taskPO);

    /**
     * 根据消息ID查询任务
     *
     * @param messageId 消息ID
     * @return 任务PO
     */
    TaskPO selectByMessageId(String messageId);

    /**
     * 根据主题查询任务列表
     *
     * @param topic 消息主题
     * @return 任务PO列表
     */
    List<TaskPO> selectByTopic(String topic);

    /**
     * 根据状态查询任务列表
     *
     * @param state 任务状态
     * @return 任务PO列表
     */
    List<TaskPO> selectByState(String state);

    /**
     * 查询失败的任务列表
     *
     * @param limit 限制数量
     * @return 任务PO列表
     */
    List<TaskPO> selectFailedTasks(int limit);

    /**
     * 查询创建状态的任务列表（用于重试）
     *
     * @param limit 限制数量
     * @return 任务PO列表
     */
    List<TaskPO> selectCreatedTasks(int limit);

    /**
     * 查询待处理的任务列表
     *
     * @param limit 限制数量
     * @return 任务PO列表
     */
    List<TaskPO> selectPendingTasks(int limit);

    /**
     * 查询超时处理中的任务列表
     *
     * @param beforeTime 超时时间
     * @param limit          限制数量
     * @return 任务PO列表
     */
    List<TaskPO> selectTimeoutProcessingTasks(@Param("beforeTime") LocalDateTime beforeTime, @Param("limit") int limit);

    /**
     * 删除任务记录
     *
     * @param messageId 消息ID
     * @return 影响的行数
     */
    int deleteByMessageId(String messageId);

    /**
     * 批量删除已完成的任务（清理历史数据）
     *
     * @param beforeTime 时间，删除此时间之前的已完成任务
     * @return 影响的行数
     */
    int deleteCompletedTasksBefore(LocalDateTime beforeTime);

}
