package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.structure.adapter.repository.ITemplateNodeRepository;
import cn.cug.sxy.domain.structure.model.entity.StructureInstanceNodeEntity;
import cn.cug.sxy.domain.structure.model.entity.StructureTemplateNodeEntity;
import cn.cug.sxy.domain.structure.model.valobj.*;
import cn.cug.sxy.infrastructure.converter.TemplateStructureNodeConverter;
import cn.cug.sxy.infrastructure.dao.ITemplateStructureNodeDao;
import cn.cug.sxy.infrastructure.dao.po.TemplateStructureNodePO;
import cn.cug.sxy.infrastructure.redis.IRedisService;
import cn.cug.sxy.types.common.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/28 18:38
 * @Description 车型结构树模板节点仓储实现
 * @Author jerryhotton
 */

@Repository
public class TemplateNodeRepository extends AbstractRepository implements ITemplateNodeRepository {

    private final ITemplateStructureNodeDao templateStructureNodeDao;
    private final IRedisService redisService;

    public TemplateNodeRepository(ITemplateStructureNodeDao templateStructureNodeDao, IRedisService redisService) {
        this.templateStructureNodeDao = templateStructureNodeDao;
        this.redisService = redisService;
    }

    @Override
    public void save(StructureTemplateNodeEntity node) {
        if (node == null) {
            return;
        }
        TemplateStructureNodePO po = TemplateStructureNodeConverter.toPO(node);
        templateStructureNodeDao.insert(po);
        // 为插入后的实体设置ID
        if (po.getId() != null && node.getId() == null) {
            node.setId(new TemplateNodeId(po.getId()));
        }
        // 清除相关缓存
        clearCacheOnSave(node);
    }

    @Override
    public int saveBatch(List<StructureTemplateNodeEntity> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return 0;
        }
        List<TemplateStructureNodePO> poList = TemplateStructureNodeConverter.toPOList(nodes);
        int result = templateStructureNodeDao.batchInsert(poList);
        // 更新实体ID
        for (int i = 0; i < poList.size(); i++) {
            if (poList.get(i).getId() != null && nodes.get(i).getId() == null) {
                nodes.get(i).setId(new TemplateNodeId(poList.get(i).getId()));
            }
        }
        // 清除相关缓存
        if (!nodes.isEmpty() && nodes.get(0).getTemplateId() != null) {
            String cacheKey = getNodesByTemplateIdCacheKey(nodes.get(0).getTemplateId().getId());
            redisService.remove(cacheKey);
        }

