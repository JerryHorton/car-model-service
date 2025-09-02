package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.event.adapter.repository.ITaskRepository;
import cn.cug.sxy.domain.event.model.entity.TaskEntity;
import cn.cug.sxy.infrastructure.converter.TaskConverter;
import cn.cug.sxy.infrastructure.dao.ITaskDao;
import cn.cug.sxy.infrastructure.dao.po.TaskPO;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/8/12 16:39
 * @Description 任务仓储实现类
 * @Author jerryhotton
 */

@Repository
public class TaskRepository implements ITaskRepository {

    private final ITaskDao taskDao;

    public TaskRepository(ITaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public boolean save(TaskEntity task) {
        TaskPO taskPO = TaskConverter.toPO(task);

        return taskDao.insert(taskPO) > 0;
    }

    @Override
    public TaskEntity findByMessageId(String messageId) {
        TaskPO taskPO = taskDao.selectByMessageId(messageId);
        if (taskPO == null) {
            return null;
        }

        return TaskConverter.toEntity(taskPO);
    }

    @Override
    public List<TaskEntity> findByState(String state, int limit) {
        List<TaskPO> taskPOs = taskDao.selectByState(state);
        if (CollectionUtils.isEmpty(taskPOs)) {
            return Collections.emptyList();
        }

        return taskPOs.stream()
                .limit(limit)
                .map(TaskConverter::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskEntity> findByTopic(String topic, int limit) {
        List<TaskPO> taskPOs = taskDao.selectByTopic(topic);
        if (CollectionUtils.isEmpty(taskPOs)) {
            return Collections.emptyList();
        }

        return taskPOs.stream()
                .limit(limit)
                .map(TaskConverter::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskEntity> findFailedTasks(int limit) {
        List<TaskPO> taskPOs = taskDao.selectFailedTasks(limit);
        if (CollectionUtils.isEmpty(taskPOs)) {
            return Collections.emptyList();
        }

        return taskPOs.stream()
                .map(TaskConverter::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskEntity> findPendingTasks(int limit) {
        List<TaskPO> taskPOS = taskDao.selectPendingTasks(limit);
        if (CollectionUtils.isEmpty(taskPOS)) {
            return Collections.emptyList();
        }

        return taskPOS.stream()
                .map(TaskConverter::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskEntity> findRetryableFailedTasks(int limit) {
        List<TaskPO> taskPOs = taskDao.selectFailedTasks(limit * 2);
        if (CollectionUtils.isEmpty(taskPOs)) {
            return Collections.emptyList();
        }

        return taskPOs.stream()
                .map(TaskConverter::toEntity)
                .filter(TaskEntity::canRetry)
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskEntity> findTimeoutProcessingTasks(int timeoutSeconds, int limit) {
        LocalDateTime beforeTime = LocalDateTime.now().minusSeconds(timeoutSeconds);
        List<TaskPO> taskPOs = taskDao.selectTimeoutProcessingTasks(beforeTime, limit);
        if (CollectionUtils.isEmpty(taskPOs)) {
            return Collections.emptyList();
        }

        return taskPOs.stream()
                .map(TaskConverter::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateState(TaskEntity task) {
        TaskPO taskPO = TaskConverter.toPO(task);

        return taskDao.updateState(taskPO) > 0;
    }

    @Override
    public boolean updateRetryInfo(String messageId, Integer retryCount, String errorMessage) {
        TaskPO taskPO = new TaskPO();
        taskPO.setMessageId(messageId);
        taskPO.setRetryCount(retryCount);
        taskPO.setErrorMessage(errorMessage);

        return taskDao.updateRetryInfo(taskPO) > 0;
    }

    @Override
    public boolean delete(String messageId) {
        return taskDao.deleteByMessageId(messageId) > 0;
    }

    @Override
    public int cleanupCompletedTasks(int beforeDays) {
        LocalDateTime beforeTime = LocalDateTime.now().minusDays(beforeDays);

        return taskDao.deleteCompletedTasksBefore(beforeTime);
    }

    @Override
    public long countByState(String state) {
        List<TaskPO> tasks = taskDao.selectByState(state);
        if (CollectionUtils.isEmpty(tasks)) {
            return 0;
        }

        return tasks.size();
    }

}
