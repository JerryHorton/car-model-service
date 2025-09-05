package cn.cug.sxy.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @version 1.0
 * @Date 2025/9/4 14:44
 * @Description 禁用备件请求参数
 * @Author jerryhotton
 */

@Data
public class ChangePartStatusRequestDTO {

    /**
     * 备件ID
     */
    @NotNull(message = "备件ID不能为空")
    private Long partId;

}
