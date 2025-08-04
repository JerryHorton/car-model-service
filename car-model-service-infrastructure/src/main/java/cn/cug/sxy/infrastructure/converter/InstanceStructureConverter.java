package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.model.model.valobj.ModelId;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import cn.cug.sxy.domain.structure.model.entity.StructureInstanceEntity;
import cn.cug.sxy.domain.structure.model.valobj.InstanceCode;
import cn.cug.sxy.domain.structure.model.valobj.InstanceId;
import cn.cug.sxy.domain.structure.model.valobj.Status;
import cn.cug.sxy.infrastructure.dao.po.InstanceStructurePO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/7/30 17:50
 * @Description 实例结构树转换
 * @Author jerryhotton
 */

public class InstanceStructureConverter {

    public static InstanceStructurePO toPO(StructureInstanceEntity instance) {
        if (instance == null) {
            return null;
        }
        InstanceStructurePO po = new InstanceStructurePO();
        po.setId(instance.getId() == null ? null : instance.getId().getId());
        po.setInstanceCode(instance.getInstanceCode() == null ? null : instance.getInstanceCode().getCode());
        po.setInstanceName(instance.getInstanceName());
        po.setInstanceDesc(instance.getInstanceDesc());
        po.setSeriesId(instance.getSeriesId() == null ? null : instance.getSeriesId().getId());
        po.setModelId(instance.getModelId() == null ? null : instance.getModelId().getId());
        po.setVersion(instance.getVersion());
        po.setStatus(instance.getStatus().getCode());
        po.setIsPublished(instance.getIsPublished());
        po.setEffectiveTime(instance.getEffectiveTime());
        po.setCreator(instance.getCreator());
        po.setCreatedTime(instance.getCreatedTime());
        po.setUpdatedTime(instance.getUpdatedTime());

        return po;
    }

    public static StructureInstanceEntity toEntity(InstanceStructurePO po) {
        if (po == null) {
            return null;
        }

        return StructureInstanceEntity.builder()
                .id(new InstanceId(po.getId()))
                .instanceCode(po.getInstanceCode() == null ? null : new InstanceCode(po.getInstanceCode()))
                .instanceName(po.getInstanceName())
                .instanceDesc(po.getInstanceDesc())
                .seriesId(po.getSeriesId() == null ? null : new SeriesId(po.getSeriesId()))
                .modelId(po.getModelId() == null ? null : new ModelId(po.getModelId()))
                .version(po.getVersion())
                .status(Status.fromCode(po.getStatus()))
                .isPublished(po.getIsPublished())
                .effectiveTime(po.getEffectiveTime())
                .creator(po.getCreator())
                .createdTime(po.getCreatedTime())
                .updatedTime(po.getUpdatedTime())
                .build();
    }

    public static List<StructureInstanceEntity> toEntityList(List<InstanceStructurePO> poList) {
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }

        return poList.stream()
                .map(InstanceStructureConverter::toEntity)
                .collect(Collectors.toList());
    }

    public static List<InstanceStructurePO> toPOList(List<StructureInstanceEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(InstanceStructureConverter::toPO)
                .collect(Collectors.toList());
    }


}
