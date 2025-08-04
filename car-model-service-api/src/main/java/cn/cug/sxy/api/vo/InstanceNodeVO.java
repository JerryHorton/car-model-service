package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/7/31 16:23
 * @Description 节点VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstanceNodeVO {

    /**
     * 节点ID
     */
    private Long id;
    /**
     * 所属实例ID
     */
    private Long instanceId;
    /**
     * 父节点ID，根节点为null
     */
    private Long parentId;
    /**
     * 节点编码
     */
    private String nodeCode;
    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 节点英文名称
     */
    private String nodeNameEn;
    /**
     * 节点类型
     */
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
     * 节点路径
     */
    private String nodePath;
    /**
     * 节点层级
     */
    private Integer nodeLevel;
    /**
     * 状态
     */
    private String status;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

}
