package cn.cug.sxy.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/7/30 09:05
 * @Description 新版本请求DTO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateNewVersionRequestDTO {

    /**
     * 模版ID
     */
    @NotNull(message = "模版ID不能为空")
    private Long templateId;
    /**
     * 新版本号
     */
    @NotBlank(message = "新版本号不能为空")
    private String newVersion;
    /**
     * 创建者
     */
    @NotBlank(message = "创建者不能为空")
    private String creator;

}
