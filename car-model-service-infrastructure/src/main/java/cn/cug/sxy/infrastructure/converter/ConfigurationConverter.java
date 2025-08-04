package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.structure.model.entity.ConfigurationEntity;
import cn.cug.sxy.domain.structure.model.valobj.ConfigurationCode;
import cn.cug.sxy.domain.structure.model.valobj.ConfigurationId;
import cn.cug.sxy.domain.structure.model.valobj.Status;
import cn.cug.sxy.infrastructure.dao.po.ConfigurationPO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/8/4 16:26
 * @Description 车型配置转换器
 * @Author jerryhotton
 */

public class ConfigurationConverter {

    /**
     * 实体转PO
     *
     * @param entity 实体
     * @return PO对象
     */
    public static ConfigurationPO toPO(ConfigurationEntity entity) {
        if (entity == null) {
            return null;
        }

        ConfigurationPO po = new ConfigurationPO();
        po.setId(entity.getId() != null ? entity.getId().getId() : null);
        po.setConfigCode(entity.getConfigCode() != null ? entity.getConfigCode().getCode() : null);
        po.setConfigName(entity.getConfigName());
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
    public static ConfigurationEntity toEntity(ConfigurationPO po) {
        if (po == null) {
            return null;
        }

        ConfigurationEntity entity = new ConfigurationEntity();
        entity.setId(po.getId() != null ? new ConfigurationId(po.getId()) : null);
        entity.setConfigCode(po.getConfigCode() != null ? new ConfigurationCode(po.getConfigCode()) : null);
        entity.setConfigName(po.getConfigName());
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
    public static List<ConfigurationPO> toPOList(List<ConfigurationEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(ConfigurationConverter::toPO)
                .collect(Collectors.toList());
    }

    /**
     * PO列表转实体列表
     *
     * @param pos PO列表
     * @return 实体列表
     */
    public static List<ConfigurationEntity> toEntityList(List<ConfigurationPO> pos) {
        if (pos == null || pos.isEmpty()) {
            return Collections.emptyList();
        }

        return pos.stream()
                .map(ConfigurationConverter::toEntity)
                .collect(Collectors.toList());
    }

}
