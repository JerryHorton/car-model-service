package cn.cug.sxy.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/9/10 09:47
 * @Description 系统大类更新请求DTO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemCategoryUpdateRequestDTO {

    /**
     * 系统大类ID
     */
    @NotNull(message = "系统大类ID不能为空")
    private Long categoryId;
    /**
     * 系统大类编码
     */
    @NotBlank(message = "系统大类编码不能为空")
    private String categoryCode;
    /**
     * 系统大类名称
     */
    @NotBlank(message = "系统大类名称不能为空")
    private String categoryName;
    /**
     * 系统大类英文名称
     */
    private String categoryNameEn;
    /**
     * 排序顺序
     */
    private Integer sortOrder;

}
