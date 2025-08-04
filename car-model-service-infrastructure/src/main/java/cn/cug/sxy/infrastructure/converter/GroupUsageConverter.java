package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.structure.model.entity.GroupUsageEntity;
import cn.cug.sxy.domain.structure.model.valobj.Status;
import cn.cug.sxy.domain.structure.model.valobj.UsageId;
import cn.cug.sxy.infrastructure.dao.po.GroupUsagePO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/8/4 16:28
 * @Description 系统分组用法转换器
 * @Author jerryhotton
 */

public class GroupUsageConverter {

    /**
     * 实体转PO
     *
     * @param entity 实体
     * @return PO对象
     */
    public static GroupUsagePO toPO(GroupUsageEntity entity) {
        if (entity == null) {
            return null;
        }

        GroupUsagePO po = new GroupUsagePO();
        po.setId(entity.getId() != null ? entity.getId().getId() : null);
        po.setGroupId(entity.getGroupId());
        po.setUsageId(entity.getUsageId());
        po.setUsageName(entity.getUsageName());
        po.setExplodedViewImg(entity.getExplodedViewImg());
        po.setStatus(entity.getStatus() != null ? entity.getStatus().getCode() : null);
        po.setCreator(entity.getCreator());
        po.setCreatedTime(entity.getCreatedTime());
        po.setUpdatedTime(entity.getUpdatedTime());

        return po;
    }

    /**
     * PO转实体
     *
     * @param po PO对象
     * @return 实体
     */
    public static GroupUsageEntity toEntity(GroupUsagePO po) {
        if (po == null) {
            return null;
        }

        GroupUsageEntity entity = new GroupUsageEntity();
        entity.setId(po.getId() != null ? new UsageId(po.getId()) : null);
        entity.setGroupId(po.getGroupId());
        entity.setUsageId(po.getUsageId());
        entity.setUsageName(po.getUsageName());
        entity.setExplodedViewImg(po.getExplodedViewImg());
        entity.setStatus(po.getStatus() != null ? Status.fromCode(po.getStatus()) : null);
        entity.setCreator(po.getCreator());
        entity.setCreatedTime(po.getCreatedTime());
        entity.setUpdatedTime(po.getUpdatedTime());

        return entity;
    }

    /**
     * 实体列表转PO列表
     *
     * @param entities 实体列表
     * @return PO列表
     */
    public static List<GroupUsagePO> toPOList(List<GroupUsageEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(GroupUsageConverter::toPO)
                .collect(Collectors.toList());
    }

    /**
     * PO列表转实体列表
     *
     * @param pos PO列表
     * @return 实体列表
     */
    public static List<GroupUsageEntity> toEntityList(List<GroupUsagePO> pos) {
        if (pos == null || pos.isEmpty()) {
            return Collections.emptyList();
        }

        return pos.stream()
                .map(GroupUsageConverter::toEntity)
                .collect(Collectors.toList());
    }

}
