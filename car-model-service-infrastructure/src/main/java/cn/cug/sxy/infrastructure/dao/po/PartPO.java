package cn.cug.sxy.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/27 11:28
 * @Description 备件持久化对象
 * @Author jerryhotton
 */

@Data
public class PartPO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 备件编码
     */
    private String partCode;
    /**
     * 备件名称
     */
    private String partName;
    /**
     * 状态（默认ENABLED）
     */
    private String status;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

}
