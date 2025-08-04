package cn.cug.sxy.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @version 1.0
 * @Date 2025/7/31 16:02
 * @Description 添加节点请求DTO
 * @Author jerryhotton
 */

@Data
public class InstanceAddNodeRequestDTO {

    /**
     * 实例ID
     */
    @NotNull(message = "实例ID不能为空")
    private Long instanceId;
    /**
     * 父节点ID，根节点为null
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
     * 系统大类ID，仅当节点类型为CATEGORY时有值
     */
    private Long categoryId;
    /**
     * 系统组ID，仅当节点类型为GROUP时有值
     */
    private Long groupId;
    /**
     * 用法ID，仅当节点类型为USAGE时有值
     */
    private Long usageId;
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
