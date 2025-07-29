package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.system.model.entity.SystemCategoryEntity;
import cn.cug.sxy.domain.system.model.valobj.CategoryCode;
import cn.cug.sxy.domain.system.model.valobj.CategoryId;
import cn.cug.sxy.domain.system.model.valobj.CategoryStatus;
import cn.cug.sxy.infrastructure.dao.po.SystemCategoryPO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/7/28 09:01
 * @Description 系统大类转换器
 * @Author jerryhotton
 */

public class SystemCategoryConverter {

    /**
     * PO转领域实体
     *
     * @param po PO对象
     * @return 领域实体
     */
    public static SystemCategoryEntity toEntity(SystemCategoryPO po) {
        if (po == null) {
            return null;
        }
        return SystemCategoryEntity.builder()
                .categoryId(new CategoryId(po.getId()))
                .categoryCode(new CategoryCode(po.getCategoryCode()))
                .categoryName(po.getCategoryName())
                .categoryNameEn(po.getCategoryNameEn())
                .sortOrder(po.getSortOrder())
                .status(CategoryStatus.fromCode(po.getStatus()))
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
    public static SystemCategoryPO toPO(SystemCategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        SystemCategoryPO po = new SystemCategoryPO();
        po.setId(entity.getCategoryId().getId());
        po.setCategoryCode(entity.getCategoryCode().getCode());
        po.setCategoryName(entity.getCategoryName());
        po.setCategoryNameEn(entity.getCategoryNameEn());
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
    public static List<SystemCategoryEntity> toEntityList(List<SystemCategoryPO> poList) {
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }
        return poList.stream()
                .map(SystemCategoryConverter::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * 领域实体列表转PO列表
     *
     * @param entityList 领域实体列表
     * @return PO列表
     */
    public static List<SystemCategoryPO> toPOList(List<SystemCategoryEntity> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }
        return entityList.stream()
                .map(SystemCategoryConverter::toPO)
                .collect(Collectors.toList());
    }

}
