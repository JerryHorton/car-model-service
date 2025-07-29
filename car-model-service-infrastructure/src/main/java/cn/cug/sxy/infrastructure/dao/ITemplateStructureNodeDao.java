package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.TemplateStructureNodePO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/29 09:43
 * @Description 车型结构树模板节点数据访问接口
 * @Author jerryhotton
 */

@Mapper
public interface ITemplateStructureNodeDao {

    /**
     * 插入节点
     *
     * @param nodePO 节点PO
     * @return 影响的行数
     */
    int insert(TemplateStructureNodePO nodePO);

    /**
     * 批量插入节点
     *
     * @param nodeList 节点PO列表
     * @return 影响的行数
     */
    int batchInsert(List<TemplateStructureNodePO> nodeList);

    /**
     * 更新节点
     *
     * @param nodePO 节点PO
     * @return 影响的行数
     */
    int update(TemplateStructureNodePO nodePO);

    /**
     * 更新节点状态
     *
     * @param templateStructureNodePO 模板结构树节点PO
     * @return 影响的行数
     */
    int updateStatus(TemplateStructureNodePO templateStructureNodePO);

    /**
     * 更新节点的父节点
     *
     * @param templateStructureNodePO 模板结构树节点PO
     * @return 影响的行数
     */
    int updateParentId(TemplateStructureNodePO templateStructureNodePO);

    /**
     * 更新节点排序序号
     *
     * @param templateStructureNodePO 模板结构树节点PO
     * @return 影响的行数
     */
    int updateSortOrder(TemplateStructureNodePO templateStructureNodePO);

    /**
     * 根据ID查询节点
     *
     * @param id 节点ID
     * @return 节点PO
     */
    TemplateStructureNodePO selectById(Long id);

    /**
     * 根据模板ID查询所有节点
     *
     * @param templateId 模板ID
     * @return 节点PO列表
     */
    List<TemplateStructureNodePO> selectByTemplateId(Long templateId);

    /**
     * 根据模板ID查询根节点
     *
     * @param templateId 模板ID
     * @return 节点PO列表
     */
    List<TemplateStructureNodePO> selectRootNodesByTemplateId(Long templateId);

    /**
     * 根据父节点ID查询子节点
     *
     * @param parentId 父节点ID
     * @return 节点PO列表
     */
    List<TemplateStructureNodePO> selectByParentId(Long parentId);

    /**
     * 根据模板ID和节点类型查询节点
     *
     * @param templateStructureNodePO 模板结构树节点PO
     * @return 节点PO列表
     */
    List<TemplateStructureNodePO> selectByTemplateIdAndNodeType(TemplateStructureNodePO templateStructureNodePO);

    /**
     * 根据模板ID和状态查询节点
     *
     * @param templateStructureNodePO 模板结构树节点PO
     * @return 节点PO列表
     */
    List<TemplateStructureNodePO> selectByTemplateIdAndStatus(TemplateStructureNodePO templateStructureNodePO);

    /**
     * 根据模板ID和节点名称关键字查询节点
     *
     * @param templateStructureNodePO 模板结构树节点PO
     * @return 节点PO列表
     */
    List<TemplateStructureNodePO> selectByTemplateIdAndNameLike(TemplateStructureNodePO templateStructureNodePO);

    /**
     * 查询节点的子树
     *
     * @param nodePath 节点路径前缀
     * @return 节点PO列表
     */
    List<TemplateStructureNodePO> selectByNodePathStartWith(String nodePath);

    /**
     * 根据节点路径模式查询节点
     *
     * @param pathPattern 路径匹配模式，支持SQL LIKE语法的通配符
     * @return 匹配指定路径模式的节点列表
     */
    List<TemplateStructureNodePO> selectByNodePathLike(String pathPattern);

    /**
     * 删除节点
     *
     * @param id 节点ID
     * @return 影响的行数
     */
    int deleteById(Long id);

    /**
     * 根据模板ID删除所有节点
     *
     * @param templateId 模板ID
     * @return 影响的行数
     */
    int deleteByTemplateId(Long templateId);

    /**
     * 根据节点路径前缀删除节点
     *
     * @param nodePath 节点路径前缀
     * @return 影响的行数
     */
    int deleteByNodePathStartWith(String nodePath);

    /**
     * 查询节点的子节点数量
     *
     * @param id 节点ID
     * @return 子节点数量
     */
    int countByParentId(Long id);

    /**
     * 查询模板节点总数
     *
     * @param templateId 模板ID
     * @return 节点总数
     */
    int countByTemplateId(Long templateId);

}
