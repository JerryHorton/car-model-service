package cn.cug.sxy.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @version 1.0
 * @Date 2025/7/31 16:00
 * @Description 新版本请求DTO
 * @Author jerryhotton
 */

@Data
public class InstanceNewVersionRequestDTO {

    /**
     * 实例ID
     */
    @NotNull(message = "实例ID不能为空")
    private Long instanceId;
    /**
     * 实例描述
     */
    private String instanceDesc;
    /**
     * 新版本号
     */
    @NotBlank(message = "新版本号不能为空")
    private String newVersion;
    /**
     * 创建人
     */
    @NotBlank(message = "创建人不能为空")
    private String creator;

}
