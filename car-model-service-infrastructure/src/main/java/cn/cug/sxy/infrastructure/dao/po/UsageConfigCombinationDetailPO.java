package cn.cug.sxy.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/5 18:47
 * @Description 用法配置组合明细PO
 * @Author jerryhotton
 */

@Data
public class UsageConfigCombinationDetailPO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 组合ID
     */
    private Long combinationId;
    /**
     * 配置项ID
     */
    private Long configItemId;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

}
