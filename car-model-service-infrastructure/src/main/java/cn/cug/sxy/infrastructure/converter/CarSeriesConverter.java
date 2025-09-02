package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.series.model.entity.CarSeriesEntity;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesCode;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import cn.cug.sxy.infrastructure.dao.po.CarSeriesPO;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/7/21 15:42
 * @Description 车型系列转换器（负责CarSeries实体和CarSeriesPO之间的转换）
 * @Author jerryhotton
 */

public class CarSeriesConverter {

    /**
     * 将PO转换为Entity
     *
     * @param po 持久化对象
     * @return 领域实体
     */
    public static CarSeriesEntity toEntity(CarSeriesPO po) {
        if (po == null) {
            return null;
        }

        return CarSeriesEntity.builder()
                .id(new SeriesId(po.getId()))
                .seriesCode(new SeriesCode(po.getSeriesCode()))
                .brand(new Brand(po.getBrand()))
                .seriesName(po.getSeriesName())
                .description(po.getDescription())
                .build();
    }

    /**
     * 将Entity转换为PO
     *
     * @param entity 领域实体
     * @return 持久化对象
     */
    public static CarSeriesPO toPO(CarSeriesEntity entity) {
        if (entity == null) {
            return null;
        }
        CarSeriesPO po = new CarSeriesPO();
        po.setId(entity.getId() == null ? null : entity.getId().getId());
        po.setSeriesCode(entity.getSeriesCode() == null ? null : entity.getSeriesCode().getCode());
        po.setBrand(entity.getBrand() == null ? null : entity.getBrand().getName());
        po.setSeriesName(entity.getSeriesName());
        po.setDescription(entity.getDescription());

        return po;
    }

    /**
     * 将PO列表转换为Entity列表
     *
     * @param poList PO列表
     * @return Entity列表
     */
    public static List<CarSeriesEntity> toEntityList(List<CarSeriesPO> poList) {
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }

        return poList.stream()
                .filter(Objects::nonNull)
                .map(CarSeriesConverter::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * 将Entity列表转换为PO列表
     *
     * @param entityList Entity列表
     * @return PO列表
     */
    public static List<CarSeriesPO> toPOList(List<CarSeriesEntity> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }

        return entityList.stream()
                .filter(Objects::nonNull)
                .map(CarSeriesConverter::toPO)
                .collect(Collectors.toList());
    }

}
