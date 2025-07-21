package cn.cug.sxy.domain.series.model.entity;

import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesCode;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/7/21 15:08
 * @Description 车型系列实体
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarSeriesEntity {

    /**
     * 系列ID
     */
    private SeriesId seriesId;
    /**
     * 车型系列编码，唯一
     */
    private SeriesCode seriesCode;
    /**
     * 品牌名称
     */
    private Brand brand;
    /**
     * 车型系列名称
     */
    private String seriesName;
    /**
     * 车型系列描述
     */
    private String description;

}
