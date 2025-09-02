package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.usage.model.entity.UsageEntity;
import cn.cug.sxy.domain.usage.model.valobj.UsageId;
import cn.cug.sxy.infrastructure.dao.po.UsagePO;
import cn.cug.sxy.types.enums.Status;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/8/6 08:53
 * @Description 用法转换器
 * @Author jerryhotton
 */

public class UsageConverter {

    /**
     * PO转领域实体
     *
     * @param po PO对象
     * @return 领域实体
     */
    public static UsageEntity toEntity(UsagePO po) {
        if (po == null) {
            return null;
        }
        return UsageEntity.builder()
                .id(new UsageId(po.getId()))
                .usageName(po.getUsageName())
                .explodedViewImg(po.getExplodedViewImg())
                .downloadUrl(po.getDownloadUrl())
                .status(Status.valueOf(po.getStatus()))
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
    public static UsagePO toPO(UsageEntity entity) {
        if (entity == null) {
            return null;
        }
        UsagePO po = new UsagePO();
        po.setId(entity.getId() == null ? null : entity.getId().getId());
        po.setUsageName(entity.getUsageName());
        po.setExplodedViewImg(entity.getExplodedViewImg());
        po.setDownloadUrl(entity.getDownloadUrl());
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
    public static List<UsageEntity> toEntityList(List<UsagePO> poList) {
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }

        return poList.stream()
                .map(UsageConverter::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * 领域实体列表转PO列表
     *
     * @param entityList 领域实体列表
     * @return PO列表
     */
    public static List<UsagePO> toPOList(List<UsageEntity> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }

        return entityList.stream()
                .map(UsageConverter::toPO)
                .collect(Collectors.toList());
    }

}
