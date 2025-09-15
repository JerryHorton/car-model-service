package cn.cug.sxy.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/9/10 10:11
 * @Description 系统分组更新请求DTO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemGroupUpdateRequestDTO {

    /**
     * 系统分组ID
     */
    @NotNull(message = "系统分组ID不能为空")
    private Long groupId;
    /**
     * 系统大类ID
     */
    @NotNull(message = "系统大类ID不能为空")
    private Long categoryId;
    /**
     * 系统分组编码
     */
    @NotBlank(message = "系统分组编码不能为空")
    private String groupCode;
    /**
     * 系统分组名称
     */
    @NotBlank(message = "系统分组名称不能为空")
    private String groupName;
    /**
     * 系统分组英文名称
     */
    private String groupNameEn;
    /**
     * 排序顺序
     */
    private Integer sortOrder;

}
