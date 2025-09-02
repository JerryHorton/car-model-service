package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/1/27 10:00
 * @Description 工时VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkHourVO {

    /**
     * 工时ID
     */
    private Long id;

    /**
     * 父工时ID
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
     * 工时类型描述
     */
    private String typeDescription;

    /**
     * 步骤顺序号
     */
    private Integer stepOrder;

    /**
     * 状态
     */
    private String status;

    /**
     * 状态描述
     */
    private String statusDescription;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 子工时列表
     */
    private List<WorkHourVO> children;

    /**
     * 是否为主工时
     */
    private Boolean isMainWorkHour;

    /**
     * 是否为子工时
     */
    private Boolean isSubWorkHour;
} 