package cn.cug.sxy.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @version 1.0
 * @Date 2025/8/7 09:04
 * @Description 更新配置类别请求DTO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigCategoryUpdateRequestDTO implements Serializable {

    /**
     * 类别ID
     */
    @NotNull(message = "配置类别ID不能为空")
    private Long categoryId;
    /**
     * 类别编码
     */
    @NotBlank(message = "配置类别编码不能为空")
    private String categoryCode;
    /**
     * 类别名称
     */
    @NotBlank(message = "配置类别名称不能为空")
    private String categoryName;
    /**
     * 排序序号
     */
    private Integer sortOrder;

}
