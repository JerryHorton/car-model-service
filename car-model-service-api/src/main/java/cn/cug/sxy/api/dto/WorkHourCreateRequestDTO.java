package cn.cug.sxy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @version 1.0
 * @Date 2025/1/27 10:00
 * @Description 工时创建请求DTO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkHourCreateRequestDTO {

    /**
     * 工时代码
     */
    @NotBlank(message = "工时代码不能为空")
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
    @NotBlank(message = "工时类型不能为空")
    private String type;

    /**
     * 父工时ID（创建子工时时必填）
     */
    private Long parentId;

    /**
     * 步骤顺序号（子工时时必填）
     */
    private Integer stepOrder;

    /**
     * 创建人
     */
    @NotBlank(message = "创建人不能为空")
    private String creator;
} 