        return result;
    }

    @Override
    public int update(StructureTemplateNodeEntity node) {
        if (node == null || node.getId() == null) {
            return 0;
        }
        TemplateStructureNodePO po = TemplateStructureNodeConverter.toPO(node);
        int result = templateStructureNodeDao.update(po);
        // 清除相关缓存
        clearCache(node);

        return result;
    }

    @Override
    public int updateStatus(TemplateNodeId nodeId, Status status) {
        if (nodeId == null || status == null) {
            return 0;
        }
        TemplateStructureNodePO templateStructureNodePOReq = new TemplateStructureNodePO();
        templateStructureNodePOReq.setId(nodeId.getId());
        templateStructureNodePOReq.setStatus(status.getCode());
        int result = templateStructureNodeDao.updateStatus(templateStructureNodePOReq);
        // 清除相关缓存
        redisService.remove(getNodeByIdCacheKey(nodeId.getId()));

        return result;
    }

    @Override
    public int updateParentId(TemplateNodeId nodeId, TemplateNodeId parentId) {
        if (nodeId == null) {
            return 0;
        }
        // 清除相关缓存
        clearCacheForNodeMove(nodeId, parentId);
        // 更新父节点ID
        TemplateStructureNodePO templateStructureNodePOReq = new TemplateStructureNodePO();
        Long parentIdValue = parentId != null ? parentId.getId() : null;
        templateStructureNodePOReq.setId(nodeId.getId());
        templateStructureNodePOReq.setParentId(parentIdValue);

        return templateStructureNodeDao.updateParentId(templateStructureNodePOReq);
    }

    @Override
    public int updateSortOrder(TemplateNodeId nodeId, Integer sortOrder) {
        if (nodeId == null || sortOrder == null) {
            return 0;
        }
        TemplateStructureNodePO templateStructureNodePOReq = new TemplateStructureNodePO();
        templateStructureNodePOReq.setId(nodeId.getId());
        templateStructureNodePOReq.setSortOrder(sortOrder);
        int result = templateStructureNodeDao.updateSortOrder(templateStructureNodePOReq);
        // 清除相关缓存
        redisService.remove(getNodeByIdCacheKey(nodeId.getId()));

        return result;
    }

    @Override
    public Optional<StructureTemplateNodeEntity> findById(TemplateNodeId nodeId) {
        if (nodeId == null) {
            return Optional.empty();
        }
        String cacheKey = getNodeByIdCacheKey(nodeId.getId());
        TemplateStructureNodePO templateStructureNodePO = getDataFromCacheOrDB(cacheKey, () -> templateStructureNodeDao.selectById(nodeId.getId()));
        if (templateStructureNodePO == null) {
            return Optional.empty();
        }
        StructureTemplateNodeEntity entity = TemplateStructureNodeConverter.toEntity(templateStructureNodePO);

        return Optional.of(entity);
    }

    @Override
    public List<StructureTemplateNodeEntity> findByTemplateId(TemplateId templateId) {
        if (templateId == null) {
            return Collections.emptyList();
        }
        String cacheKey = getNodesByTemplateIdCacheKey(templateId.getId());
        List<TemplateStructureNodePO> templateStructureNodePOList = getDataFromCacheOrDB(cacheKey,
                () -> templateStructureNodeDao.selectByTemplateId(templateId.getId()));
        if (CollectionUtils.isEmpty(templateStructureNodePOList)) {
            return Collections.emptyList();
        }

        return TemplateStructureNodeConverter.toEntityList(templateStructureNodePOList);
    }

    @Override
    public List<StructureTemplateNodeEntity> findRootNodesByTemplateId(TemplateId templateId) {
        if (templateId == null) {
            return Collections.emptyList();
        }
        List<TemplateStructureNodePO> poList = templateStructureNodeDao.selectRootNodesByTemplateId(templateId.getId());
        if (CollectionUtils.isEmpty(poList)) {
            return Collections.emptyList();
        }

        return TemplateStructureNodeConverter.toEntityList(poList);
    }

    @Override
    public List<StructureTemplateNodeEntity> findByParentId(TemplateNodeId parentId) {
        if (parentId == null) {
            return Collections.emptyList();
        }
        String cacheKey = getNodesByParentIdCacheKey(parentId.getId());
        List<TemplateStructureNodePO> templateStructureNodePOList = getDataFromCacheOrDB(cacheKey, () -> templateStructureNodeDao.selectByParentId(parentId.getId()));
        if (CollectionUtils.isEmpty(templateStructureNodePOList)) {
            return Collections.emptyList();
        }

        return TemplateStructureNodeConverter.toEntityList(templateStructureNodePOList);
    }

    @Override
    public List<StructureTemplateNodeEntity> findByTemplateIdAndNodeType(TemplateId templateId, NodeType nodeType) {
        if (templateId == null || nodeType == null) {
            return Collections.emptyList();
        }
        TemplateStructureNodePO templateStructureNodePOReq = new TemplateStructureNodePO();
        templateStructureNodePOReq.setId(templateId.getId());
        templateStructureNodePOReq.setNodeType(nodeType.name());
        List<TemplateStructureNodePO> poList = templateStructureNodeDao.selectByTemplateIdAndNodeType(templateStructureNodePOReq);
        if (CollectionUtils.isEmpty(poList)) {
            return Collections.emptyList();
        }

        return TemplateStructureNodeConverter.toEntityList(poList);
    }

    @Override
    public List<StructureTemplateNodeEntity> findByTemplateIdAndStatus(TemplateId templateId, Status status) {
        if (templateId == null || status == null) {
            return Collections.emptyList();
        }
        TemplateStructureNodePO templateStructureNodePOReq = new TemplateStructureNodePO();
        templateStructureNodePOReq.setId(templateId.getId());
        templateStructureNodePOReq.setStatus(status.name());
        List<TemplateStructureNodePO> poList = templateStructureNodeDao.selectByTemplateIdAndStatus(templateStructureNodePOReq);
        if (CollectionUtils.isEmpty(poList)) {
            return Collections.emptyList();
        }

        return TemplateStructureNodeConverter.toEntityList(poList);
    }

    @Override
    public List<StructureTemplateNodeEntity> findByTemplateIdAndNameLike(TemplateId templateId, String nameKeyword) {
        if (templateId == null || nameKeyword == null || nameKeyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        // 转义特殊字符
        nameKeyword = nameKeyword.replace("%", "\\%").replace("_", "\\_");
        // 通配符
        nameKeyword = "%" + nameKeyword + "%";
        TemplateStructureNodePO templateStructureNodePOReq = new TemplateStructureNodePO();
        templateStructureNodePOReq.setId(templateId.getId());
        templateStructureNodePOReq.setNodeName(nameKeyword);
        templateStructureNodePOReq.setNodeNameEn(nameKeyword);
        List<TemplateStructureNodePO> poList = templateStructureNodeDao.selectByTemplateIdAndNameLike(templateStructureNodePOReq);
        if (CollectionUtils.isEmpty(poList)) {
            return Collections.emptyList();
        }

        return TemplateStructureNodeConverter.toEntityList(poList);
    }

    @Override
    public List<StructureTemplateNodeEntity> findSubTree(TemplateNodeId rootNodeId) {
        if (rootNodeId == null) {
            return Collections.emptyList();
        }
        // 首先查询根节点，获取其路径
        Optional<StructureTemplateNodeEntity> rootNodeOpt = findById(rootNodeId);
        if (!rootNodeOpt.isPresent()) {
            return Collections.emptyList();
        }
        StructureTemplateNodeEntity rootNode = rootNodeOpt.get();
        // 如果节点有路径，使用路径查询子树
        if (StringUtils.isNotBlank(rootNode.getNodePath())) {
            return findByPathStartingWith(rootNode.getNodePath());
        }
        // 否则使用递归查询
        List<StructureTemplateNodeEntity> result = new ArrayList<>();
        result.add(rootNode);
        // 查询直接子节点
        List<StructureTemplateNodeEntity> children = findByParentId(rootNodeId);
        // 递归查询每个子节点的子树
        for (StructureTemplateNodeEntity child : children) {
            result.addAll(findSubTree(child.getId()));
        }

        return result;
    }


    @Override
    public List<StructureTemplateNodeEntity> findByPathStartingWith(String pathPrefix) {
        if (pathPrefix == null || pathPrefix.trim().isEmpty()) {
            return Collections.emptyList();
        }
        // 转义特殊字符
        pathPrefix = pathPrefix.replace("%", "\\%").replace("_", "\\_");
        pathPrefix = pathPrefix + "%";
        List<TemplateStructureNodePO> poList = templateStructureNodeDao.selectByNodePathStartWith(pathPrefix);
        if (CollectionUtils.isEmpty(poList)) {
            return Collections.emptyList();
        }

        return TemplateStructureNodeConverter.toEntityList(poList);
    }

    @Override
    public List<StructureTemplateNodeEntity> findByPathLike(String pathPattern) {
        if (pathPattern == null || pathPattern.trim().isEmpty()) {
            return Collections.emptyList();
        }
        // 转义特殊字符
        pathPattern = pathPattern.replace("%", "\\%").replace("_", "\\_");
        pathPattern = "%" + pathPattern + "%";
        List<TemplateStructureNodePO> poList = templateStructureNodeDao.selectByNodePathLike(pathPattern);
        if (CollectionUtils.isEmpty(poList)) {
            return Collections.emptyList();
        }

        return TemplateStructureNodeConverter.toEntityList(poList);
    }

    @Override
    public int deleteById(TemplateNodeId nodeId) {
        if (nodeId == null) {
            return 0;
        }
        // 先查询节点
        Optional<StructureTemplateNodeEntity> nodeOpt = findById(nodeId);
        if (!nodeOpt.isPresent()) {
            return 0;
        }
        StructureTemplateNodeEntity node = nodeOpt.get();
        int result = templateStructureNodeDao.deleteById(nodeId.getId());
        // 清除相关缓存
        clearCache(node);

        return result;
    }

    @Override
    public int deleteSubTree(TemplateNodeId rootNodeId) {
        if (rootNodeId == null) {
            return 0;
        }
        // 先查询根节点，获取其路径
        Optional<StructureTemplateNodeEntity> rootNodeOpt = findById(rootNodeId);
        if (!rootNodeOpt.isPresent()) {
            return 0;
        }
        StructureTemplateNodeEntity rootNode = rootNodeOpt.get();
        int result;
        if (StringUtils.isNotBlank(rootNode.getNodePath())) {
            result = templateStructureNodeDao.deleteByNodePathStartWith(rootNode.getNodePath());
        } else {
            // 否则先删除所有子节点，再删除自身
            result = deleteChildrenRecursively(rootNodeId);
            result += templateStructureNodeDao.deleteById(rootNodeId.getId());
        }
        // 清除相关缓存
        if (rootNode.getTemplateId() != null) {
            redisService.remove(getNodesByTemplateIdCacheKey(rootNode.getTemplateId().getId()));
        }

        return result;
    }

    /**
     * 递归删除子节点
     */
    private int deleteChildrenRecursively(TemplateNodeId parentId) {
        int count = 0;
        List<StructureTemplateNodeEntity> children = findByParentId(parentId);
        for (StructureTemplateNodeEntity child : children) {
            count += deleteChildrenRecursively(child.getId());
            count += templateStructureNodeDao.deleteById(child.getId().getId());
            // 删除缓存
            clearCache(child);
        }

        return count;
    }

    @Override
    public int deleteByTemplateId(TemplateId templateId) {
        if (templateId == null) {
            return 0;
        }
        int result = templateStructureNodeDao.deleteByTemplateId(templateId.getId());
        // 清除相关缓存
        redisService.remove(getNodesByTemplateIdCacheKey(templateId.getId()));

        return result;
    }

    @Override
    public boolean hasChildren(TemplateNodeId nodeId) {
        if (nodeId == null) {
            return false;
        }

        return templateStructureNodeDao.countByParentId(nodeId.getId()) > 0;
    }

    @Override
    public int countByTemplateId(TemplateId templateId) {
        if (templateId == null) {
            return 0;
        }

        return templateStructureNodeDao.countByTemplateId(templateId.getId());
    }

    /**
     * 清除节点相关的所有缓存
     */
    private void clearCache(StructureTemplateNodeEntity node) {
        if (node == null) {
            return;
        }
        // 清除节点缓存
        if (node.getId() != null) {
            redisService.remove(getNodeByIdCacheKey(node.getId().getId()));
        }
        // 清除模板节点列表缓存
        if (node.getTemplateId() != null) {
            redisService.remove(getNodesByTemplateIdCacheKey(node.getTemplateId().getId()));
        }
        // 清除父节点的子节点列表缓存
        if (node.getParentId() != null) {
            redisService.remove(getNodesByParentIdCacheKey(node.getParentId().getId()));
        }
    }

    /**
     * 清除节点移动相关的缓存
     */
    private void clearCacheForNodeMove(TemplateNodeId nodeId, TemplateNodeId oldParentNodeId) {
        if (nodeId == null) {
            return;
        }
        // 查询节点
        Optional<StructureTemplateNodeEntity> nodeOpt = findById(nodeId);
        if (!nodeOpt.isPresent()) {
            return;
        }
        StructureTemplateNodeEntity node = nodeOpt.get();
        // 清除节点缓存
        redisService.remove(getNodeByIdCacheKey(nodeId.getId()));
        // 清除模板节点列表缓存
        if (node.getTemplateId() != null) {
            redisService.remove(getNodesByTemplateIdCacheKey(node.getTemplateId().getId()));
        }
        // 清除原父节点和新父节点的子节点列表缓存
        if (node.getParentId() != null) {
            redisService.remove(getNodesByParentIdCacheKey(node.getParentId().getId()));
        }
        if (oldParentNodeId != null) {
            redisService.remove(getNodesByParentIdCacheKey(oldParentNodeId.getId()));
        }
    }

    /**
     * 清除保存节点时的相关缓存
     */
    private void clearCacheOnSave(StructureTemplateNodeEntity node) {
        if (node == null) {
            return;
        }
        // 清除模板节点列表缓存
        if (node.getTemplateId() != null) {
            redisService.remove(getNodesByTemplateIdCacheKey(node.getTemplateId().getId()));
        }
        // 清除父节点的子节点列表缓存
        if (node.getParentId() != null) {
            redisService.remove(getNodesByParentIdCacheKey(node.getParentId().getId()));
        }
    }

    private String getNodeByIdCacheKey(Long id) {
        return Constants.RedisKey.TEMPLATE_STRUCTURE_NODE_BY_ID_KEY + id;
    }

    private String getNodesByTemplateIdCacheKey(Long templateId) {
        return Constants.RedisKey.TEMPLATE_STRUCTURE_NODES_BY_TEMPLATE_ID_KEY + templateId;
    }

    private String getNodesByParentIdCacheKey(Long parentId) {
        return Constants.RedisKey.TEMPLATE_STRUCTURE_NODES_BY_PARENT_ID_KEY + parentId;
    }

}
