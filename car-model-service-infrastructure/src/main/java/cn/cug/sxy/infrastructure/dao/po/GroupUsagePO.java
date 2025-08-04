package cn.cug.sxy.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/4 16:15
 * @Description 系统分组用法持久化对象
 * @Author jerryhotton
 */

@Data
public class GroupUsagePO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 归属系统分组ID
     */
    private Long groupId;
    /**
     * 用法ID
     */
    private Long usageId;
    /**
     * 用法名称
     */
    private String usageName;
    /**
     * 爆炸图URL
     */
    private String explodedViewImg;
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
