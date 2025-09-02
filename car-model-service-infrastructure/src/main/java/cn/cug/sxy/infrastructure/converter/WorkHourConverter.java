package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.workhour.model.entity.WorkHourEntity;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourCode;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourId;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourStatus;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourType;
import cn.cug.sxy.infrastructure.dao.po.WorkHourPO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/1/27 10:00
 * @Description 工时转换器
 * @Author jerryhotton
 */

public class WorkHourConverter {

    /**
     * 实体转PO
     */
    public static WorkHourPO toPO(WorkHourEntity entity) {
        if (entity == null) {
            return null;
        }

        return WorkHourPO.builder()
                .id(entity.getId() != null ? entity.getId().getId() : null)
                .parentId(entity.getParentId() != null ? entity.getParentId().getId() : null)
                .code(entity.getCode() != null ? entity.getCode().getCode() : null)
                .description(entity.getDescription())
                .standardHours(entity.getStandardHours())
                .type(entity.getType() != null ? entity.getType().getCode() : null)
                .stepOrder(entity.getStepOrder())
                .status(entity.getStatus() != null ? entity.getStatus().getCode() : null)
                .creator(entity.getCreator())
                .build();
    }

    /**
     * PO转实体
     */
    public static WorkHourEntity toEntity(WorkHourPO po) {
        if (po == null) {
            return null;
        }

        return WorkHourEntity.builder()
                .id(po.getId() != null ? new WorkHourId(po.getId()) : null)
                .parentId(po.getParentId() != null ? new WorkHourId(po.getParentId()) : null)
                .code(po.getCode() != null ? new WorkHourCode(po.getCode()) : null)
                .description(po.getDescription())
                .standardHours(po.getStandardHours())
                .type(po.getType() != null ? WorkHourType.fromCode(po.getType()) : null)
                .stepOrder(po.getStepOrder())
                .status(po.getStatus() != null ? WorkHourStatus.fromCode(po.getStatus()) : null)
                .creator(po.getCreator())
                .build();
    }

    /**
     * 实体列表转PO列表
     */
    public static List<WorkHourPO> toPOList(List<WorkHourEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(WorkHourConverter::toPO)
                .collect(Collectors.toList());
    }

    /**
     * PO列表转实体列表
     */
    public static List<WorkHourEntity> toEntityList(List<WorkHourPO> poList) {
        if (poList == null) {
            return Collections.emptyList();
        }

        return poList.stream()
                .map(WorkHourConverter::toEntity)
                .collect(Collectors.toList());
    }

}