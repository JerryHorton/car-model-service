package cn.cug.sxy.domain.series.service;

import cn.cug.sxy.domain.series.model.entity.CarSeriesEntity;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesCode;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/21 15:23
 * @Description 车型系列查询服务接口
 * @Author jerryhotton
 */

public interface ICarSeriesQueryService {

    /**
     * 根据系列ID查询车型系列
     *
     * @param seriesId 系列ID
     * @return 车型系列实体，如果不存在则返回空
     */
    Optional<CarSeriesEntity> getById(SeriesId seriesId);

    /**
     * 根据系列编码查询车型系列
     *
     * @param seriesCode 系列编码
     * @return 车型系列实体，如果不存在则返回空
     */
    Optional<CarSeriesEntity> getByCode(SeriesCode seriesCode);

    /**
     * 根据品牌查询车型系列列表
     *
     * @param brand 品牌
     * @return 车型系列实体列表
     */
    List<CarSeriesEntity> getByBrand(Brand brand);

    /**
     * 根据系列名称模糊查询车型系列列表
     *
     * @param seriesName 系列名称（部分匹配）
     * @return 车型系列实体列表
     */
    List<CarSeriesEntity> getBySeriesNameLike(String seriesName);

    /**
     * 查询所有车型系列
     *
     * @return 车型系列实体列表
     */
    List<CarSeriesEntity> getAll();

    /**
     * 检查系列编码是否已存在
     *
     * @param seriesCode 系列编码
     * @return 是否已存在
     */
    boolean isCodeExists(SeriesCode seriesCode);

}
