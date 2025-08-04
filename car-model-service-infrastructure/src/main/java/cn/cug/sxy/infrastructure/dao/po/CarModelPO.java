package cn.cug.sxy.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/7/21 19:14
 * @Description 车型持久化对象
 * @Author jerryhotton
 */

@Data
public class CarModelPO {

    /**
     * 自增ID
     */
    private Long id;
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
     * 动力类型，例如"汽油"、"柴油"、"混合动力"、"纯电动"等
     */
    private String powerType;
    /**
     * 车型状态，包括"待发布"、"已发布"、"废止"三种状态
     */
    private String status;
    /**
     * 车型系列ID
     */
    private Long seriesId;
    /**
     * 车型图标文件路径
     */
    private String iconPath;
    /**
     * 车型描述
     */
    private String description;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

}
