package cn.cug.sxy.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/7/21 15:03
 * @Description 车型系列持久化对象
 * @Author jerryhotton
 */

@Data
public class CarSeriesPO {

    /**
     * 自增ID
     */
    private Long id;
    /**
     * 车型系列编码，唯一
     */
    private String seriesCode;
    /**
     * 车型系列名称
     */
    private String seriesName;
    /**
     * 品牌名称
     */
    private String brand;
    /**
     * 车型系列描述
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
