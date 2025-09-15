package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.part.model.valobj.PartId;
import cn.cug.sxy.domain.usage.model.entity.UsagePartEntity;
import cn.cug.sxy.domain.usage.model.valobj.UsageId;
import cn.cug.sxy.infrastructure.dao.po.UsagePartPO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/9/5 09:30
 * @Description 用法备件关联转换器
 * @Author jerryhotton
 */

public class UsagePartConverter {

    /**
     * PO转实体
     *
     * @param po PO对象
     * @return 实体对象
     */
    public static UsagePartEntity toEntity(UsagePartPO po) {
        if (po == null) {
            return null;
        }

        return UsagePartEntity.builder()
                .id(po.getId())
                .usageId(new UsageId(po.getUsageId()))
                .partId(new PartId(po.getPartId()))
                .count(po.getCount())
                .build();
    }

    /**
     * 实体转PO
     *
     * @param entity 实体对象
     * @return PO对象
     */
    public static UsagePartPO toPO(UsagePartEntity entity) {
        if (entity == null) {
            return null;
        }
        UsagePartPO po = new UsagePartPO();
        po.setId(entity.getId());
        po.setUsageId(entity.getUsageId() != null ? entity.getUsageId().getId() : null);
        po.setPartId(entity.getPartId() != null ? entity.getPartId().getId() : null);
        po.setCount(entity.getCount());

        return po;
    }

    /**
     * PO列表转实体列表
     *
     * @param poList PO对象列表
     * @return 实体对象列表
     */
    public static List<UsagePartEntity> toEntityList(List<UsagePartPO> poList) {
        if (poList == null) {
            return List.of();
        }

        return poList.stream()
                .map(UsagePartConverter::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * 实体列表转PO列表
     *
     * @param entityList 实体对象列表
     * @return PO对象列表
     */
    public static List<UsagePartPO> toPOList(List<UsagePartEntity> entityList) {
        if (entityList == null) {
            return List.of();
        }

        return entityList.stream()
                .map(UsagePartConverter::toPO)
                .collect(Collectors.toList());
    }

}
