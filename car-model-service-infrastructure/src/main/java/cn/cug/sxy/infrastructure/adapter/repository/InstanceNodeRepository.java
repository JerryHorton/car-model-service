package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.structure.adapter.repository.IInstanceNodeRepository;
import cn.cug.sxy.domain.structure.model.entity.StructureInstanceNodeEntity;
import cn.cug.sxy.domain.structure.model.valobj.*;
import cn.cug.sxy.infrastructure.converter.InstanceStructureNodeConverter;
import cn.cug.sxy.infrastructure.dao.IInstanceStructureNodeDao;
import cn.cug.sxy.infrastructure.dao.po.InstanceStructureNodePO;
import cn.cug.sxy.infrastructure.redis.IRedisService;
import cn.cug.sxy.types.common.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/31 10:18
 * @Description 车型结构树实例节点仓储实现
 * @Author jerryhotton
 */

@Repository
public class InstanceNodeRepository extends AbstractRepository implements IInstanceNodeRepository {

    private final IInstanceStructureNodeDao instanceStructureNodeDao;
    private final IRedisService redisService;

    public InstanceNodeRepository(
            IInstanceStructureNodeDao instanceStructureNodeDao,
            IRedisService redisService) {
        this.instanceStructureNodeDao = instanceStructureNodeDao;
        this.redisService = redisService;
    }

    @Override
    public StructureInstanceNodeEntity save(StructureInstanceNodeEntity node) {
        InstanceStructureNodePO po = InstanceStructureNodeConverter.toPO(node);
        instanceStructureNodeDao.insert(po);
        // 设置ID
        if (po.getId() != null || node.getId() == null) {
            node.setId(new InstanceNodeId(po.getId()));
        }
        // 清除实例节点列表缓存
        clearCacheOnSave(node);

        return node;
    }

    @Override
    public int saveBatch(List<StructureInstanceNodeEntity> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return 0;
        }
        List<InstanceStructureNodePO> poList = InstanceStructureNodeConverter.toPOList(nodes);
        int result = instanceStructureNodeDao.batchInsert(poList);
        // 更新实体ID
        for (int i = 0; i < poList.size(); i++) {
            if (poList.get(i).getId() != null && nodes.get(i).getId() == null) {
                nodes.get(i).setId(new InstanceNodeId(poList.get(i).getId()));
            }
            // 清除父节点的子节点列表缓存
            if (poList.get(i).getParentId() != null) {
                String cacheKey = getNodesByParentIdCacheKey(poList.get(i).getParentId());
                redisService.remove(cacheKey);
            }
        }
        // 清除实例节点列表缓存
        if (!nodes.isEmpty() && nodes.get(0).getInstanceId() != null) {
            String cacheKey = getNodesByInstanceIdCacheKey(nodes.get(0).getInstanceId().getId());
            redisService.remove(cacheKey);
        }

