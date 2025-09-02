package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/31 16:05
 * @Description 节点树VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstanceNodeTreeVO implements Serializable {

    /**
     * 节点ID
     */
    private Long id;
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
     * 父节点ID，根节点为null
     */
    private Long parentId;
    /**
     * 节点路径
     */
    private String nodePath;
    /**
     * 节点层级
     */
    private Integer nodeLevel;
    /**
     * 子节点列表
     */
    private List<InstanceNodeTreeVO> children;

}
