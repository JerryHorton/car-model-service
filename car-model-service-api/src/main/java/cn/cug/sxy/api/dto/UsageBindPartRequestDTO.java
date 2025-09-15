package cn.cug.sxy.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @version 1.0
 * @Date 2025/9/5 10:58
 * @Description 用法绑定备件请求DTO
 * @Author jerryhotton
 */

@Data
public class UsageBindPartRequestDTO {

    /**
     * 用法ID
     */
    @NotNull(message = "用法ID不能为空")
    private Long usageId;
    /**
     * 备件ID
     */
    @NotNull(message = "备件ID不能为空")
    private Long partId;
    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    private Integer count;

}
