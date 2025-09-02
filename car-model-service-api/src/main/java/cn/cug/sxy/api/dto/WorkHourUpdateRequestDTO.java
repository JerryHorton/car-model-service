package cn.cug.sxy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @version 1.0
 * @Date 2025/1/27 10:00
 * @Description 工时更新请求DTO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkHourUpdateRequestDTO {

    /**
     * 工时ID
     */
    @NotNull(message = "工时ID不能为空")
    private Long workHourId;
    /**
     * 工时描述
     */
    private String description;
    /**
     * 标准工时（小时）
     */
    private BigDecimal standardHours;
    /**
     * 步骤顺序号（子工时时可用）
     */
    private Integer stepOrder;

} 