        return result;
    }

    @Override
    public int update(StructureInstanceNodeEntity node) {
        if (node == null) {
            return 0;
        }
        InstanceStructureNodePO po = InstanceStructureNodeConverter.toPO(node);
        int result = instanceStructureNodeDao.update(po);
        // 清除相关缓存
        clearCache(node);

        return result;
    }

    @Override
    public int updateStatus(InstanceNodeId nodeId, Status status) {
        if (nodeId == null || status == null) {
            return 0;
        }
        // 先查询节点
        Optional<StructureInstanceNodeEntity> nodeOpt = findById(nodeId);
        if (!nodeOpt.isPresent()) {
            return 0;
        }
        StructureInstanceNodeEntity node = nodeOpt.get();
        node.updateStatus(status);
        InstanceStructureNodePO po = InstanceStructureNodeConverter.toPO(node);
        int result = instanceStructureNodeDao.updateStatus(po);
        // 删除缓存
        if (result > 0) {
            clearCache(node);
        }

        return result;
    }

    @Override
    public int updateParentId(InstanceNodeId nodeId, InstanceNodeId parentId) {
        if (nodeId == null || parentId == null) {
            return 0;
        }
        // 先查询节点
        Optional<StructureInstanceNodeEntity> nodeOpt = findById(nodeId);
        if (!nodeOpt.isPresent()) {
            return 0;
        }
        StructureInstanceNodeEntity node = nodeOpt.get();
        node.updateParent(parentId);
        InstanceStructureNodePO po = InstanceStructureNodeConverter.toPO(node);
        int result = instanceStructureNodeDao.updateParentId(po);
        // 删除缓存
        if (result > 0) {
            clearCacheForNodeMove(nodeId, parentId);
        }

        return result;
    }

    @Override
    public int updateSortOrder(InstanceNodeId nodeId, Integer sortOrder) {
        if (nodeId == null || sortOrder == null) {
            return 0;
        }
        InstanceStructureNodePO po = new InstanceStructureNodePO();
        po.setId(nodeId.getId());
        po.setSortOrder(sortOrder);
        int result = instanceStructureNodeDao.updateSortOrder(po);
        // 删除缓存
        if (result > 0) {
            findById(nodeId).ifPresent(this::clearCache);
        }

        return result;
    }

    @Override
    public Optional<StructureInstanceNodeEntity> findById(InstanceNodeId nodeId) {
        if (nodeId == null) {
            return Optional.empty();
        }
        InstanceStructureNodePO instanceStructureNodePO = getDataFromCacheOrDB(getNodeByIdCacheKey(nodeId.getId()), () ->
                instanceStructureNodeDao.selectById(nodeId.getId()));
        if (instanceStructureNodePO == null) {
            return Optional.empty();
        }
        StructureInstanceNodeEntity entity = InstanceStructureNodeConverter.toEntity(instanceStructureNodePO);

        return Optional.of(entity);
    }

    @Override
    public List<StructureInstanceNodeEntity> findByInstanceId(InstanceId instanceId) {
        if (instanceId == null) {
            return Collections.emptyList();
        }
        List<InstanceStructureNodePO> instanceStructureNodePOList = getDataFromCacheOrDB(getNodesByInstanceIdCacheKey(instanceId.getId()), () ->
                instanceStructureNodeDao.selectByInstanceId(instanceId.getId()));
        if (CollectionUtils.isEmpty(instanceStructureNodePOList)) {
            return Collections.emptyList();
        }

        return InstanceStructureNodeConverter.toEntityList(instanceStructureNodePOList);
    }

    @Override
    public List<StructureInstanceNodeEntity> findRootNodesByInstanceId(InstanceId instanceId) {
        if (instanceId == null) {
            return Collections.emptyList();
        }
        List<InstanceStructureNodePO> instanceStructureNodePOList = instanceStructureNodeDao.selectRootNodesByInstanceId(instanceId.getId());
        if (CollectionUtils.isEmpty(instanceStructureNodePOList)) {
            return Collections.emptyList();
        }

        return InstanceStructureNodeConverter.toEntityList(instanceStructureNodePOList);
    }

    @Override
    public List<StructureInstanceNodeEntity> findByParentId(InstanceNodeId parentId) {
        if (parentId == null) {
            return Collections.emptyList();
        }
        List<InstanceStructureNodePO> instanceStructureNodePOList = getDataFromCacheOrDB(getNodesByParentIdCacheKey(parentId.getId()), () ->
                instanceStructureNodeDao.selectByParentId(parentId.getId()));
        if (CollectionUtils.isEmpty(instanceStructureNodePOList)) {
            return Collections.emptyList();
        }

        return InstanceStructureNodeConverter.toEntityList(instanceStructureNodePOList);
    }

    @Override
    public List<StructureInstanceNodeEntity> findByInstanceIdAndNodeType(InstanceId instanceId, NodeType nodeType) {
        if (instanceId == null || nodeType == null) {
            return Collections.emptyList();
        }
        InstanceStructureNodePO po = new InstanceStructureNodePO();
        po.setInstanceId(instanceId.getId());
        po.setNodeType(nodeType.getCode());
        List<InstanceStructureNodePO> poList = instanceStructureNodeDao.selectByInstanceIdAndNodeType(po);
        if (CollectionUtils.isEmpty(poList)) {
            return Collections.emptyList();
        }

        return InstanceStructureNodeConverter.toEntityList(poList);
    }

    @Override
    public List<StructureInstanceNodeEntity> findByInstanceIdAndStatus(InstanceId instanceId, Status status) {
        if (instanceId == null || status == null) {
            return Collections.emptyList();
        }
        InstanceStructureNodePO po = new InstanceStructureNodePO();
        po.setInstanceId(instanceId.getId());
        po.setStatus(status.getCode());
        List<InstanceStructureNodePO> poList = instanceStructureNodeDao.selectByInstanceIdAndStatus(po);
        if (CollectionUtils.isEmpty(poList)) {
            return Collections.emptyList();
        }

        return InstanceStructureNodeConverter.toEntityList(poList);
    }

    @Override
    public List<StructureInstanceNodeEntity> findByInstanceIdAndNameLike(InstanceId instanceId, String nameKeyword) {
        if (instanceId == null || nameKeyword == null) {
            return Collections.emptyList();
        }
        // 转义特殊字符
        nameKeyword = nameKeyword.replace("%", "\\%").replace("_", "\\_");
        // 通配符
        nameKeyword = "%" + nameKeyword + "%";
        InstanceStructureNodePO po = new InstanceStructureNodePO();
        po.setInstanceId(instanceId.getId());
        po.setNodeName(nameKeyword);
        po.setNodeNameEn(nameKeyword);
        List<InstanceStructureNodePO> poList = instanceStructureNodeDao.selectByInstanceIdAndNameLike(po);
        if (CollectionUtils.isEmpty(poList)) {
            return Collections.emptyList();
        }

        return InstanceStructureNodeConverter.toEntityList(poList);
    }

    @Override
    public List<StructureInstanceNodeEntity> findSubTree(InstanceNodeId rootNodeId) {
        if (rootNodeId == null) {
            return Collections.emptyList();
        }
        // 先查询根节点
        Optional<StructureInstanceNodeEntity> rootNodeOpt = findById(rootNodeId);
        if (!rootNodeOpt.isPresent()) {
            return Collections.emptyList();
        }
        StructureInstanceNodeEntity rootNode = rootNodeOpt.get();
        // 如果节点有路径，使用路径查询子树
        if (StringUtils.isNotBlank(rootNode.getNodePath())) {
            return findByPathStartingWith(rootNode.getNodePath());
        }
        // 否则使用递归查询
        List<StructureInstanceNodeEntity> result = new ArrayList<>();
        result.add(rootNode);
        // 查询直接子节点
        List<StructureInstanceNodeEntity> children = findByParentId(rootNodeId);
        // 递归查询每个子节点的子树
        for (StructureInstanceNodeEntity child : children) {
            result.addAll(findSubTree(child.getId()));
        }

        return result;
    }

    @Override
    public List<StructureInstanceNodeEntity> findByPathStartingWith(String nodePath) {
        if (StringUtils.isBlank(nodePath)) {
            return Collections.emptyList();
        }
        // 通配符
        nodePath = nodePath + "%";
        List<InstanceStructureNodePO> poList = instanceStructureNodeDao.selectByNodePathStartWith(nodePath);
        if (CollectionUtils.isEmpty(poList)) {
            return Collections.emptyList();
        }

        return InstanceStructureNodeConverter.toEntityList(poList);
    }

    @Override
    public List<StructureInstanceNodeEntity> findByPathLike(String pathPattern) {
        if (StringUtils.isBlank(pathPattern)) {
            return Collections.emptyList();
        }
        // 通配符
        pathPattern = "%" + pathPattern + "%";
        List<InstanceStructureNodePO> poList = instanceStructureNodeDao.selectByNodePathLike(pathPattern);
        if (CollectionUtils.isEmpty(poList)) {
            return Collections.emptyList();
        }

        return InstanceStructureNodeConverter.toEntityList(poList);
    }

    @Override
    public int deleteById(InstanceNodeId nodeId) {
        // 先查询节点获取实例ID
        Optional<StructureInstanceNodeEntity> nodeOpt = findById(nodeId);
        if (!nodeOpt.isPresent()) {
            return 0;
        }
        StructureInstanceNodeEntity node = nodeOpt.get();
        int result = instanceStructureNodeDao.deleteById(nodeId.getId());
        // 删除缓存
        if (result > 0) {
            clearCache(node);
        }

        return result;
    }

    @Override
    public int deleteByInstanceId(InstanceId instanceId) {
        if (instanceId == null) {
            return 0;
        }
        int result = instanceStructureNodeDao.deleteByInstanceId(instanceId.getId());
        // 清除实例节点列表缓存
        if (result > 0) {
            redisService.remove(getNodesByInstanceIdCacheKey(instanceId.getId()));
        }

        return result;
    }

    @Override
    public int deleteSubTree(InstanceNodeId nodeId) {
        // 先查询节点
        Optional<StructureInstanceNodeEntity> nodeOpt = findById(nodeId);
        if (!nodeOpt.isPresent()) {
            return 0;
        }
        StructureInstanceNodeEntity node = nodeOpt.get();
        int result;
        // 如果节点有路径，使用路径删除子树
        if (StringUtils.isNotBlank(node.getNodePath())) {
            String nodePathStartWith = node.getNodePath() + "%";
            result = instanceStructureNodeDao.deleteByNodePathStartWith(nodePathStartWith);
        } else {
            // 否则先删除所有子节点，再删除自身
            result = deleteChildrenRecursively(nodeId);
            result += instanceStructureNodeDao.deleteById(nodeId.getId());
        }
        // 清除缓存
        if (result > 0) {
            clearCache(node);
        }

        return result;
    }

    /**
     * 递归删除子节点
     */
    private int deleteChildrenRecursively(InstanceNodeId parentId) {
        int count = 0;
        List<StructureInstanceNodeEntity> children = findByParentId(parentId);
        for (StructureInstanceNodeEntity child : children) {
            count += deleteChildrenRecursively(child.getId());
            count += instanceStructureNodeDao.deleteById(child.getId().getId());
            // 删除缓存
            clearCache(child);
        }

        return count;
    }

    @Override
    public int countByParentId(InstanceNodeId parentId) {
        if (parentId == null) {
            return 0;
        }

        return instanceStructureNodeDao.countByParentId(parentId.getId());
    }

    @Override
    public int countByInstanceId(InstanceId instanceId) {
        if (instanceId == null) {
            return 0;
        }

        return instanceStructureNodeDao.countByInstanceId(instanceId.getId());
    }

    /**
     * 清除节点相关的所有缓存
     */
    private void clearCache(StructureInstanceNodeEntity node) {
        if (node == null) {
            return;
        }
        // 清除节点缓存
        if (node.getId() != null) {
            redisService.remove(getNodeByIdCacheKey(node.getId().getId()));
        }
        // 清除实例节点列表缓存
        if (node.getInstanceId() != null) {
            redisService.remove(getNodesByInstanceIdCacheKey(node.getInstanceId().getId()));
        }
        // 清除父节点的子节点列表缓存
        if (node.getParentId() != null) {
            redisService.remove(getNodesByParentIdCacheKey(node.getParentId().getId()));
        }
    }

    /**
     * 清除保存节点时的相关缓存
     */
    private void clearCacheOnSave(StructureInstanceNodeEntity node) {
        if (node == null) {
            return;
        }
        // 清除实例节点列表缓存
        if (node.getInstanceId() != null) {
            redisService.remove(getNodesByInstanceIdCacheKey(node.getInstanceId().getId()));
        }
        // 清除父节点的子节点列表缓存
        if (node.getParentId() != null) {
            redisService.remove(getNodesByParentIdCacheKey(node.getParentId().getId()));
        }
    }

    /**
     * 清除节点移动相关的缓存
     */
    private void clearCacheForNodeMove(InstanceNodeId nodeId, InstanceNodeId oldParentNodeId) {
        if (nodeId == null) {
            return;
        }
        // 查询节点
        Optional<StructureInstanceNodeEntity> nodeOpt = findById(nodeId);
        if (!nodeOpt.isPresent()) {
            return;
        }
        StructureInstanceNodeEntity node = nodeOpt.get();
        // 清除节点缓存
        redisService.remove(getNodeByIdCacheKey(nodeId.getId()));
        // 清除实例节点列表缓存
        if (node.getInstanceId() != null) {
            redisService.remove(getNodesByInstanceIdCacheKey(node.getInstanceId().getId()));
        }
        // 清除原父节点和新父节点的子节点列表缓存
        if (node.getParentId() != null) {
            redisService.remove(getNodesByParentIdCacheKey(node.getParentId().getId()));
        }
        if (oldParentNodeId != null) {
            redisService.remove(getNodesByParentIdCacheKey(oldParentNodeId.getId()));
        }
    }

    private String getNodeByIdCacheKey(Long id) {
        return Constants.RedisKey.INSTANCE_STRUCTURE_NODE_BY_ID_KEY + id;
    }

    private String getNodesByInstanceIdCacheKey(Long instanceId) {
        return Constants.RedisKey.INSTANCE_STRUCTURE_NODES_BY_INSTANCE_ID_KEY + instanceId;
    }

    private String getNodesByParentIdCacheKey(Long parentId) {
        return Constants.RedisKey.INSTANCE_STRUCTURE_NODES_BY_PARENT_ID_KEY + parentId;
    }

}
