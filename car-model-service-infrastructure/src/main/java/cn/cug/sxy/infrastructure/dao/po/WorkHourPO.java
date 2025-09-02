package cn.cug.sxy.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/1/27 10:00
 * @Description 工时持久化对象
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkHourPO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 父ID（主工时）
     */
    private Long parentId;

    /**
     * 工时代码
     */
    private String code;

    /**
     * 工时描述
     */
    private String description;

    /**
     * 标准工时（小时）
     */
    private BigDecimal standardHours;

    /**
     * 工时类型
     */
    private String type;

    /**
     * 步骤顺序号（主工时可为NULL，子工时需填写）
     */
    private Integer stepOrder;

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
