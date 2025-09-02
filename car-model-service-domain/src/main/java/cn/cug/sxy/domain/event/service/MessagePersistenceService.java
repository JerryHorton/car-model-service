package cn.cug.sxy.domain.event.service;

import cn.cug.sxy.domain.event.adapter.repository.ITaskRepository;
import cn.cug.sxy.domain.event.model.entity.TaskEntity;
import cn.cug.sxy.domain.event.model.valobj.TaskState;
import cn.cug.sxy.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/8/12 15:56
 * @Description 消息持久化服务实现
 * @Author jerryhotton
 */

@Slf4j
@Service
public class MessagePersistenceService implements IMessagePersistenceService {

    private final ITaskRepository taskRepository;

    public MessagePersistenceService(ITaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public <T> boolean persistMessage(BaseEvent.EventMessage<T> eventMessage) {
        try {
            TaskEntity task = TaskEntity.create(eventMessage);
            boolean saved = taskRepository.save(task);
            if (saved) {
                log.info("消息持久化成功，messageId: {}, topic: {}", eventMessage.getId(), eventMessage.getTopic());
                return true;
            } else {
                log.warn("消息持久化失败，messageId: {}, topic: {}", eventMessage.getId(), eventMessage.getTopic());
                return false;
            }
        } catch (Exception e) {
            log.error("消息持久化异常，messageId: {}, topic: {}", eventMessage.getId(), eventMessage.getTopic(), e);
            throw e;
        }
    }

    @Override
    public boolean markAsPublished(String messageId) {
        try {
            TaskEntity task = TaskEntity.builder()
                    .messageId(messageId)
                    .build();
            task.markAsPublished();
            boolean updated = taskRepository.updateState(task);
            if (updated) {
                log.info("消息标记为已发布，messageId: {}", messageId);
                return true;
            } else {
                log.warn("消息标记为已发布失败，messageId: {}", messageId);
                return false;
            }
        } catch (Exception e) {
            log.error("消息标记为已发布异常，messageId: {}", messageId, e);
            return false;
        }
    }

    @Override
    public boolean markAsProcessing(String messageId) {
        try {
            TaskEntity task = TaskEntity.builder()
                    .messageId(messageId)
                    .build();
            task.markAsProcessing();
            boolean updated = taskRepository.updateState(task);
            if (updated) {
                log.info("消息标记为处理中，messageId: {}", messageId);
                return true;
            } else {
                log.warn("消息标记为处理中失败，messageId: {}", messageId);
                return false;
            }
        } catch (Exception e) {
            log.error("消息标记为处理中异常，messageId: {}", messageId, e);
            return false;
        }
    }

    @Override
    public boolean markAsCompleted(String messageId) {
        try {
            TaskEntity task = TaskEntity.builder()
                    .messageId(messageId)
                    .build();
            task.markAsCompleted();
            boolean updated = taskRepository.updateState(task);
            if (updated) {
                log.info("消息标记为已完成，messageId: {}", messageId);
                return true;
            } else {
                log.warn("消息标记为已完成失败，messageId: {}", messageId);
                return false;
            }
        } catch (Exception e) {
            log.error("消息标记为已完成异常，messageId: {}", messageId, e);
            return false;
        }
    }

    @Override
    public boolean markAsFailed(String messageId, String errorMessage) {
        try {
            TaskEntity task = TaskEntity.builder()
                    .messageId(messageId)
                    .build();
            task.markAsFailed(errorMessage);
            boolean updated = taskRepository.updateState(task);
            if (updated) {
                log.warn("消息标记为失败，messageId: {}, error: {}", messageId, errorMessage);
                return true;
            } else {
                log.error("消息标记为失败失败，messageId: {}", messageId);
                return false;
            }
        } catch (Exception e) {
            log.error("消息标记为失败异常，messageId: {}", messageId, e);
            return false;
        }
    }

    @Override
    public boolean updateRetryInfo(String messageId, Integer retryCount, String errorMessage) {
        try {
            boolean updated = taskRepository.updateRetryInfo(messageId, retryCount, errorMessage);
            if (updated) {
                log.info("消息重试信息更新成功，messageId: {}, retryCount: {}", messageId, retryCount);
                return true;
            } else {
                log.warn("消息重试信息更新失败，messageId: {}", messageId);
                return false;
            }
        } catch (Exception e) {
            log.error("消息重试信息更新异常，messageId: {}", messageId, e);
            return false;
        }
    }

    @Override
    public List<BaseEvent.EventMessage<?>> getUnpublishedMessages(int limit) {
        try {
            List<TaskEntity> tasks = taskRepository.findByState(TaskState.CREATE.getCode(), limit);
            List<BaseEvent.EventMessage<?>> eventMessages = tasks.stream()
                    .map(task -> {
                        try {
                            return task.parseToEventMessage();
                        } catch (Exception e) {
                            log.error("解析消息失败，messageId: {}", task.getMessageId(), e);
                            return null;
                        }
                    })
                    .filter(event -> event != null)
                    .collect(Collectors.toList());
            log.info("获取未发布消息，数量: {}", eventMessages.size());

            return eventMessages;
        } catch (Exception e) {
            log.error("获取未发布消息异常，limit: {}", limit, e);

            return new ArrayList<>();
        }
    }

    @Override
    public List<BaseEvent.EventMessage<?>> getTimeoutProcessingMessages(int timeoutMinutes, int limit) {
        try {
            List<TaskEntity> tasks = taskRepository.findTimeoutProcessingTasks(timeoutMinutes, limit);
            List<BaseEvent.EventMessage<?>> events = tasks.stream()
                    .map(task -> {
                        try {
                            return task.parseToEventMessage();
                        } catch (Exception e) {
                            log.error("解析超时消息失败，messageId: {}", task.getMessageId(), e);
                            return null;
                        }
                    })
                    .filter(event -> event != null)
                    .collect(Collectors.toList());
            log.info("获取超时处理消息，超时分钟: {}, 数量: {}", timeoutMinutes, events.size());

            return events;
        } catch (Exception e) {
            log.error("获取超时处理消息异常", e);

            return new ArrayList<>();
        }
    }

    @Override
    public List<BaseEvent.EventMessage<?>> getRetryableFailedMessages(int limit) {
        try {
            List<TaskEntity> tasks = taskRepository.findRetryableFailedTasks(limit);
            List<BaseEvent.EventMessage<?>> events = tasks.stream()
                    .map(task -> {
                        try {
                            return task.parseToEventMessage();
                        } catch (Exception e) {
                            log.error("解析可重试失败消息失败，messageId: {}", task.getMessageId(), e);
                            return null;
                        }
                    })
                    .filter(event -> event != null)
                    .collect(Collectors.toList());
            log.info("获取可重试失败消息，数量: {}", events.size());

            return events;
        } catch (Exception e) {
            log.error("获取可重试失败消息异常，limit: {}", limit, e);

            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanupCompletedMessages(int beforeDays) {
        try {
            int result = taskRepository.cleanupCompletedTasks(beforeDays);
            log.info("清理已完成消息，清理数量: {}, 保留天数: {}", result, beforeDays);
            return result;
        } catch (Exception e) {
            log.error("清理已完成消息异常，保留天数: {}", beforeDays, e);
            return 0;
        }
    }

    @Override
    public BaseEvent.EventMessage<?> getMessageById(String messageId) {
        try {
            TaskEntity task = taskRepository.findByMessageId(messageId);
            if (task != null) {
                return task.parseToEventMessage();
            }
            return null;
        } catch (Exception e) {
            log.error("根据ID获取消息异常，messageId: {}", messageId, e);

            return null;
        }
    }

    @Override
    public TaskInfo getTaskInfo(String messageId) {
        try {
            TaskEntity task = taskRepository.findByMessageId(messageId);
            if (task != null) {
                return new TaskInfo(
                        task.getMessageId(),
                        task.getState() != null ? task.getState().getCode() : null,
                        task.getRetryCount(),
                        task.getMaxRetries(),
                        task.getErrorMessage()
                );
            }
            return null;
        } catch (Exception e) {
            log.error("获取任务信息异常，messageId: {}", messageId, e);
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementRetryCount(String messageId, String errorMessage) {
        try {
            TaskEntity task = taskRepository.findByMessageId(messageId);
            if (task == null) {
                log.warn("任务不存在，无法增加重试次数，messageId: {}", messageId);
                return false;
            }
            // 增加重试次数并更新状态
            task.incrementRetry(errorMessage);
            boolean updated = taskRepository.updateState(task);
            if (updated) {
                log.info("增加重试次数成功，messageId: {}, retryCount: {}",
                        messageId, task.getRetryCount());
                return true;
            } else {
                log.warn("增加重试次数失败，messageId: {}", messageId);
                return false;
            }
        } catch (Exception e) {
            log.error("增加重试次数异常，messageId: {}", messageId, e);
            throw e; // 重新抛出异常，触发事务回滚
        }
    }

    @Override
    public boolean batchFixTaskState(String messageId, Object fixInfo) {
        try {
            if (!(fixInfo instanceof MessageStateSynchronizer.TaskFixInfo taskFixInfo)) {
                log.error("修复信息类型错误，messageId: {}", messageId);
                return false;
            }
            if (!taskFixInfo.needsFix()) {
                log.debug("无需修复，messageId: {}", messageId);
                return true;
            }
            TaskEntity task = taskRepository.findByMessageId(messageId);
            if (task == null) {
                log.warn("任务不存在，无法修复，messageId: {}", messageId);
                return false;
            }
            // 批量应用修复
            boolean hasChanges = false;
            if (taskFixInfo.getNewState() != null) {
                task.setState(TaskState.fromCode(taskFixInfo.getNewState()));
                hasChanges = true;
            }
            if (taskFixInfo.getNewRetryCount() != null) {
                task.setRetryCount(taskFixInfo.getNewRetryCount());
                hasChanges = true;
            }
            if (taskFixInfo.getNewErrorMessage() != null) {
                task.setErrorMessage(taskFixInfo.getNewErrorMessage());
                hasChanges = true;
            }
            if (hasChanges) {
                boolean updated = taskRepository.updateState(task);
                if (updated) {
                    log.info("批量修复任务状态成功，messageId: {}, 修复内容: {}",
                            messageId, taskFixInfo.getFixDescription());
                    return true;
                } else {
                    log.warn("批量修复任务状态失败，messageId: {}", messageId);
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            log.error("批量修复任务状态异常，messageId: {}", messageId, e);
            throw e;
        }
    }

}
