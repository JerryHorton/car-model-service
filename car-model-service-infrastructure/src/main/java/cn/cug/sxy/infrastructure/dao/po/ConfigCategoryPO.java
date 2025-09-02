package cn.cug.sxy.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/5 18:31
 * @Description 配置类别PO
 * @Author jerryhotton
 */

@Data
public class ConfigCategoryPO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 类别编码
     */
    private String categoryCode;
    /**
     * 类别名称
     */
    private String categoryName;
    /**
     * 状态
     */
    private String status;
    /**
     * 排序序号
     */
    private Integer sortOrder;
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
