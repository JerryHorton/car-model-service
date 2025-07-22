package cn.cug.sxy.domain.model.adapter.repository;

import cn.cug.sxy.domain.model.model.entity.CarModelEntity;
import cn.cug.sxy.domain.model.model.valobj.ModelCode;
import cn.cug.sxy.domain.model.model.valobj.ModelId;
import cn.cug.sxy.domain.model.model.valobj.ModelStatus;
import cn.cug.sxy.domain.model.model.valobj.PowerType;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/21 19:21
 * @Description 车型仓储接口
 * @Author jerryhotton
 */

public interface ICarModelRepository {

    /**
     * 保存车型实体
     *
     * @param carModelEntity 车型实体
     */
    void save(CarModelEntity carModelEntity);

    /**
     * 更新车型实体
     *
     * @param carModelEntity 车型实体
     * @return 更新的记录数
     */
    int update(CarModelEntity carModelEntity);

    /**
     * 根据车型ID查找车型
     *
     * @param modelId 车型ID
     * @return 车型实体，如果不存在则返回空
     */
    Optional<CarModelEntity> findById(ModelId modelId);

    /**
     * 根据车型编码查找车型
     *
     * @param modelCode 车型编码
     * @return 车型实体，如果不存在则返回空
     */
    Optional<CarModelEntity> findByCode(ModelCode modelCode);

    /**
     * 根据品牌查找车型列表
     *
     * @param brand 品牌
     * @return 车型实体列表
     */
    List<CarModelEntity> findByBrand(Brand brand);

    /**
     * 根据动力类型查找车型列表
     *
     * @param powerType 动力类型
     * @return 车型实体列表
     */
    List<CarModelEntity> findByPowerType(PowerType powerType);

    /**
     * 根据车型状态查找车型列表
     *
     * @param status 车型状态
     * @return 车型实体列表
     */
    List<CarModelEntity> findByStatus(ModelStatus status);

    /**
     * 根据车型系列ID查找车型列表
     *
     * @param seriesId 车型系列ID
     * @return 车型实体列表
     */
    List<CarModelEntity> findBySeriesId(SeriesId seriesId);

    /**
     * 根据车型名称模糊查询车型列表
     *
     * @param modelName 车型名称（部分匹配）
     * @return 车型实体列表
     */
    List<CarModelEntity> findByModelNameLike(String modelName);

    /**
     * 查询所有车型
     *
     * @return 车型实体列表
     */
    List<CarModelEntity> findAll();

    /**
     * 删除车型
     * 只有待发布状态的车型才能删除
     *
     * @param modelId 车型ID
     * @return 是否删除成功
     */
    boolean remove(ModelId modelId);

    /**
     * 检查车型编码是否已存在
     *
     * @param modelCode 车型编码
     * @return 是否已存在
     */
    boolean existsByCode(ModelCode modelCode);

}
