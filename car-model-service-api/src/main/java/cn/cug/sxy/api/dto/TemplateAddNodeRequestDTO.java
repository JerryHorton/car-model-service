package cn.cug.sxy.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/7/30 09:53
 * @Description 添加节点请求DTO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateAddNodeRequestDTO {

    /**
     * 模板ID
     */
    @NotNull(message = "模板ID不能为空")
    private Long templateId;
    /**
     * 父节点ID，如为null则表示添加根节点
     */
    private Long parentNodeId;
    /**
     * 节点名称
     */
    @NotBlank(message = "节点名称不能为空")
    private String nodeName;
    /**
     * 节点英文名称
     */
    private String nodeNameEn;
    /**
     * 节点类型
     */
    @NotBlank(message = "节点类型不能为空")
    private String nodeType;
    /**
     * 系统大类ID
     */
    private Long categoryId;
    /**
     * 系统组ID
     */
    private Long groupId;
    /**
     * 排序序号
     */
    private Integer sortOrder;
    /**
     * 创建者
     */
    @NotBlank(message = "创建者不能为空")
    private String creator;

}
