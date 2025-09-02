package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.usage.model.entity.UsageConfigCombinationDetailEntity;
import cn.cug.sxy.domain.usage.model.valobj.ConfigItemId;
import cn.cug.sxy.domain.usage.model.valobj.UsageConfigCombinationDetailId;
import cn.cug.sxy.domain.usage.model.valobj.UsageConfigCombinationId;
import cn.cug.sxy.infrastructure.dao.po.UsageConfigCombinationDetailPO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/8/6 09:01
 * @Description 用法配置组合明细转换器
 * @Author jerryhotton
 */

public class UsageConfigCombinationDetailConverter {

    /**
     * PO转领域实体
     *
     * @param po PO对象
     * @return 领域实体
     */
    public static UsageConfigCombinationDetailEntity toEntity(UsageConfigCombinationDetailPO po) {
        if (po == null) {
            return null;
        }

        return UsageConfigCombinationDetailEntity.builder()
                .id(new UsageConfigCombinationDetailId(po.getId()))
                .combinationId(new UsageConfigCombinationId(po.getCombinationId()))
                .configItemId(new ConfigItemId(po.getConfigItemId()))
                .createdTime(po.getCreatedTime())
                .build();
    }

    /**
     * 领域实体转PO
     *
     * @param entity 领域实体
     * @return PO对象
     */
    public static UsageConfigCombinationDetailPO toPO(UsageConfigCombinationDetailEntity entity) {
        if (entity == null) {
            return null;
        }
        UsageConfigCombinationDetailPO po = new UsageConfigCombinationDetailPO();
        po.setId(entity.getId() != null ? entity.getId().getId() : null);
        po.setCombinationId(entity.getCombinationId() != null ? entity.getCombinationId().getId() : null);
        po.setConfigItemId(entity.getConfigItemId() != null ? entity.getConfigItemId().getId() : null);
        po.setCreatedTime(entity.getCreatedTime());

        return po;
    }

    /**
     * PO列表转领域实体列表
     *
     * @param poList PO列表
     * @return 领域实体列表
     */
    public static List<UsageConfigCombinationDetailEntity> toEntityList(List<UsageConfigCombinationDetailPO> poList) {
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }

        return poList.stream()
                .map(UsageConfigCombinationDetailConverter::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * 领域实体列表转PO列表
     *
     * @param entityList 领域实体列表
     * @return PO列表
     */
    public static List<UsageConfigCombinationDetailPO> toPOList(List<UsageConfigCombinationDetailEntity> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }

        return entityList.stream()
                .map(UsageConfigCombinationDetailConverter::toPO)
                .collect(Collectors.toList());
    }

    /**
     * 配置项ID列表转明细实体列表
     * <p>
     * 用于创建组合时，将配置项ID列表转换为明细实体列表
     *
     * @param combinationId 组合ID
     * @param configItemIds 配置项ID列表
     * @return 明细实体列表
     */
    public static List<UsageConfigCombinationDetailEntity> createDetailEntities(
            UsageConfigCombinationId combinationId, List<ConfigItemId> configItemIds) {
        if (combinationId == null || configItemIds == null || configItemIds.isEmpty()) {
            return Collections.emptyList();
        }

        return configItemIds.stream()
                .map(configItemId -> UsageConfigCombinationDetailEntity.create(combinationId, configItemId))
                .collect(Collectors.toList());
    }

    /**
     * 明细实体列表转配置项ID列表
     * <p>
     * 用于从明细实体列表中提取配置项ID列表
     *
     * @param detailEntities 明细实体列表
     * @return 配置项ID列表
     */
    public static List<ConfigItemId> extractConfigItemIds(List<UsageConfigCombinationDetailEntity> detailEntities) {
        if (detailEntities == null || detailEntities.isEmpty()) {
            return Collections.emptyList();
        }

        return detailEntities.stream()
                .map(UsageConfigCombinationDetailEntity::getConfigItemId)
                .collect(Collectors.toList());
    }

}
