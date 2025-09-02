package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @version 1.0
 * @Date 2025/8/8 16:32
 * @Description 车型视图对象
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarModelVO implements Serializable {

    /**
     * 车型ID
     */
    private Long modelId;
    /**
     * 车型编码
     */
    private String modelCode;
    /**
     * 车型名称
     */
    private String modelName;
    /**
     * 品牌名称
     */
    private String brand;
    /**
     * 动力类型编码
     */
    private String powerTypeCode;
    /**
     * 动力类型描述
     */
    private String powerTypeDesc;
    /**
     * 车型状态编码
     */
    private String statusCode;
    /**
     * 车型状态描述
     */
    private String statusDesc;
    /**
     * 车系ID
     */
    private Long seriesId;
    /**
     * 车系名称
     */
    private String seriesName;
    /**
     * 车型图标路径
     */
    private String iconPath;
    /**
     * 车型描述
     */
    private String description;
}
