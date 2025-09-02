package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.event.model.entity.TaskEntity;
import cn.cug.sxy.domain.event.model.valobj.TaskState;
import cn.cug.sxy.infrastructure.dao.po.TaskPO;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/8/12 16:44
 * @Description 任务转换器
 * @Author jerryhotton
 */

public class TaskConverter {

    /**
     * 将Task实体转换为TaskPO
     *
     * @param entity 任务实体
     * @return 任务PO
     */
    public static TaskPO toPO(TaskEntity entity) {
        if (entity == null) {
            return null;
        }
        TaskPO taskPO = new TaskPO();
        taskPO.setId(entity.getId());
        taskPO.setMessageId(entity.getMessageId());
        taskPO.setTopic(entity.getTopic());
        taskPO.setMessage(entity.getMessage());
        taskPO.setState(entity.getState() != null ? entity.getState().getCode() : null);
        taskPO.setRetryCount(entity.getRetryCount());
        taskPO.setMaxRetries(entity.getMaxRetries());
        taskPO.setErrorMessage(entity.getErrorMessage());
        taskPO.setLastRetryTime(entity.getLastRetryTime());
        taskPO.setCreatedTime(entity.getCreatedTime());
        taskPO.setUpdatedTime(entity.getUpdatedTime());

        return taskPO;
    }

    /**
     * 将TaskPO转换为Task实体
     *
     * @param taskPO 任务PO
     * @return 任务实体
     */
    public static TaskEntity toEntity(TaskPO taskPO) {
        if (taskPO == null) {
            return null;
        }
        return TaskEntity.builder()
                .id(taskPO.getId())
                .messageId(taskPO.getMessageId())
                .topic(taskPO.getTopic())
                .message(taskPO.getMessage())
                .state(TaskState.fromCode(taskPO.getState()))
                .retryCount(taskPO.getRetryCount())
                .maxRetries(taskPO.getMaxRetries())
                .errorMessage(taskPO.getErrorMessage())
                .lastRetryTime(taskPO.getLastRetryTime())
                .createdTime(taskPO.getCreatedTime())
                .updatedTime(taskPO.getUpdatedTime())
                .build();
    }

    /**
     * 将TaskPO列表转换为Task实体列表
     *
     * @param taskPOs 任务PO列表
     * @return 任务实体列表
     */
    public static List<TaskEntity> toEntityList(List<TaskPO> taskPOs) {
        if (CollectionUtils.isEmpty(taskPOs)) {
            return Collections.emptyList();
        }
        return taskPOs.stream()
                .map(TaskConverter::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * 将Task实体列表转换为TaskPO列表
     *
     * @param taskEntities 任务实体列表
     * @return 任务PO列表
     */
    public static List<TaskPO> toPOList(List<TaskEntity> taskEntities) {
        if (CollectionUtils.isEmpty(taskEntities)) {
            return Collections.emptyList();
        }

        return taskEntities.stream()
                .map(TaskConverter::toPO)
                .collect(Collectors.toList());
    }

}
