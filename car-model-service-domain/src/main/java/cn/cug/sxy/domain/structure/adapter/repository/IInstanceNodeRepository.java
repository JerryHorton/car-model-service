package cn.cug.sxy.domain.structure.adapter.repository;

import cn.cug.sxy.domain.structure.model.entity.StructureInstanceNodeEntity;
import cn.cug.sxy.domain.structure.model.valobj.InstanceId;
import cn.cug.sxy.domain.structure.model.valobj.InstanceNodeId;
import cn.cug.sxy.domain.structure.model.valobj.NodeType;
import cn.cug.sxy.domain.structure.model.valobj.Status;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/30 17:04
 * @Description 车型结构树实例节点仓储接口
 * @Author jerryhotton
 */

public interface IInstanceNodeRepository {

    /**
     * 保存节点
     *
     * @param node 节点实体
     * @return 保存后的节点实体
     */
    StructureInstanceNodeEntity save(StructureInstanceNodeEntity node);

    /**
     * 批量保存节点
     *
     * @param nodes 节点实体列表
     * @return 保存的记录数
     */
    int saveBatch(List<StructureInstanceNodeEntity> nodes);

    /**
     * 更新节点
     *
     * @param node 节点实体
     * @return 更新的记录数
     */
    int update(StructureInstanceNodeEntity node);

    /**
     * 更新节点状态
     *
     * @param nodeId 节点ID
     * @param status 状态
     * @return 更新的记录数
     */
    int updateStatus(InstanceNodeId nodeId, Status status);

    /**
     * 更新节点父节点ID
     *
     * @param nodeId 节点ID
     * @param parentId 父节点ID
     * @return 更新的记录数
     */
    int updateParentId(InstanceNodeId nodeId, InstanceNodeId parentId);

    /**
     * 更新节点排序序号
     *
     * @param nodeId 节点ID
     * @param sortOrder 排序序号
     * @return 更新的记录数
     */
    int updateSortOrder(InstanceNodeId nodeId, Integer sortOrder);

    /**
     * 根据ID查询节点
     *
     * @param nodeId 节点ID
     * @return 节点Optional
     */
    Optional<StructureInstanceNodeEntity> findById(InstanceNodeId nodeId);

    /**
     * 根据实例ID查询所有节点
     *
     * @param instanceId 实例ID
     * @return 节点列表
     */
    List<StructureInstanceNodeEntity> findByInstanceId(InstanceId instanceId);

    /**
     * 根据实例ID查询根节点
     *
     * @param instanceId 实例ID
     * @return 根节点列表
     */
    List<StructureInstanceNodeEntity> findRootNodesByInstanceId(InstanceId instanceId);

    /**
     * 根据父节点ID查询子节点
     *
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    List<StructureInstanceNodeEntity> findByParentId(InstanceNodeId parentId);

    /**
     * 根据实例ID和节点类型查询节点
     *
     * @param instanceId 实例ID
     * @param nodeType 节点类型
     * @return 节点列表
     */
    List<StructureInstanceNodeEntity> findByInstanceIdAndNodeType(InstanceId instanceId, NodeType nodeType);

    /**
     * 根据实例ID和状态查询节点
     *
     * @param instanceId 实例ID
     * @param status 状态
     * @return 节点列表
     */
    List<StructureInstanceNodeEntity> findByInstanceIdAndStatus(InstanceId instanceId, Status status);

    /**
     * 根据实例ID和节点名称关键字查询节点
     *
     * @param instanceId 实例ID
     * @param nameKeyword 名称关键字
     * @return 节点列表
     */
    List<StructureInstanceNodeEntity> findByInstanceIdAndNameLike(InstanceId instanceId, String nameKeyword);

    /**
     * 查询节点的子树
     *
     * @param rootNodeId 根节点ID
     * @return 子树节点列表
     */
    List<StructureInstanceNodeEntity> findSubTree(InstanceNodeId rootNodeId);

    /**
     * 查询节点的路径前缀
     *
     * @param nodePath 节点路径前缀
     * @return 匹配前缀的节点列表
     */
    List<StructureInstanceNodeEntity> findByPathStartingWith(String nodePath);

    /**
     * 根据路径模式查询节点
     *
     * @param pathPattern 路径匹配模式
     * @return 匹配模式的节点列表
     */
    List<StructureInstanceNodeEntity> findByPathLike(String pathPattern);

    /**
     * 删除节点
     *
     * @param nodeId 节点ID
     * @return 删除的记录数
     */
    int deleteById(InstanceNodeId nodeId);

    /**
     * 根据实例ID删除所有节点
     *
     * @param instanceId 实例ID
     * @return 删除的记录数
     */
    int deleteByInstanceId(InstanceId instanceId);

    /**
     * 删除节点及其子树
     *
     * @param nodeId 节点ID
     * @return 删除的记录数
     */
    int deleteSubTree(InstanceNodeId nodeId);

    /**
     * 查询节点的子节点数量
     *
     * @param nodeId 节点ID
     * @return 子节点数量
     */
    int countByParentId(InstanceNodeId nodeId);

    /**
     * 查询实例节点总数
     *
     * @param instanceId 实例ID
     * @return 节点总数
     */
    int countByInstanceId(InstanceId instanceId);

}
