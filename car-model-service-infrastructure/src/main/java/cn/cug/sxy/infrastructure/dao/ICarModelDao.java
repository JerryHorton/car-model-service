package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.CarModelPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/22 09:22
 * @Description 车型数据访问层接口
 * @Author jerryhotton
 */

@Mapper
public interface ICarModelDao {

    /**
     * 插入车型记录
     *
     * @param carModelPO 车型PO
     */
    void insert(CarModelPO carModelPO);

    /**
     * 更新车型记录
     *
     * @param carModelPO 车型PO
     * @return 影响的行数
     */
    int update(CarModelPO carModelPO);

    /**
     * 根据车型ID删除车型记录
     *
     * @param modelId 车型ID
     * @return 影响的行数
     */
    int deleteByModelId(Long modelId);

    /**
     * 根据车型ID查询车型记录
     *
     * @param modelId 车型ID
     * @return 车型PO
     */
    CarModelPO selectByModelId(Long modelId);

    /**
     * 根据车型编码查询车型记录
     *
     * @param modelCode 车型编码
     * @return 车型PO
     */
    CarModelPO selectByModelCode(String modelCode);

    /**
     * 根据品牌查询车型记录列表
     *
     * @param brand 品牌
     * @return 车型PO列表
     */
    List<CarModelPO> selectByBrand(String brand);

    /**
     * 根据动力类型查询车型记录列表
     *
     * @param powerType 动力类型
     * @return 车型PO列表
     */
    List<CarModelPO> selectByPowerType(String powerType);

    /**
     * 根据车型状态查询车型记录列表
     *
     * @param status 车型状态
     * @return 车型PO列表
     */
    List<CarModelPO> selectByStatus(String status);

    /**
     * 根据车型系列ID查询车型记录列表
     *
     * @param seriesId 车型系列ID
     * @return 车型PO列表
     */
    List<CarModelPO> selectBySeriesId(Long seriesId);

    /**
     * 根据车型名称模糊查询车型记录列表
     *
     * @param modelNameLike 车型名称（模糊匹配）
     * @return 车型PO列表
     */
    List<CarModelPO> selectByModelNameLike(String modelNameLike);

    /**
     * 查询所有车型记录
     *
     * @return 车型PO列表
     */
    List<CarModelPO> selectAll();

    /**
     * 检查车型编码是否已存在
     *
     * @param modelCode 车型编码
     * @return 是否存在
     */
    int countByModelCode(String modelCode);

}
