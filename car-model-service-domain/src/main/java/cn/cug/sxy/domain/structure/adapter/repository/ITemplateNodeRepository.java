package cn.cug.sxy.domain.structure.adapter.repository;

import cn.cug.sxy.domain.structure.model.entity.StructureTemplateNodeEntity;
import cn.cug.sxy.domain.structure.model.valobj.NodeType;
import cn.cug.sxy.types.enums.Status;
import cn.cug.sxy.domain.structure.model.valobj.TemplateId;
import cn.cug.sxy.domain.structure.model.valobj.TemplateNodeId;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/28 18:36
 * @Description 车型结构树模板节点仓储接口
 * @Author jerryhotton
 */

public interface ITemplateNodeRepository {

    /**
     * 保存节点
     *
     * @param node 节点实体
     */
    void save(StructureTemplateNodeEntity node);

    /**
     * 批量保存节点
     *
     * @param nodes 节点实体列表
     * @return 保存的记录数
     */
    int saveBatch(List<StructureTemplateNodeEntity> nodes);

    /**
     * 更新节点
     *
     * @param node 节点实体
     * @return 更新的记录数
     */
    int update(StructureTemplateNodeEntity node);

    /**
     * 更新节点状态
     *
     * @param nodeId 节点ID
     * @param status 状态
     * @return 更新的记录数
     */
    int updateStatus(TemplateNodeId nodeId, Status status);

    /**
     * 更新节点的父节点
     *
     * @param nodeId   节点ID
     * @param parentId 父节点ID，如为null则表示为根节点
     * @return 更新的记录数
     */
    int updateParentId(TemplateNodeId nodeId, TemplateNodeId parentId);

    /**
     * 更新节点排序序号
     *
     * @param nodeId    节点ID
     * @param sortOrder 排序序号
     * @return 更新的记录数
     */
    int updateSortOrder(TemplateNodeId nodeId, Integer sortOrder);

    /**
     * 根据ID查询节点
     *
     * @param nodeId 节点ID
     * @return 节点实体
     */
    Optional<StructureTemplateNodeEntity> findById(TemplateNodeId nodeId);

    /**
     * 根据模板ID查询所有节点
     *
     * @param templateId 模板ID
     * @return 节点实体列表
     */
    List<StructureTemplateNodeEntity> findByTemplateId(TemplateId templateId);

    /**
     * 根据模板ID查询所有根节点
     *
     * @param templateId 模板ID
     * @return 根节点实体列表
     */
    List<StructureTemplateNodeEntity> findRootNodesByTemplateId(TemplateId templateId);

    /**
     * 根据父节点ID查询子节点
     *
     * @param parentId 父节点ID
     * @return 子节点实体列表
     */
    List<StructureTemplateNodeEntity> findByParentId(TemplateNodeId parentId);

    /**
     * 根据模板ID和节点类型查询节点
     *
     * @param templateId 模板ID
     * @param nodeType   节点类型
     * @return 节点实体列表
     */
    List<StructureTemplateNodeEntity> findByTemplateIdAndNodeType(TemplateId templateId, NodeType nodeType);

    /**
     * 根据模板ID和状态查询节点
     *
     * @param templateId 模板ID
     * @param status     状态
     * @return 节点实体列表
     */
    List<StructureTemplateNodeEntity> findByTemplateIdAndStatus(TemplateId templateId, Status status);

    /**
     * 根据模板ID和节点名称关键字查询节点
     *
     * @param templateId 模板ID
     * @param nameKeyword 名称关键字
     * @return 节点实体列表
     */
    List<StructureTemplateNodeEntity> findByTemplateIdAndNameLike(TemplateId templateId, String nameKeyword);

    /**
     * 查询节点及其所有子节点
     *
     * @param rootNodeId 根节点ID
     * @return 节点实体列表（包含根节点自身）
     */
    List<StructureTemplateNodeEntity> findSubTree(TemplateNodeId rootNodeId);

    /**
     * 根据路径前缀查询节点
     *
     * @param pathPrefix 路径前缀
     * @return 节点实体列表
     */
    List<StructureTemplateNodeEntity> findByPathStartingWith(String pathPrefix);

    /**
     * 根据路径模糊查询节点
     *
     * @param pathPattern 路径模式
     * @return 节点实体列表
     */
    List<StructureTemplateNodeEntity> findByPathLike(String pathPattern);

    /**
     * 删除节点
     *
     * @param nodeId 节点ID
     * @return 删除的记录数
     */
    int deleteById(TemplateNodeId nodeId);

    /**
     * 删除节点及其所有子节点
     *
     * @param rootNodeId 根节点ID
     * @return 删除的记录数
     */
    int deleteSubTree(TemplateNodeId rootNodeId);

    /**
     * 删除模板的所有节点
     *
     * @param templateId 模板ID
     * @return 删除的记录数
     */
    int deleteByTemplateId(TemplateId templateId);

    /**
     * 检查节点是否有子节点
     *
     * @param nodeId 节点ID
     * @return 是否有子节点
     */
    boolean hasChildren(TemplateNodeId nodeId);

    /**
     * 获取模板的节点总数
     *
     * @param templateId 模板ID
     * @return 节点总数
     */
    int countByTemplateId(TemplateId templateId);

}
