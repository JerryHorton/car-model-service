package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.usage.model.entity.UsageConfigCombinationEntity;
import cn.cug.sxy.domain.usage.model.valobj.UsageConfigCombinationId;
import cn.cug.sxy.domain.usage.model.valobj.UsageId;
import cn.cug.sxy.infrastructure.dao.po.UsageConfigCombinationPO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/8/6 08:55
 * @Description 用法配置组合转换器
 * @Author jerryhotton
 */

public class UsageConfigCombinationConverter {

    /**
     * PO转领域实体
     * <p>
     * 注意：返回的实体不包含配置项列表，需要通过明细表单独查询
     *
     * @param po PO对象
     * @return 领域实体
     */
    public static UsageConfigCombinationEntity toEntity(UsageConfigCombinationPO po) {
        if (po == null) {
            return null;
        }

        return UsageConfigCombinationEntity.builder()
                .id(new UsageConfigCombinationId(po.getId()))
                .usageId(new UsageId(po.getUsageId()))
                .combinationName(po.getCombinationName())
                .sortOrder(po.getSortOrder())
                .configItemIds(null) // 配置项列表需要通过明细表单独查询
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
    public static UsageConfigCombinationPO toPO(UsageConfigCombinationEntity entity) {
        if (entity == null) {
            return null;
        }
        UsageConfigCombinationPO po = new UsageConfigCombinationPO();
        po.setId(entity.getId() != null ? entity.getId().getId() : null);
        po.setUsageId(entity.getUsageId() != null ? entity.getUsageId().getId() : null);
        po.setCombinationName(entity.getCombinationName());
        po.setSortOrder(entity.getSortOrder());
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
    public static List<UsageConfigCombinationEntity> toEntityList(List<UsageConfigCombinationPO> poList) {
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }

        return poList.stream()
                .map(UsageConfigCombinationConverter::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * 领域实体列表转PO列表
     *
     * @param entityList 领域实体列表
     * @return PO列表
     */
    public static List<UsageConfigCombinationPO> toPOList(List<UsageConfigCombinationEntity> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }

        return entityList.stream()
                .map(UsageConfigCombinationConverter::toPO)
                .collect(Collectors.toList());
    }

}
