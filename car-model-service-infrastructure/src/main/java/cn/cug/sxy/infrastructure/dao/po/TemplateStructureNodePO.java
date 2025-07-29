package cn.cug.sxy.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/7/28 19:17
 * @Description 车型结构树模板节点持久化对象
 * @Author jerryhotton
 */

@Data
public class TemplateStructureNodePO {

    /**
     * 节点ID
     */
    private Long id;
    /**
     * 模板ID
     */
    private Long templateId;
    /**
     * 父节点ID，根节点为null
     */
    private Long parentId;
    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 节点英文名称
     */
    private String nodeNameEn;
    /**
     * 节点类型：CATEGORY-系统大类, GROUP-系统组, PART-备件, OTHER-其他
     */
    private String nodeType;
    /**
     * 节点编码
     */
    private String nodeCode;
    /**
     * 系统大类ID，仅当节点类型为CATEGORY时有值
     */
    private Long categoryId;
    /**
     * 系统组ID，仅当节点类型为GROUP时有值
     */
    private Long groupId;
    /**
     * 排序序号
     */
    private Integer sortOrder;
    /**
     * 节点层级，从0开始
     */
    private Integer nodeLevel;
    /**
     * 节点路径，格式：rootId-parentId-...
     */
    private String nodePath;
    /**
     * 状态 禁用、启用、删除
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
