package cn.cug.sxy.domain.series.service;

import cn.cug.sxy.domain.series.model.entity.CarModelEntity;
import cn.cug.sxy.domain.series.model.valobj.ModelCode;
import cn.cug.sxy.domain.series.model.valobj.ModelId;
import cn.cug.sxy.domain.series.model.valobj.ModelStatus;
import cn.cug.sxy.domain.series.model.valobj.PowerType;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/21 19:22
 * @Description 车型查询服务接口
 * @Author jerryhotton
 */

public interface ICarModelQueryService {

    /**
     * 根据车型ID查询车型
     *
     * @param modelId 车型ID
     * @return 车型实体，如果不存在则返回空
     */
    CarModelEntity getById(ModelId modelId);

    /**
     * 根据车型编码查询车型
     *
     * @param modelCode 车型编码
     * @return 车型实体，如果不存在则返回空
     */
    CarModelEntity getByCode(ModelCode modelCode);

    /**
     * 根据品牌查询车型列表
     *
     * @param brand 品牌
     * @return 车型实体列表
     */
    List<CarModelEntity> getByBrand(Brand brand);

    /**
     * 根据动力类型查询车型列表
     *
     * @param powerType 动力类型
     * @return 车型实体列表
     */
    List<CarModelEntity> getByPowerType(PowerType powerType);

    /**
     * 根据车型状态查询车型列表
     *
     * @param status 车型状态
     * @return 车型实体列表
     */
    List<CarModelEntity> getByStatus(ModelStatus status);

    /**
     * 根据车型系列ID查询车型列表
     *
     * @param seriesId 车型系列ID
     * @return 车型实体列表
     */
    List<CarModelEntity> getBySeriesId(SeriesId seriesId);

    /**
     * 根据车型名称模糊查询车型列表
     *
     * @param modelName 车型名称（部分匹配）
     * @return 车型实体列表
     */
    List<CarModelEntity> getByModelNameLike(String modelName);

    /**
     * 查询所有车型
     *
     * @return 车型实体列表
     */
    List<CarModelEntity> getAll();

    /**
     * 检查车型编码是否已存在
     *
     * @param modelCode 车型编码
     * @return 是否已存在
     */
    boolean isCodeExists(ModelCode modelCode);

}
