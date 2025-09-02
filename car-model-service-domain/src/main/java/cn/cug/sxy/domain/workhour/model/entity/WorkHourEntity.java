package cn.cug.sxy.domain.workhour.model.entity;

import cn.cug.sxy.domain.workhour.model.valobj.WorkHourCode;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourId;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourStatus;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @version 1.0
 * @Date 2025/1/27 10:00
 * @Description 工时实体
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkHourEntity {

    /**
     * 工时ID
     */
    private WorkHourId id;
    /**
     * 父ID（主工时）
     */
    private WorkHourId parentId;
    /**
     * 工时代码
     */
    private WorkHourCode code;
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
    private WorkHourType type;
    /**
     * 步骤顺序号（主工时可为NULL，子工时需填写）
     */
    private Integer stepOrder;
    /**
     * 状态
     */
    private WorkHourStatus status;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 子工时列表
     */
    private List<WorkHourEntity> children;

    /**
     * 创建工时
     */
    public static WorkHourEntity create(
            WorkHourCode code,
            String description,
            BigDecimal standardHours,
            WorkHourType type,
            WorkHourId parentId,
            Integer stepOrder,
            String creator) {
        // 验证必填字段
        if (code == null) {
            throw new IllegalArgumentException("工时代码不能为空");
        }
        if (type == null) {
            throw new IllegalArgumentException("工时类型不能为空");
        }
        if (creator == null || creator.trim().isEmpty()) {
            throw new IllegalArgumentException("创建人不能为空");
        }
        // 验证子工时必须有步骤顺序号
        if (parentId != null && stepOrder == null) {
            throw new IllegalArgumentException("子工时必须填写步骤顺序号");
        }
        // 验证主工时不能有步骤顺序号
        if (parentId == null && stepOrder != null) {
            throw new IllegalArgumentException("主工时不能填写步骤顺序号");
        }

        return WorkHourEntity.builder()
                .code(code)
                .description(description)
                .standardHours(standardHours)
                .type(type)
                .parentId(parentId)
                .stepOrder(stepOrder)
                .status(WorkHourStatus.ENABLED)
                .creator(creator)
                .build();
    }

    /**
     * 判断是否为主工时
     */
    public boolean isMainWorkHour() {
        return parentId == null;
    }

    /**
     * 判断是否为子工时
     */
    public boolean isSubWorkHour() {
        return parentId != null;
    }

    /**
     * 更新工时信息
     */
    public void update(String description, BigDecimal standardHours, Integer stepOrder) {
        if (description != null) {
            this.description = description;
        }
        if (standardHours != null) {
            this.standardHours = standardHours;
        }
        if (stepOrder != null) {
            // 验证主工时不能有步骤顺序号
            if (parentId == null) {
                throw new IllegalArgumentException("主工时不能设置步骤顺序号");
            }
            this.stepOrder = stepOrder;
        }
    }

    /**
     * 启用工时
     */
    public void enable() {
        this.status = WorkHourStatus.ENABLED;
    }

    /**
     * 禁用工时
     */
    public void disable() {
        this.status = WorkHourStatus.DISABLED;
    }

    /**
     * 删除工时
     */
    public void delete() {
        this.status = WorkHourStatus.DELETED;
    }

} 