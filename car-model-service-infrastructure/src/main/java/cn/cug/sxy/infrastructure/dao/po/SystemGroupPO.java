package cn.cug.sxy.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/7/28 08:57
 * @Description 系统分组持久化对象
 * @Author jerryhotton
 */

@Data
public class SystemGroupPO {

    /**
     * 自增ID
     */
    private Long id;
    /**
     * 归属系统大类编码
     */
    private Long categoryId;
    /**
     * 系统分组编码
     */
    private String groupCode;
    /**
     * 系统分组名称
     */
    private String groupName;
    /**
     * 系统分组英文名称
     */
    private String groupNameEn;
    /**
     * 排序
     */
    private Integer sortOrder;
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
