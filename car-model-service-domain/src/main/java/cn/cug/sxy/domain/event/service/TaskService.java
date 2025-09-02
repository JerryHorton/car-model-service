package cn.cug.sxy.domain.event.service;

import cn.cug.sxy.domain.event.adapter.repository.ITaskRepository;
import cn.cug.sxy.domain.event.model.entity.TaskEntity;
import cn.cug.sxy.domain.event.model.valobj.TaskState;
import cn.cug.sxy.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/13 09:22
 * @Description 任务服务实现
 * @Author jerryhotton
 */

@Slf4j
@Service
public class TaskService implements ITaskService {

    private final ITaskRepository taskRepository;

    public TaskService(ITaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public <T> boolean createTask(BaseEvent.EventMessage<T> eventMessage) {
        try {
            TaskEntity task = TaskEntity.create(eventMessage);
            boolean saved = taskRepository.save(task);
            if (saved) {
                log.info("任务创建成功，messageId: {}, topic: {}", eventMessage.getId(), eventMessage.getTopic());
                return true;
            } else {
                log.warn("任务创建失败，messageId: {}, topic: {}", eventMessage.getId(), eventMessage.getTopic());
                return false;
            }
        } catch (Exception e) {
            log.error("任务创建异常，messageId: {}, topic: {}", eventMessage.getId(), eventMessage.getTopic(), e);
            return false;
        }
    }

    @Override
    public boolean completeTask(String messageId) {
        try {
            TaskEntity task = taskRepository.findByMessageId(messageId);
            if (task == null) {
                log.warn("未找到任务，messageId: {}", messageId);
                return false;
            }
            task.markAsCompleted();
            boolean updated = taskRepository.updateState(task);

            if (updated) {
                log.info("任务完成状态更新成功，messageId: {}", messageId);
                return true;
            } else {
                log.warn("任务完成状态更新失败，messageId: {}", messageId);
                return false;
            }
        } catch (Exception e) {
            log.error("任务完成状态更新异常，messageId: {}", messageId, e);
            return false;
        }
    }

    @Override
    public boolean failTask(String messageId) {
        try {
            TaskEntity task = TaskEntity.builder()
                    .messageId(messageId)
                    .state(TaskState.FAILED)
                    .build();
            boolean updated = taskRepository.updateState(task);
            if (updated) {
                log.info("任务失败状态更新成功，messageId: {}", messageId);
                return true;
            } else {
                log.warn("任务失败状态更新失败，messageId: {}", messageId);
                return false;
            }
        } catch (Exception e) {
            log.error("任务失败状态更新异常，messageId: {}", messageId, e);
            return false;
        }
    }

    @Override
    public void publishTask(String messageId) {
        try {
            TaskEntity task = TaskEntity.builder()
                    .messageId(messageId)
                    .state(TaskState.PUBLISHED)
                    .build();
            boolean updated = taskRepository.updateState(task);
            if (updated) {
                log.info("任务发布状态更新成功，messageId: {}", messageId);
            } else {
                log.warn("任务发布状态更新失败，messageId: {}", messageId);
            }
        } catch (Exception e) {
            log.error("任务发布状态更新异常，messageId: {}", messageId, e);
        }
    }

    @Override
    public TaskEntity getTaskByMessageId(String messageId) {
        try {
            return taskRepository.findByMessageId(messageId);
        } catch (Exception e) {
            log.error("查询任务异常，messageId: {}", messageId, e);
            return null;
        }
    }

    @Override
    public List<TaskEntity> getFailedTasks(int limit) {
        try {
            return taskRepository.findFailedTasks(limit);
        } catch (Exception e) {
            log.error("查询失败任务异常，limit: {}", limit, e);
            return null;
        }
    }

    @Override
    public List<TaskEntity> getPendingTasks(int limit) {
        try {
            return taskRepository.findPendingTasks(limit);
        } catch (Exception e) {
            log.error("查询待处理任务异常，limit: {}", limit, e);
            return null;
        }
    }

    @Override
    public boolean deleteTask(String messageId) {
        try {
            boolean deleted = taskRepository.delete(messageId);
            if (deleted) {
                log.info("任务删除成功，messageId: {}", messageId);
                return true;
            } else {
                log.warn("任务删除失败，messageId: {}", messageId);
                return false;
            }
        } catch (Exception e) {
            log.error("任务删除异常，messageId: {}", messageId, e);
            return false;
        }
    }

    @Override
    public int cleanupHistoryTasks(int beforeDays) {
        try {
            int result = taskRepository.cleanupCompletedTasks(beforeDays);
            log.info("历史任务清理完成，清理数量: {}, 保留天数: {}", result, beforeDays);
            return result;
        } catch (Exception e) {
            log.error("历史任务清理异常，保留天数: {}", beforeDays, e);
            return 0;
        }
    }

}
