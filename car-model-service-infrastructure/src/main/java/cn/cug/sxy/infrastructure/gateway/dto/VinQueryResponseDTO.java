package cn.cug.sxy.infrastructure.gateway.dto;

import lombok.Data;

/**
 * @version 1.0
 * @Date 2025/7/22 18:42
 * @Description Vin查询响应
 * @Author jerryhotton
 */

@Data
public class VinQueryResponseDTO {

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
     * 动力类型
     */
    private String powerType;

}
