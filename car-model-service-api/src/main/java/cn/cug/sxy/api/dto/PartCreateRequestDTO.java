package cn.cug.sxy.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @version 1.0
 * @Date 2025/9/3
 * @Description 备件创建请求
 * @Author jerryhotton
 */
@Data
public class PartCreateRequestDTO {

    /**
     * 备件编码
     */
    @NotBlank
    private String partCode;
    /**
     * 备件名称
     */
    @NotBlank
    private String partName;
    /**
     * 创建人
     */
    @NotBlank
    private String creator;
    /**
     * 备注
     */
    @NotBlank
    private String remark;

}