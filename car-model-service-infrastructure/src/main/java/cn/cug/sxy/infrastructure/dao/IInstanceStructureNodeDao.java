package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.InstanceStructureNodePO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/30 17:35
 * @Description 车型结构树实例节点数据访问接口
 * @Author jerryhotton
 */

@Mapper
public interface IInstanceStructureNodeDao {

    /**
     * 插入节点
     *
     * @param nodePO 节点PO
     * @return 影响的行数
     */
    int insert(InstanceStructureNodePO nodePO);

    /**
     * 批量插入节点
     *
     * @param nodeList 节点PO列表
     * @return 影响的行数
     */
    int batchInsert(List<InstanceStructureNodePO> nodeList);

    /**
     * 更新节点
     *
     * @param nodePO 节点PO
     * @return 影响的行数
     */
    int update(InstanceStructureNodePO nodePO);

    /**
     * 更新节点状态
     *
     * @param nodePO 节点PO
     * @return 影响的行数
     */
    int updateStatus(InstanceStructureNodePO nodePO);

    /**
     * 更新节点的父节点
     *
     * @param nodePO 节点PO
     * @return 影响的行数
     */
    int updateParentId(InstanceStructureNodePO nodePO);

    /**
     * 更新节点排序序号
     *
     * @param nodePO 节点PO
     * @return 影响的行数
     */
    int updateSortOrder(InstanceStructureNodePO nodePO);

    /**
     * 根据ID查询节点
     *
     * @param id 节点ID
     * @return 节点PO
     */
    InstanceStructureNodePO selectById(Long id);

    /**
     * 根据实例ID查询所有节点
     *
     * @param instanceId 实例ID
     * @return 节点PO列表
     */
    List<InstanceStructureNodePO> selectByInstanceId(Long instanceId);

    /**
     * 根据实例ID查询根节点
     *
     * @param instanceId 实例ID
     * @return 节点PO列表
     */
    List<InstanceStructureNodePO> selectRootNodesByInstanceId(Long instanceId);

    /**
     * 根据父节点ID查询子节点
     *
     * @param parentId 父节点ID
     * @return 节点PO列表
     */
    List<InstanceStructureNodePO> selectByParentId(Long parentId);

    /**
     * 根据实例ID和节点类型查询节点
     *
     * @param nodePO 节点PO
     * @return 节点PO列表
     */
    List<InstanceStructureNodePO> selectByInstanceIdAndNodeType(InstanceStructureNodePO nodePO);

    /**
     * 根据实例ID和状态查询节点
     *
     * @param nodePO 节点PO
     * @return 节点PO列表
     */
    List<InstanceStructureNodePO> selectByInstanceIdAndStatus(InstanceStructureNodePO nodePO);

    /**
     * 根据实例ID和节点名称关键字查询节点
     *
     * @param nodePO 节点PO
     * @return 节点PO列表
     */
    List<InstanceStructureNodePO> selectByInstanceIdAndNameLike(InstanceStructureNodePO nodePO);

    /**
     * 查询节点的子树
     *
     * @param nodePath 节点路径前缀
     * @return 节点PO列表
     */
    List<InstanceStructureNodePO> selectByNodePathStartWith(String nodePath);

    /**
     * 根据节点路径模式查询节点
     *
     * @param pathPattern 路径匹配模式，支持SQL LIKE语法的通配符
     * @return 匹配指定路径模式的节点列表
     */
    List<InstanceStructureNodePO> selectByNodePathLike(String pathPattern);

    /**
     * 删除节点
     *
     * @param id 节点ID
     * @return 影响的行数
     */
    int deleteById(Long id);

    /**
     * 根据实例ID删除所有节点
     *
     * @param instanceId 实例ID
     * @return 影响的行数
     */
    int deleteByInstanceId(Long instanceId);

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
     * @param parentId 父节点ID
     * @return 子节点数量
     */
    int countByParentId(Long parentId);

    /**
     * 查询实例节点总数
     *
     * @param instanceId 实例ID
     * @return 节点总数
     */
    int countByInstanceId(Long instanceId);

}
