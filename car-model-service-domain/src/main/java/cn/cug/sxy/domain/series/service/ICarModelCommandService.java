package cn.cug.sxy.domain.series.service;

import cn.cug.sxy.domain.series.model.entity.CarModelEntity;
import cn.cug.sxy.domain.series.model.valobj.ModelCode;
import cn.cug.sxy.domain.series.model.valobj.ModelId;
import cn.cug.sxy.domain.series.model.valobj.PowerType;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;

/**
 * @version 1.0
 * @Date 2025/7/21 19:23
 * @Description 车型命令服务接口
 * @Author jerryhotton
 */

public interface ICarModelCommandService {

    /**
     * 创建车型
     *
     * @param modelCode   车型编码
     * @param brand       品牌
     * @param powerType   动力类型
     * @param seriesId    车型系列ID
     * @param modelName   车型名称
     * @param iconPath    车型图标路径
     * @param description 车型描述
     * @return 创建的车型实体
     * @throws IllegalArgumentException 如果车型编码已存在
     */
    CarModelEntity createCarModel(
            ModelCode modelCode,
            Brand brand,
            PowerType powerType,
            SeriesId seriesId,
            String modelName,
            String iconPath,
            String description);

    /**
     * 上传车型图标
     *
     * @param fileData  文件数据
     * @param fileName  文件名
     * @param contentType 内容类型
     * @param modelId   车型ID
     * @return 图标路径
     */
    String uploadModelIcon(String fileData, String fileName, String contentType, Long modelId);

    /**
     * 删除车型图标
     *
     * @param iconPath 图标路径
     * @return 是否删除成功
     */
    boolean deleteModelIcon(String iconPath);

    /**
     * 更新车型图标路径
     *
     * @param modelEntity 车型实体
     * @return 更新数量
     */
    int updateModelIconPath(CarModelEntity modelEntity);

    /**
     * 更新车型
     *
     * @param carModelEntity 车型实体
     * @return 更新数量
     * @throws IllegalArgumentException 如果车型不存在或车型编码已存在
     */
    int updateCarModel(CarModelEntity carModelEntity);

    /**
     * 发布车型
     * 只有待发布状态的车型才能发布
     *
     * @param modelId 车型ID
     * @return 发布后的车型实体
     * @throws IllegalArgumentException 如果车型不存在
     * @throws IllegalStateException    如果车型状态不是待发布
     */
    CarModelEntity publishCarModel(ModelId modelId);

    /**
     * 废止车型
     * 只有已发布状态的车型才能废止
     *
     * @param modelId 车型ID
     * @return 废止后的车型实体
     * @throws IllegalArgumentException 如果车型不存在
     * @throws IllegalStateException    如果车型状态不是已发布
     */
    CarModelEntity deprecateCarModel(ModelId modelId);

    /**
     * 删除车型
     * 只有待发布状态的车型才能删除
     *
     * @param modelId 车型ID
     * @return 是否删除成功
     * @throws IllegalArgumentException 如果车型不存在
     * @throws IllegalStateException    如果车型状态不是待发布
     */
    boolean removeCarModel(ModelId modelId);

}
