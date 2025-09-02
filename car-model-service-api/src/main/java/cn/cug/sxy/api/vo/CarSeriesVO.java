package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/8/12 10:10
 * @Description 车系视图对象
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarSeriesVO {

    /**
     * 车系ID
     */
    private Long seriesId;
    /**
     * 车系编码
     */
    private String seriesCode;
    /**
     * 品牌名称
     */
    private String brand;
    /**
     * 车系名称
     */
    private String seriesName;
    /**
     * 车系描述
     */
    private String description;

}
