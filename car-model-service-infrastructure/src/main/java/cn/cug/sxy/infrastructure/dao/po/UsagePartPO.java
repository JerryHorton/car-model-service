package cn.cug.sxy.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/27 13:47
 * @Description 用法备件持久化对象
 * @Author jerryhotton
 */

@Data
public class UsagePartPO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 用法ID
     */
    private Long usageId;
    /**
     * 备件ID
     */
    private Long partId;
    /**
     * 数量
     */
    private Integer count;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
