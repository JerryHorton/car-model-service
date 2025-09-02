package cn.cug.sxy.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/5 18:32
 * @Description 配置项PO
 * @Author jerryhotton
 */

@Data
public class ConfigItemPO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 配置类别ID
     */
    private Long categoryId;
    /**
     * 配置项编码
     */
    private String itemCode;
    /**
     * 配置项名称
     */
    private String itemName;
    /**
     * 配置值
     */
    private String itemValue;
    /**
     * 状态
     */
    private String status;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

}
