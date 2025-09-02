package cn.cug.sxy.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0
 * @Date 2025/7/31 15:55
 * @Description 实例创建请求DTO
 * @Author jerryhotton
 */

@Data
public class InstanceCreateRequestDTO implements Serializable {

    /**
     * 实例编码
     */
    @NotBlank(message = "实例编码不能为空")
    private String instanceCode;
    /**
     * 实例名称
     */
    private String instanceName;
    /**
     * 实例描述
     */
    private String instanceDesc;
    /**
     * 车型系列ID
     */
    private Long seriesId;
    /**
     * 车型ID
     */
    private Long modelId;
    /**
     * 模板ID
     */
    @NotNull(message = "模板ID不能为空")
    private Long templateId;
    /**
     * 实例版本
     */
    @NotBlank(message = "实例版本不能为空")
    private String version;
    /**
     * 创建人
     */
    @NotBlank(message = "创建人不能为空")
    private String creator;

}
