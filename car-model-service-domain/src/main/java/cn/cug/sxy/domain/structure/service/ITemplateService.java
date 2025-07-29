package cn.cug.sxy.domain.structure.service;

import cn.cug.sxy.domain.structure.model.entity.StructureTemplateEntity;
import cn.cug.sxy.domain.structure.model.entity.StructureTemplateNodeEntity;
import cn.cug.sxy.domain.structure.model.valobj.*;

import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @Date 2025/7/28 18:30
 * @Description 车型结构树模板服务接口
 * @Author jerryhotton
 */

public interface ITemplateService {

    /**
     * 创建新模板
     *
     * @param templateCode 模板编码
     * @param templateName 模板名称
     * @param templateDesc 模板描述
     * @param version      版本号
     * @param creator      创建者
     * @return 创建的模板实体
     * @throws IllegalArgumentException 当参数无效时抛出
     */
    StructureTemplateEntity createTemplate(TemplateCode templateCode, String templateName,
                                           String templateDesc, String version, String creator);

    /**
     * 更新模板基本信息
     *
     * @param templateId   模板ID
     * @param templateName 模板名称
     * @param templateDesc 模板描述
     * @return 更新是否成功
     */
    boolean updateTemplateInfo(TemplateId templateId, String templateName, String templateDesc);

    /**
     * 创建新版本模板并复制节点结构
     *
     * @param sourceTemplateId 源模板ID
     * @param newVersion       新版本号
     * @param creator          创建者
     * @return 新创建的模板实体
     */
    StructureTemplateEntity createNewVersionWithNodes(TemplateId sourceTemplateId, String newVersion, String creator);

    /**
     * 添加节点到模板
     *
     * @param templateId   模板ID
     * @param parentNodeId 父节点ID，如为null则表示添加根节点
     * @param nodeName     节点名称
     * @param nodeNameEn   节点英文名称
     * @param nodeType     节点类型
     * @param categoryId   系统大类ID
     * @param groupId      系统组ID
     * @param sortOrder    排序序号
     * @param creator      创建者
     * @return 创建的节点实体
     */
    StructureTemplateNodeEntity addNode(TemplateId templateId, TemplateNodeId parentNodeId,
                                        String nodeName, String nodeNameEn, String nodeType,
                                        Long categoryId, Long groupId, Integer sortOrder, String creator);

    /**
     * 更新节点基本信息
     *
     * @param nodeId     节点ID
     * @param nodeName   节点名称
     * @param nodeNameEn 节点英文名称
     * @param sortOrder  排序序号
     * @return 更新是否成功
     */
    boolean updateNodeInfo(TemplateNodeId nodeId, String nodeName, String nodeNameEn, Integer sortOrder);

    /**
     * 移动节点（更改父节点）
     *
     * @param nodeId      要移动的节点ID
     * @param newParentId 新的父节点ID，如为null则表示移动为根节点
     * @param sortOrder   新位置的排序序号
     * @return 移动是否成功
     */
    boolean moveNode(TemplateNodeId nodeId, TemplateNodeId newParentId, Integer sortOrder);

    /**
     * 获取模板及其完整树结构
     *
     * @param templateId 模板ID
     * @return 模板及其节点树
     */
    Map<String, Object> getTemplateWithFullTree(TemplateId templateId);

    /**
     * 根据模板编码和版本获取模板及其完整树结构
     *
     * @param templateCode 模板编码
     * @param version      版本号
     * @return 模板及其节点树
     */
    Map<String, Object> getTemplateWithFullTreeByCodeAndVersion(TemplateCode templateCode, String version);

    /**
     * 删除模板及其所有节点
     *
     * @param templateId 模板ID
     * @return 操作是否成功
     */
    boolean deleteTemplateWithAllNodes(TemplateId templateId);

    /**
     * 导入节点树
     *
     * @param templateId   目标模板ID
     * @param parentNodeId 父节点ID，如为null则导入为根节点
     * @param nodes        要导入的节点列表
     * @param creator      创建者
     * @return 成功导入的节点数量
     */
    int importNodeTree(TemplateId templateId, TemplateNodeId parentNodeId,
                       List<StructureTemplateNodeEntity> nodes, String creator);

    /**
     * 校验模板结构的有效性
     *
     * @param templateId 模板ID
     * @return 校验结果，包含是否有效及问题列表
     */
    Map<String, Object> validateTemplateStructure(TemplateId templateId);

    /**
     * 查询模板列表
     *
     * @param templateCode 模板编码
     * @return 不同版本的模板列表
     */
    List<StructureTemplateEntity> findTemplates(TemplateCode templateCode, Status status, String nameKeyword);

    /**
     * 查询模板列表（分页效果）
     *
     * @param templateCode 模板编码
     * @return 模板列表页面
     */
    StructureTemplatePageVO findTemplates(TemplateCode templateCode, Status status, String nameKeyword, int pageNo, int pageSize);


    /**
     * 删除节点及其子节点
     *
     * @param nodeId 节点ID
     * @return 删除的节点数量
     */
    int deleteNodeAndChildren(TemplateNodeId nodeId);

    /**
     * 启用模板
     *
     * @param templateId 模板ID
     * @return 操作是否成功
     */
    boolean enableTemplate(TemplateId templateId);

    /**
     * 禁用模板
     *
     * @param templateId 模板ID
     * @return 操作是否成功
     */
    boolean disableTemplate(TemplateId templateId);

    /**
     * 根据ID查询模板
     *
     * @param templateId 模板ID
     * @return 模板实体
     */
    StructureTemplateEntity findTemplateById(TemplateId templateId);

    /**
     * 根据ID查询节点
     *
     * @param nodeId 节点ID
     * @return 节点实体
     */
    StructureTemplateNodeEntity findNodeById(TemplateNodeId nodeId);

    /**
     * 查询节点的所有子节点
     *
     * @param parentNodeId 父节点ID
     * @return 子节点列表
     */
    List<StructureTemplateNodeEntity> findChildNodes(TemplateNodeId parentNodeId);

    /**
     * 查询节点及其所有子节点（子树）
     *
     * @param rootNodeId 根节点ID
     * @return 子树节点列表
     */
    List<StructureTemplateNodeEntity> findSubTree(TemplateNodeId rootNodeId);

}
