package cn.cug.sxy.domain.series.service;

import cn.cug.sxy.domain.series.model.entity.CarSeriesEntity;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesCode;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;

/**
 * @version 1.0
 * @Date 2025/7/21 15:27
 * @Description 车型系列命令服务接口
 * @Author jerryhotton
 */

public interface ICarSeriesCommandService {

    /**
     * 创建车型系列
     *
     * @param seriesId    系列ID
     * @param seriesCode  系列编码
     * @param brand       品牌
     * @param seriesName  系列名称
     * @param description 系列描述
     * @return 创建的车型系列实体
     * @throws IllegalArgumentException 如果系列编码已存在
     */
    CarSeriesEntity createCarSeries(
            SeriesId seriesId,
            SeriesCode seriesCode,
            Brand brand,
            String seriesName,
            String description);

    /**
     * 删除车型系列
     *
     * @param seriesId 系列ID
     * @return 是否删除成功
     */
    boolean removeCarSeries(SeriesId seriesId);

}
