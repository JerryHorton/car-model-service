package cn.cug.sxy.domain.series.adapter.repository;

import cn.cug.sxy.domain.series.model.entity.CarModelEntity;
import cn.cug.sxy.domain.series.model.entity.CarSeriesEntity;
import cn.cug.sxy.domain.series.model.valobj.*;

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
    CarSeriesEntity save(CarSeriesEntity carSeriesEntity);

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
    List<CarSeriesEntity> findSeriesByBrand(Brand brand);

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

    /**
     * 更新车型系列
     *
     * @param carSeriesEntity 车型系列实体
     * @return 更新的记录数
     */
    int update(CarSeriesEntity carSeriesEntity);

    /**
     * 保存车型实体
     *
     * @param carModelEntity 车型实体
     */
    void save(CarModelEntity carModelEntity);

    /**
     * 上传车型图标
     *
     * @param fileData    文件数据
     * @param fileName    文件名
     * @param contentType 内容类型
     * @param modelId     车型ID
     * @return 图标路径
     */
    String uploadModelIcon(String fileData, String fileName, String contentType, Long modelId);

    /**
     * 更新车型实体
     *
     * @param carModelEntity 车型实体
     * @return 更新的记录数
     */
    int update(CarModelEntity carModelEntity);

    /**
     * 更新车型图标路径
     *
     * @param modelEntity 车型实体
     * @return 更新数量
     */
    int updateModelIconPath(CarModelEntity modelEntity);

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
    List<CarModelEntity> findModelByBrand(Brand brand);

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
    List<CarModelEntity> findAllModel();

    /**
     * 删除车型
     * 只有待发布状态的车型才能删除
     *
     * @param modelId 车型ID
     * @return 是否删除成功
     */
    boolean remove(ModelId modelId);

    /**
     * 删除车型图标
     *
     * @param iconPath 图标路径
     * @return 是否删除成功
     */
    boolean deleteModelIcon(String iconPath);

    /**
     * 检查车型编码是否已存在
     *
     * @param modelCode 车型编码
     * @return 是否已存在
     */
    boolean existsByCode(ModelCode modelCode);

}
