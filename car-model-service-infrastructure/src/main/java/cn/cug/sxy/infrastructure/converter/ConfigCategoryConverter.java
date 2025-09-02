package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.usage.model.entity.ConfigCategoryEntity;
import cn.cug.sxy.domain.usage.model.valobj.ConfigCategoryId;
import cn.cug.sxy.infrastructure.dao.po.ConfigCategoryPO;
import cn.cug.sxy.types.enums.Status;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/8/6 08:47
 * @Description 配置类别转换器
 * @Author jerryhotton
 */

public class ConfigCategoryConverter {

    /**
     * PO转领域实体
     *
     * @param po PO对象
     * @return 领域实体
     */
    public static ConfigCategoryEntity toEntity(ConfigCategoryPO po) {
        if (po == null) {
            return null;
        }
        return ConfigCategoryEntity.builder()
                .id(new ConfigCategoryId(po.getId()))
                .categoryCode(po.getCategoryCode())
                .categoryName(po.getCategoryName())
                .sortOrder(po.getSortOrder())
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
    public static ConfigCategoryPO toPO(ConfigCategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        ConfigCategoryPO po = new ConfigCategoryPO();
        po.setId(entity.getId() == null ? null : entity.getId().getId());
        po.setCategoryCode(entity.getCategoryCode());
        po.setCategoryName(entity.getCategoryName());
        po.setSortOrder(entity.getSortOrder());
        po.setStatus(entity.getStatus() != null ? entity.getStatus().getCode() : null);
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
    public static List<ConfigCategoryEntity> toEntityList(List<ConfigCategoryPO> poList) {
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }

        return poList.stream()
                .map(ConfigCategoryConverter::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * 领域实体列表转PO列表
     *
     * @param entityList 领域实体列表
     * @return PO列表
     */
    public static List<ConfigCategoryPO> toPOList(List<ConfigCategoryEntity> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }

        return entityList.stream()
                .map(ConfigCategoryConverter::toPO)
                .collect(Collectors.toList());
    }

}
