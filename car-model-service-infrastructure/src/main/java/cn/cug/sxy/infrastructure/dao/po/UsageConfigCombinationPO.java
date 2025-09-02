package cn.cug.sxy.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/5 18:33
 * @Description 用法配置组合PO
 * @Author jerryhotton
 */

@Data
public class UsageConfigCombinationPO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 用法ID
     */
    private Long usageId;
    /**
     * 组合名称
     */
    private String combinationName;
    /**
     * 排序序号
     */
    private Integer sortOrder;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

}
