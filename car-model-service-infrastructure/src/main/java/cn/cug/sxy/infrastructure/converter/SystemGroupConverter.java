package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.system.model.entity.SystemGroupEntity;
import cn.cug.sxy.domain.system.model.valobj.*;
import cn.cug.sxy.infrastructure.dao.po.SystemGroupPO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/7/28 09:03
 * @Description 系统分组转换器
 * @Author jerryhotton
 */

public class SystemGroupConverter {

    /**
     * PO转领域实体
     *
     * @param po PO对象
     * @return 领域实体
     */
    public static SystemGroupEntity toEntity(SystemGroupPO po) {
        if (po == null) {
            return null;
        }
        return SystemGroupEntity.builder()
                .groupId(new GroupId(po.getId()))
                .categoryId(new CategoryId(po.getCategoryId()))
                .groupCode(new GroupCode(po.getGroupCode()))
                .groupName(po.getGroupName())
                .groupNameEn(po.getGroupNameEn())
                .sortOrder(po.getSortOrder())
                .status(GroupStatus.fromCode(po.getStatus()))
                .creator(po.getCreator())
                .createdTime(po.getCreatedTime())
                .updatedTime(po.getUpdatedTime())
                .build();
    }

    /**
     * 领域实体转PO
     *
     * @param entity 领域实体
     * @return PO对象
     */
    public static SystemGroupPO toPO(SystemGroupEntity entity) {
        if (entity == null) {
            return null;
        }
        SystemGroupPO po = new SystemGroupPO();
        po.setId(entity.getGroupId().getId());
        po.setCategoryId(entity.getCategoryId().getId());
        po.setGroupCode(entity.getGroupCode().getCode());
        po.setGroupName(entity.getGroupName());
        po.setGroupNameEn(entity.getGroupNameEn());
        po.setSortOrder(entity.getSortOrder());
        po.setStatus(entity.getStatus().getCode());
        po.setCreator(entity.getCreator());
        po.setCreatedTime(entity.getCreatedTime());
        po.setUpdatedTime(entity.getUpdatedTime());
        return po;
    }

    /**
     * PO列表转领域实体列表
     *
     * @param poList PO列表
     * @return 领域实体列表
     */
    public static List<SystemGroupEntity> toEntityList(List<SystemGroupPO> poList) {
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }
        return poList.stream()
                .map(SystemGroupConverter::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * 领域实体列表转PO列表
     *
     * @param entityList 领域实体列表
     * @return PO列表
     */
    public static List<SystemGroupPO> toPOList(List<SystemGroupEntity> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }
        return entityList.stream()
                .map(SystemGroupConverter::toPO)
                .collect(Collectors.toList());
    }

}
