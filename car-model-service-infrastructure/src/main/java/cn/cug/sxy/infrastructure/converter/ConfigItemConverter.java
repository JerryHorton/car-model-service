package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.usage.model.entity.ConfigItemEntity;
import cn.cug.sxy.domain.usage.model.valobj.ConfigCategoryId;
import cn.cug.sxy.domain.usage.model.valobj.ConfigItemId;
import cn.cug.sxy.infrastructure.dao.po.ConfigItemPO;
import cn.cug.sxy.types.enums.Status;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/8/6 08:50
 * @Description 配置项转换器
 * @Author jerryhotton
 */

public class ConfigItemConverter {

    /**
     * PO转领域实体
     *
     * @param po PO对象
     * @return 领域实体
     */
    public static ConfigItemEntity toEntity(ConfigItemPO po) {
        if (po == null) {
            return null;
        }
        return ConfigItemEntity.builder()
                .id(new ConfigItemId(po.getId()))
                .categoryId(new ConfigCategoryId(po.getCategoryId()))
                .itemCode(po.getItemCode())
                .itemName(po.getItemName())
                .itemValue(po.getItemValue())
                .status(Status.fromCode(po.getStatus()))
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
    public static ConfigItemPO toPO(ConfigItemEntity entity) {
        if (entity == null) {
            return null;
        }
        ConfigItemPO po = new ConfigItemPO();
        po.setId(entity.getId() == null ? null : entity.getId().getId());
        po.setCategoryId(entity.getCategoryId() == null ? null : entity.getCategoryId().getId());
        po.setItemCode(entity.getItemCode());
        po.setItemName(entity.getItemName());
        po.setItemValue(entity.getItemValue());
        po.setStatus(entity.getStatus() == null ? null : entity.getStatus().getCode());
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
    public static List<ConfigItemEntity> toEntityList(List<ConfigItemPO> poList) {
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }

        return poList.stream()
                .map(ConfigItemConverter::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * 领域实体列表转PO列表
     *
     * @param entityList 领域实体列表
     * @return PO列表
     */
    public static List<ConfigItemPO> toPOList(List<ConfigItemEntity> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }

        return entityList.stream()
                .map(ConfigItemConverter::toPO)
                .collect(Collectors.toList());
    }

}
