package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.structure.model.entity.GroupUsageConfigurationEntity;
import cn.cug.sxy.domain.structure.model.valobj.ConfigurationId;
import cn.cug.sxy.domain.structure.model.valobj.UsageId;
import cn.cug.sxy.infrastructure.dao.po.GroupUsageConfigurationPO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/8/4 16:29
 * @Description 用法与配置多对多关系转换器
 * @Author jerryhotton
 */

public class GroupUsageConfigurationConverter {

    /**
     * 实体转PO
     *
     * @param entity 实体
     * @return PO对象
     */
    public static GroupUsageConfigurationPO toPO(GroupUsageConfigurationEntity entity) {
        if (entity == null) {
            return null;
        }

        GroupUsageConfigurationPO po = new GroupUsageConfigurationPO();
        po.setId(entity.getId());
        po.setUsageId(entity.getUsageId() != null ? entity.getUsageId().getId() : null);
        po.setConfigId(entity.getConfigId() != null ? entity.getConfigId().getId() : null);

        return po;
    }

    /**
     * PO转实体
     *
     * @param po PO对象
     * @return 实体
     */
    public static GroupUsageConfigurationEntity toEntity(GroupUsageConfigurationPO po) {
        if (po == null) {
            return null;
        }

        GroupUsageConfigurationEntity entity = new GroupUsageConfigurationEntity();
        entity.setId(po.getId());
        entity.setUsageId(po.getUsageId() != null ? new UsageId(po.getUsageId()) : null);
        entity.setConfigId(po.getConfigId() != null ? new ConfigurationId(po.getConfigId()) : null);

        return entity;
    }

    /**
     * 实体列表转PO列表
     *
     * @param entities 实体列表
     * @return PO列表
     */
    public static List<GroupUsageConfigurationPO> toPOList(List<GroupUsageConfigurationEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(GroupUsageConfigurationConverter::toPO)
                .collect(Collectors.toList());
    }

    /**
     * PO列表转实体列表
     *
     * @param pos PO列表
     * @return 实体列表
     */
    public static List<GroupUsageConfigurationEntity> toEntityList(List<GroupUsageConfigurationPO> pos) {
        if (pos == null || pos.isEmpty()) {
            return Collections.emptyList();
        }

        return pos.stream()
                .map(GroupUsageConfigurationConverter::toEntity)
                .collect(Collectors.toList());
    }

}
