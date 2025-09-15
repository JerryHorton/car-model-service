package cn.cug.sxy.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/9/10 10:01
 * @Description 系统分组创建请求DTO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemGroupCreateRequestDTO {

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
    /**
     * 创建人
     */
    @NotBlank(message = "创建人不能为空")
    private String creator;

}
