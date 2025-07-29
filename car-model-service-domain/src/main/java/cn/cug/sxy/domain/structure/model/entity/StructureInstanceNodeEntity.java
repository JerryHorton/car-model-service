package cn.cug.sxy.domain.structure.model.entity;

import cn.cug.sxy.domain.structure.model.valobj.InstanceId;
import cn.cug.sxy.domain.structure.model.valobj.InstanceNodeId;
import cn.cug.sxy.domain.structure.model.valobj.NodeType;
import cn.cug.sxy.domain.structure.model.valobj.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/7/28 15:34
 * @Description 车型结构树实例节点实体
 * @Author jerryhotton
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StructureInstanceNodeEntity {

    /**
     * 节点ID
     */
    private InstanceNodeId id;
    /**
     * 所属实例ID
     */
    private InstanceId instanceId;
    /**
     * 父节点ID，根节点为null
     */
    private InstanceNodeId parentId;
    /**
     * 节点类型
     */
    private NodeType nodeType;
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
     * 排序顺序
     */
    private Integer sortOrder;
    /**
     * 状态
     */
    private Status status;
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

    /**
     * 创建根节点
     *
     * @param instanceId 所属实例ID
     * @param nodeType   节点类型
     * @param nodeCode   节点编码
     * @param nodeName   节点名称
     * @param nodeNameEn 节点英文名称
     * @param sortOrder  排序顺序
     * @param creator    创建人
     * @return 实例节点实体
     */
    public static StructureInstanceNodeEntity createRoot(InstanceId instanceId, NodeType nodeType,
                                                         String nodeCode, String nodeName, String nodeNameEn,
                                                         Integer sortOrder, String creator) {
        return StructureInstanceNodeEntity.builder()
                .instanceId(instanceId)
                .parentId(null)
                .nodeType(nodeType)
                .nodeCode(nodeCode)
                .nodeName(nodeName)
                .nodeNameEn(nodeNameEn)
                .sortOrder(sortOrder != null ? sortOrder : 0)
                .status(Status.ENABLED)
                .creator(creator)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建子节点
     *
     * @param instanceId 所属实例ID
     * @param parentId   父节点ID
     * @param nodeType   节点类型
     * @param nodeCode   节点编码
     * @param nodeName   节点名称
     * @param nodeNameEn 节点英文名称
     * @param sortOrder  排序顺序
     * @param creator    创建人
     * @return 实例节点实体
     */
    public static StructureInstanceNodeEntity createChild(InstanceId instanceId, InstanceNodeId parentId,
                                                          NodeType nodeType, String nodeCode, String nodeName,
                                                          String nodeNameEn, Integer sortOrder, String creator) {
        return StructureInstanceNodeEntity.builder()
                .instanceId(instanceId)
                .parentId(parentId)
                .nodeType(nodeType)
                .nodeCode(nodeCode)
                .nodeName(nodeName)
                .nodeNameEn(nodeNameEn)
                .sortOrder(sortOrder != null ? sortOrder : 0)
                .status(Status.ENABLED)
                .creator(creator)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    /**
     * 更新节点基本信息
     *
     * @param nodeName   节点名称
     * @param nodeNameEn 节点英文名称
     * @param sortOrder  排序顺序
     */
    public void update(String nodeName, String nodeNameEn, Integer sortOrder) {
        this.nodeName = nodeName;
        this.nodeNameEn = nodeNameEn;
        if (sortOrder != null) {
            this.sortOrder = sortOrder;
        }
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 更新节点状态
     *
     * @param status 状态
     */
    public void updateStatus(Status status) {
        this.status = status;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 更新父节点
     *
     * @param parentId 父节点ID
     */
    public void updateParent(InstanceNodeId parentId) {
        this.parentId = parentId;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 启用节点
     */
    public void enable() {
        this.status = Status.ENABLED;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 禁用节点
     */
    public void disable() {
        this.status = Status.DISABLED;
        this.updatedTime = LocalDateTime.now();
    }

}
