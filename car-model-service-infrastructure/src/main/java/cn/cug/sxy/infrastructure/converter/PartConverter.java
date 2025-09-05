package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.part.model.entity.PartEntity;
import cn.cug.sxy.domain.part.model.valobj.PartCode;
import cn.cug.sxy.domain.part.model.valobj.PartId;
import cn.cug.sxy.infrastructure.dao.po.PartPO;
import cn.cug.sxy.types.enums.Status;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/9/3
 * @Description 备件转换器
 * @Author jerryhotton
 */
public class PartConverter {

    /**
     * 实体转PO
     */
    public static PartPO toPO(PartEntity entity) {
        if (entity == null) {
            return null;
        }

        PartPO po = new PartPO();
        po.setId(entity.getId() != null ? entity.getId().getId() : null);
        po.setPartCode(entity.getCode() != null ? entity.getCode().getCode() : null);
        po.setPartName(entity.getName());
        po.setStatus(entity.getStatus() != null ? entity.getStatus().getCode() : null);
        po.setCreator(entity.getCreator());
        po.setRemark(entity.getRemark());
        po.setCreatedTime(entity.getCreatedTime());
        po.setUpdatedTime(entity.getUpdatedTime());

        return po;
    }

    /**
     * PO转实体
     */
    public static PartEntity toEntity(PartPO po) {
        if (po == null) {
            return null;
        }

        return PartEntity.builder()
                .id(po.getId() != null ? new PartId(po.getId()) : null)
                .code(po.getPartCode() != null ? new PartCode(po.getPartCode()) : null)
                .name(po.getPartName())
                .status(po.getStatus() != null ? Status.fromCode(po.getStatus()) : null)
                .creator(po.getCreator())
                .remark(po.getRemark())
                .createdTime(po.getCreatedTime())
                .updatedTime(po.getUpdatedTime())
                .build();
    }

    /**
     * 实体列表转PO列表
     */
    public static List<PartPO> toPOList(List<PartEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(PartConverter::toPO)
                .collect(Collectors.toList());
    }

    /**
     * PO列表转实体列表
     */
    public static List<PartEntity> toEntityList(List<PartPO> poList) {
        if (poList == null) {
            return Collections.emptyList();
        }

        return poList.stream()
                .map(PartConverter::toEntity)
                .collect(Collectors.toList());
    }

}