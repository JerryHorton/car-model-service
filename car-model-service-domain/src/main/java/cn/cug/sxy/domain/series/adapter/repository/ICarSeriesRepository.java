package cn.cug.sxy.domain.series.adapter.repository;

import cn.cug.sxy.domain.series.model.entity.CarSeriesEntity;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesCode;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/21 15:31
 * @Description 车型系列仓储接口
 * @Author jerryhotton
 */

public interface ICarSeriesRepository {

    /**
     * 保存车型系列实体
     *
     * @param carSeriesEntity 车型系列实体
     */
    void save(CarSeriesEntity carSeriesEntity);

    /**
     * 删除车型系列
     *
     * @param seriesId 系列ID
     * @return 是否删除成功
     */
    boolean remove(SeriesId seriesId);

    /**
     * 根据系列ID查找车型系列
     *
     * @param seriesId 系列ID
     * @return 车型系列实体，如果不存在则返回空
     */
    Optional<CarSeriesEntity> findById(SeriesId seriesId);

    /**
     * 根据系列编码查找车型系列
     *
     * @param seriesCode 系列编码
     * @return 车型系列实体，如果不存在则返回空
     */
    Optional<CarSeriesEntity> findByCode(SeriesCode seriesCode);

    /**
     * 根据品牌查找车型系列列表
     *
     * @param brand 品牌
     * @return 车型系列实体列表
     */
    List<CarSeriesEntity> findByBrand(Brand brand);

    /**
     * 根据系列名称模糊查询车型系列列表
     *
     * @param seriesName 系列名称（部分匹配）
     * @return 车型系列实体列表
     */
    List<CarSeriesEntity> findBySeriesNameLike(String seriesName);

    /**
     * 查询所有车型系列
     *
     * @return 车型系列实体列表
     */
    List<CarSeriesEntity> findAll();

    /**
     * 检查系列编码是否已存在
     *
     * @param seriesCode 系列编码
     * @return 是否已存在
     */
    boolean existsByCode(SeriesCode seriesCode);

}
