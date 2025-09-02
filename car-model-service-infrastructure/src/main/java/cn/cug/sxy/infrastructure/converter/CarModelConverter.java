package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.series.model.entity.CarModelEntity;
import cn.cug.sxy.domain.series.model.valobj.ModelCode;
import cn.cug.sxy.domain.series.model.valobj.ModelId;
import cn.cug.sxy.domain.series.model.valobj.ModelStatus;
import cn.cug.sxy.domain.series.model.valobj.PowerType;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import cn.cug.sxy.infrastructure.dao.po.CarModelPO;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/7/22 09:57
 * @Description 车型转换器
 * @Author jerryhotton
 */

public class CarModelConverter {

    /**
     * 将持久化对象转换为实体
     *
     * @param po 持久化对象
     * @return 实体对象
     */
    public static CarModelEntity toEntity(CarModelPO po) {
        if (po == null) {
            return null;
        }

        return CarModelEntity.builder()
                .id(new ModelId(po.getId()))
                .modelCode(new ModelCode(po.getModelCode()))
                .brand(new Brand(po.getBrand()))
                .powerType(PowerType.fromCode(po.getPowerType()))
                .status(ModelStatus.fromCode(po.getStatus()))
                .seriesId(new SeriesId(po.getSeriesId()))
                .modelName(po.getModelName())
                .iconPath(po.getIconPath())
                .description(po.getDescription())
                .build();
    }

    /**
     * 将实体转换为持久化对象
     *
     * @param entity 实体对象
     * @return 持久化对象
     */
    public static CarModelPO toPO(CarModelEntity entity) {
        if (entity == null) {
            return null;
        }
        CarModelPO po = new CarModelPO();
        po.setId(entity.getId() == null ? null : entity.getId().getId());
        po.setModelCode(entity.getModelCode() == null ? null : entity.getModelCode().getCode());
        po.setBrand(entity.getBrand() == null ? null : entity.getBrand().getName());
        po.setPowerType(entity.getPowerType() == null ? null : entity.getPowerType().getCode());
        po.setStatus(entity.getStatus() == null ? null : entity.getStatus().getCode());
        po.setSeriesId(entity.getSeriesId() == null ? null : entity.getSeriesId().getId());
        po.setModelName(entity.getModelName());
        po.setIconPath(entity.getIconPath());
        po.setDescription(entity.getDescription());

        return po;
    }

    /**
     * 将持久化对象列表转换为实体列表
     *
     * @param poList 持久化对象列表
     * @return 实体列表
     */
    public static List<CarModelEntity> toEntityList(List<CarModelPO> poList) {
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }

        return poList.stream()
                .filter(Objects::nonNull)
                .map(CarModelConverter::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * 将实体列表转换为持久化对象列表
     *
     * @param entityList 实体列表
     * @return 持久化对象列表
     */
    public static List<CarModelPO> toPOList(List<CarModelEntity> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }

        return entityList.stream()
                .filter(Objects::nonNull)
                .map(CarModelConverter::toPO)
                .collect(Collectors.toList());
    }

}
