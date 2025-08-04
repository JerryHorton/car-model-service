package cn.cug.sxy.domain.structure.service;

import cn.cug.sxy.domain.model.model.valobj.ModelId;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import cn.cug.sxy.domain.structure.adapter.repository.IInstanceNodeRepository;
import cn.cug.sxy.domain.structure.adapter.repository.IInstanceRepository;
import cn.cug.sxy.domain.structure.adapter.repository.ITemplateNodeRepository;
import cn.cug.sxy.domain.structure.adapter.repository.ITemplateRepository;
import cn.cug.sxy.domain.structure.model.entity.StructureInstanceEntity;
import cn.cug.sxy.domain.structure.model.entity.StructureInstanceNodeEntity;
import cn.cug.sxy.domain.structure.model.entity.StructureTemplateEntity;
import cn.cug.sxy.domain.structure.model.entity.StructureTemplateNodeEntity;
import cn.cug.sxy.domain.structure.model.valobj.*;
import cn.cug.sxy.types.exception.AppException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/7/31 14:32
 * @Description 车型结构树实例领域服务实现
 * @Author jerryhotton
 */

@Service
public class InstanceService implements IInstanceService {

    private final IInstanceRepository instanceRepository;
    private final IInstanceNodeRepository instanceNodeRepository;
    private final ITemplateRepository templateRepository;
    private final ITemplateNodeRepository templateNodeRepository;

    public InstanceService(
            IInstanceRepository instanceRepository,
            IInstanceNodeRepository instanceNodeRepository,
            ITemplateRepository templateRepository,
            ITemplateNodeRepository templateNodeRepository) {
        this.instanceRepository = instanceRepository;
        this.instanceNodeRepository = instanceNodeRepository;
        this.templateRepository = templateRepository;
        this.templateNodeRepository = templateNodeRepository;
    }

    @Override
    public StructureInstanceEntity createInstanceFromTemplate(
            InstanceCode instanceCode, String instanceName, String instanceDesc,
            SeriesId seriesId, ModelId modelId, TemplateId templateId, String instanceVersion, String creator) {
        // 参数校验
        if (instanceCode == null) {
            throw new IllegalArgumentException("实例编码不能为空");
        }
        if (templateId == null) {
            throw new IllegalArgumentException("模板ID不能为空");
        }
        if (StringUtils.isBlank(instanceVersion)) {
            throw new IllegalArgumentException("实例版本不能为空");
        }
        if (StringUtils.isBlank(creator)) {
            throw new IllegalArgumentException("创建者不能为空");
        }
        // 检查编码是否已存在
        if (instanceRepository.existsByCode(instanceCode)) {
            // 如果编码存在，检查编码和版本的组合是否存在
            if (instanceRepository.existsByCodeAndVersion(instanceCode, instanceVersion)) {
                throw new AppException(
                        String.format("实例编码[%s]和版本[%s]的组合已存在", instanceCode.getCode(), instanceVersion));
            }
        }
        // 检查模板是否存在
        Optional<StructureTemplateEntity> templateOpt = templateRepository.findById(templateId);
        if (templateOpt.isEmpty()) {
            throw new AppException("模板不存在");
        }
        // 创建实例实体
        StructureInstanceEntity instance = StructureInstanceEntity.create(
                instanceCode, instanceName, instanceDesc, seriesId, modelId, instanceVersion, creator);
        // 保存到仓储
        instanceRepository.save(instance);
        // 复制模板节点到实例
        copyTemplateNodesToInstance(templateId, instance.getId(), creator);

        return instance;
    }

    @Override
    public StructureInstanceEntity createNewVersionWithNodes(
            InstanceId sourceInstanceId, String newVersion, String instanceDesc, String creator) {
        // 参数校验
        if (sourceInstanceId == null) {
            throw new IllegalArgumentException("源实例ID不能为空");
        }
        if (StringUtils.isBlank(newVersion)) {
            throw new IllegalArgumentException("新版本号不能为空");
        }
        if (StringUtils.isBlank(creator)) {
            throw new IllegalArgumentException("创建者不能为空");
        }
        // 查询源实例
        Optional<StructureInstanceEntity> sourceInstanceOpt = instanceRepository.findById(sourceInstanceId);
        if (sourceInstanceOpt.isEmpty()) {
            throw new AppException("源实例不存在");
        }
        StructureInstanceEntity sourceInstance = sourceInstanceOpt.get();
        InstanceCode instanceCode = sourceInstance.getInstanceCode();
        // 检查新版本是否已存在
        if (instanceRepository.existsByCodeAndVersion(instanceCode, newVersion)) {
            throw new AppException(
                    String.format("实例编码[%s]和版本[%s]的组合已存在", instanceCode.getCode(), newVersion));
        }
        // 创建新版本实例
        StructureInstanceEntity newInstance = StructureInstanceEntity.create(
                instanceCode,
                sourceInstance.getInstanceName(),
                instanceDesc,
                sourceInstance.getSeriesId(),
                sourceInstance.getModelId(),
                newVersion,
                creator);
        instanceRepository.save(newInstance);
        // 复制节点结构
        copyNodeStructure(sourceInstanceId, newInstance.getId(), creator);

        return newInstance;
    }

    @Override
    public StructureInstanceNodeEntity addNode(
            InstanceId instanceId, InstanceNodeId parentNodeId,
            String nodeName, String nodeNameEn, String nodeTypeStr,
            Long categoryIdValue, Long groupIdValue, Long usageIdValue,
            Integer sortOrder, String creator) {
        // 参数校验
        if (instanceId == null) {
            throw new IllegalArgumentException("实例ID不能为空");
        }
        if (StringUtils.isBlank(nodeName)) {
            throw new IllegalArgumentException("节点名称不能为空");
        }
        if (StringUtils.isBlank(nodeTypeStr)) {
            throw new IllegalArgumentException("节点类型不能为空");
        }
        if (StringUtils.isBlank(creator)) {
            throw new IllegalArgumentException("创建者不能为空");
        }
        // 转换节点类型
        NodeType nodeType;
        try {
            nodeType = NodeType.fromCode(nodeTypeStr);
        } catch (IllegalArgumentException e) {
            throw new AppException("无效的节点类型: " + nodeTypeStr);
        }
        // 校验节点类型特定参数
        validateNodeTypeSpecificParams(nodeType, categoryIdValue, groupIdValue, usageIdValue);
        // 检查实例是否存在
        Optional<StructureInstanceEntity> instanceOpt = instanceRepository.findById(instanceId);
        if (instanceOpt.isEmpty()) {
            throw new AppException("实例不存在");
        }
        StructureInstanceNodeEntity node;
        if (parentNodeId == null) {
            // 生成节点编码
            String nodeCode = generateNodeCode(instanceId, null, nodeType);
            // 创建根节点
            node = StructureInstanceNodeEntity.createRoot(
                    instanceId, nodeType, nodeCode, nodeName, nodeNameEn, sortOrder, creator);
            // 设置特定类型的ID
            setNodeTypeSpecificId(node, categoryIdValue, groupIdValue, usageIdValue);
            // 保存节点
            instanceNodeRepository.save(node);
            // 设置根节点路径与层级
            if (node.getId() != null) {
                String nodePath = String.valueOf(node.getId().getId());
                node.updatePathAndLevel(nodePath, 0);
                instanceNodeRepository.update(node);
            }
        } else {
            // 查询父节点
            Optional<StructureInstanceNodeEntity> parentNodeOpt = instanceNodeRepository.findById(parentNodeId);
            if (parentNodeOpt.isEmpty()) {
                throw new AppException("父节点不存在");
            }
            StructureInstanceNodeEntity parentNode = parentNodeOpt.get();
            // 生成节点编码
            String nodeCode = generateNodeCode(instanceId, parentNodeId, nodeType);
            // 创建子节点
            node = StructureInstanceNodeEntity.createChild(
                    instanceId, parentNodeId, nodeType, nodeCode, nodeName, nodeNameEn, sortOrder, creator);
            // 设置特定类型的ID
            setNodeTypeSpecificId(node, categoryIdValue, groupIdValue, usageIdValue);
            // 保存节点
            instanceNodeRepository.save(node);
            // 设置子节点路径和层级
            if (node.getId() != null) {
                String parentPath = parentNode.getNodePath();
                String nodePath = parentPath + "-" + node.getId().getId();
                int nodeLevel = parentNode.getNodeLevel() + 1;
                node.updatePathAndLevel(nodePath, nodeLevel);
                instanceNodeRepository.update(node);
            }
        }

        return node;
    }

    @Override
    public boolean updateNodeInfo(InstanceNodeId nodeId, String nodeName, String nodeNameEn, Integer sortOrder) {
        // 参数校验
        if (nodeId == null) {
            throw new IllegalArgumentException("节点ID不能为空");
        }
        if (StringUtils.isBlank(nodeName)) {
            throw new IllegalArgumentException("节点名称不能为空");
        }
        // 查询节点
        Optional<StructureInstanceNodeEntity> nodeOpt = instanceNodeRepository.findById(nodeId);
        if (nodeOpt.isEmpty()) {
            return false;
        }
        StructureInstanceNodeEntity node = nodeOpt.get();
        // 更新节点信息
        node.update(nodeName, nodeNameEn);
        // 保存更新
        int result = instanceNodeRepository.update(node);
        // 调整节点排序
        if (sortOrder != null) {
            adjustNodeOrder(node, sortOrder);
        }

        return result > 0;
    }

    @Override
    public boolean moveNode(InstanceNodeId nodeId, InstanceNodeId newParentId, Integer sortOrder) {
        // 参数校验
        if (nodeId == null) {
            throw new IllegalArgumentException("节点ID不能为空");
        }
        // 查询节点
        Optional<StructureInstanceNodeEntity> nodeOpt = instanceNodeRepository.findById(nodeId);
        if (nodeOpt.isEmpty()) {
            throw new AppException("节点不存在");
        }
        StructureInstanceNodeEntity node = nodeOpt.get();
        String oldPath = node.getNodePath();
        InstanceNodeId oldParentId = node.getParentId();
        // 如果新父节点和旧父节点相同，只需要调整排序
        if ((newParentId == null && oldParentId == null) ||
                (newParentId != null && newParentId.equals(oldParentId))) {
            return adjustNodeOrder(node, sortOrder);
        }
        // 如果指定了新父节点，确保它存在
        if (newParentId != null) {
            Optional<StructureInstanceNodeEntity> parentNodeOpt = instanceNodeRepository.findById(newParentId);
            if (parentNodeOpt.isEmpty()) {
                throw new AppException("新父节点不存在");
            }
            StructureInstanceNodeEntity parentNode = parentNodeOpt.get();
            // 确保不会形成循环引用
            if (parentNode.getNodePath() != null && parentNode.getNodePath().contains("-" + nodeId.getId() + "-")) {
                throw new AppException("不能将节点移动到其子节点下，会形成循环引用");
            }
            // 更新父节点引用
            int result = instanceNodeRepository.updateParentId(nodeId, newParentId);
            if (result <= 0) {
                return false;
            }
            // 更新节点路径和层级
            String newPath = parentNode.getNodePath() + "-" + nodeId.getId();
            int newLevel = parentNode.getNodeLevel() + 1;
            node.updatePathAndLevel(newPath, newLevel);
            instanceNodeRepository.update(node);
            // 更新所有子节点的路径和层级
            updateSubtreePathAndLevel(oldPath, newPath, node.getNodeLevel(), newLevel);
        } else {
            // 移动为根节点
            int result = instanceNodeRepository.updateParentId(nodeId, null);
            if (result <= 0) {
                return false;
            }
            // 更新节点路径和层级
            String newPath = String.valueOf(nodeId.getId());
            node.updatePathAndLevel(newPath, 0);
            instanceNodeRepository.update(node);
            // 更新所有子节点的路径和层级
            updateSubtreePathAndLevel(oldPath, newPath, node.getNodeLevel(), 0);
        }
        // 调整排序
        return adjustNodeOrder(node, sortOrder);
    }

    @Override
    public Map<String, Object> getInstanceWithFullTree(InstanceId instanceId) {
        // 参数校验
        if (instanceId == null) {
            throw new IllegalArgumentException("实例ID不能为空");
        }
        // 查询实例
        Optional<StructureInstanceEntity> instanceOpt = instanceRepository.findById(instanceId);
        if (instanceOpt.isEmpty()) {
            throw new AppException("实例不存在");
        }
        // 查询实例的所有节点
        List<StructureInstanceNodeEntity> nodes = instanceNodeRepository.findByInstanceId(instanceId);
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("instance", instanceOpt.get());
        result.put("nodes", nodes);

        return result;
    }

    @Override
    public Map<String, Object> getInstanceWithFullTreeByCodeAndVersion(InstanceCode instanceCode, String version) {
        // 参数校验
        if (instanceCode == null) {
            throw new IllegalArgumentException("实例编码不能为空");
        }
        if (StringUtils.isBlank(version)) {
            throw new IllegalArgumentException("版本号不能为空");
        }
        // 查询实例
        Optional<StructureInstanceEntity> instanceOpt = instanceRepository.findByInstanceCodeAndVersion(instanceCode, version);
        if (instanceOpt.isEmpty()) {
            throw new AppException("实例不存在");
        }
        StructureInstanceEntity instance = instanceOpt.get();

        // 使用已有方法获取实例和树结构
        return getInstanceWithFullTree(instance.getId());
    }

    @Override
    public boolean deleteInstanceWithAllNodes(InstanceId instanceId) {
        // 参数校验
        if (instanceId == null) {
            throw new IllegalArgumentException("实例ID不能为空");
        }
        // 先删除所有节点
        int count = instanceNodeRepository.deleteByInstanceId(instanceId);
        // 再删除实例（逻辑删除，将状态更新为删除）
        int result = instanceRepository.deleteById(instanceId);

        return count > 0 && result > 0;
    }

    @Override
    public int importNodeTree(InstanceId instanceId, InstanceNodeId parentNodeId,
                              List<StructureInstanceNodeEntity> nodes, String creator) {
        // 参数校验
        if (instanceId == null) {
            throw new IllegalArgumentException("实例ID不能为空");
        }
        if (CollectionUtils.isEmpty(nodes)) {
            return 0;
        }
        if (StringUtils.isBlank(creator)) {
            throw new IllegalArgumentException("创建者不能为空");
        }
        // 检查实例是否存在
        Optional<StructureInstanceEntity> instanceOpt = instanceRepository.findById(instanceId);
        if (instanceOpt.isEmpty()) {
            throw new AppException("实例不存在");
        }
        // 如果指定了父节点，检查父节点是否存在
        if (parentNodeId != null) {
            Optional<StructureInstanceNodeEntity> parentNodeOpt = instanceNodeRepository.findById(parentNodeId);
            if (parentNodeOpt.isEmpty()) {
                throw new AppException("父节点不存在");
            }
        }
        // 为导入的节点设置正确的实例ID和父节点ID
        for (StructureInstanceNodeEntity node : nodes) {
            node.setInstanceId(instanceId);
            if (parentNodeId != null) {
                node.setParentId(parentNodeId);
            }
            node.setCreator(creator);
        }
        // 批量保存节点
        return instanceNodeRepository.saveBatch(nodes);
    }

    @Override
    public Map<String, Object> validateInstanceStructure(InstanceId instanceId) {
        // 参数校验
        if (instanceId == null) {
            throw new IllegalArgumentException("实例ID不能为空");
        }
        // 查询实例
        Optional<StructureInstanceEntity> instanceOpt = instanceRepository.findById(instanceId);
        if (instanceOpt.isEmpty()) {
            throw new AppException("实例不存在");
        }
        // 查询实例的所有节点
        List<StructureInstanceNodeEntity> nodes = instanceNodeRepository.findByInstanceId(instanceId);
        // 校验结果
        Map<String, Object> result = new HashMap<>();
        List<String> issues = new ArrayList<>();
        // 校验逻辑
        if (nodes.isEmpty()) {
            issues.add("实例没有任何节点");
            result.put("valid", false);
        } else {
            // 检查是否有根节点
            long rootNodeCount = nodes.stream()
                    .filter(node -> node.getParentId() == null)
                    .count();
            if (rootNodeCount == 0) {
                issues.add("实例没有根节点");
                result.put("valid", false);
            } else {
                // 检查节点引用的完整性
                Set<InstanceNodeId> allNodeIds = nodes.stream()
                        .map(StructureInstanceNodeEntity::getId)
                        .collect(Collectors.toSet());
                Set<InstanceNodeId> allParentIds = nodes.stream()
                        .map(StructureInstanceNodeEntity::getParentId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                // 检查所有父节点ID是否都存在于节点ID集合中
                boolean allParentsExist = allNodeIds.containsAll(allParentIds);
                if (!allParentsExist) {
                    issues.add("存在引用了不存在节点的父节点ID");
                    result.put("valid", false);
                } else {
                    // 检查是否有循环引用
                    boolean hasCycle = checkCyclicReference(nodes);
                    if (hasCycle) {
                        issues.add("节点之间存在循环引用");
                        result.put("valid", false);
                    } else {
                        result.put("valid", true);
                    }
                }
            }
        }
        result.put("issues", issues);

        return result;
    }

    @Override
    public StructureInstancePageVO findInstances(InstanceCode instanceCode, String nameKeyword, Status status, SeriesId seriesId, ModelId modelId, int pageNo, int pageSize) {
        return instanceRepository.findInstances(instanceCode, nameKeyword, status, seriesId, modelId, pageNo, pageSize);
    }

    @Override
    public int deleteNodeAndChildren(InstanceNodeId nodeId) {
        // 参数校验
        if (nodeId == null) {
            throw new IllegalArgumentException("节点ID不能为空");
        }
        // 删除节点及其子节点
        return instanceNodeRepository.deleteSubTree(nodeId);
    }

    @Override
    public boolean enableInstance(InstanceId instanceId) {
        // 参数校验
        if (instanceId == null) {
            throw new IllegalArgumentException("实例ID不能为空");
        }
        // 更新状态
        int result = instanceRepository.updateStatus(instanceId, Status.ENABLED);

        return result > 0;
    }

    @Override
    public boolean disableInstance(InstanceId instanceId) {
        // 参数校验
        if (instanceId == null) {
            throw new IllegalArgumentException("实例ID不能为空");
        }
        // 更新状态
        int result = instanceRepository.updateStatus(instanceId, Status.DISABLED);

        return result > 0;
    }

    @Override
    public boolean publishInstance(InstanceId instanceId, LocalDateTime effectiveTime) {
        // 参数校验
        if (instanceId == null) {
            throw new IllegalArgumentException("实例ID不能为空");
        }
        if (effectiveTime == null) {
            throw new IllegalArgumentException("生效时间不能为空");
        }
        // 查询实例
        Optional<StructureInstanceEntity> instanceOpt = instanceRepository.findById(instanceId);
        if (instanceOpt.isEmpty()) {
            return false;
        }
        StructureInstanceEntity instance = instanceOpt.get();
        // 更新发布状态和生效时间
        instance.publish(effectiveTime);
        // 保存更新
        int result = instanceRepository.update(instance);

        return result > 0;
    }

    @Override
    public boolean unpublishInstance(InstanceId instanceId) {
        // 参数校验
        if (instanceId == null) {
            throw new IllegalArgumentException("实例ID不能为空");
        }
        // 查询实例
        Optional<StructureInstanceEntity> instanceOpt = instanceRepository.findById(instanceId);
        if (instanceOpt.isEmpty()) {
            return false;
        }
        StructureInstanceEntity instance = instanceOpt.get();
        // 更新发布状态
        instance.unpublish();
        // 保存更新
        int result = instanceRepository.update(instance);

        return result > 0;
    }

    @Override
    public StructureInstanceEntity findInstanceById(InstanceId instanceId) {
        if (instanceId == null) {
            return null;
        }
        Optional<StructureInstanceEntity> structureInstanceEntityOpt = instanceRepository.findById(instanceId);
        if (!structureInstanceEntityOpt.isPresent()) {
            return null;
        }

        return structureInstanceEntityOpt.get();
    }

    @Override
    public StructureInstanceNodeEntity findNodeById(InstanceNodeId nodeId) {
        if (nodeId == null) {
            return null;
        }
        Optional<StructureInstanceNodeEntity> structureInstanceNodeEntityOpt = instanceNodeRepository.findById(nodeId);
        if (!structureInstanceNodeEntityOpt.isPresent()) {
            return null;
        }

        return structureInstanceNodeEntityOpt.get();
    }

    @Override
    public List<StructureInstanceNodeEntity> findChildNodes(InstanceNodeId parentNodeId) {
        if (parentNodeId == null) {
            return Collections.emptyList();
        }

        return instanceNodeRepository.findByParentId(parentNodeId);
    }

    @Override
    public List<StructureInstanceNodeEntity> findSubTree(InstanceNodeId rootNodeId) {
        if (rootNodeId == null) {
            return Collections.emptyList();
        }

        return instanceNodeRepository.findSubTree(rootNodeId);
    }

    @Override
    public Map<String, Object> compareInstances(InstanceId instanceId1, InstanceId instanceId2) {
        // 参数校验
        if (instanceId1 == null || instanceId2 == null) {
            throw new IllegalArgumentException("实例ID不能为空");
        }
        // 查询两个实例的所有节点
        List<StructureInstanceNodeEntity> nodes1 = instanceNodeRepository.findByInstanceId(instanceId1);
        List<StructureInstanceNodeEntity> nodes2 = instanceNodeRepository.findByInstanceId(instanceId2);
        // 构建节点映射，用于比较
        Map<String, StructureInstanceNodeEntity> nodeMap1 = buildNodeMap(nodes1);
        Map<String, StructureInstanceNodeEntity> nodeMap2 = buildNodeMap(nodes2);
        // 比较结果
        List<Map<String, Object>> added = new ArrayList<>();
        List<Map<String, Object>> removed = new ArrayList<>();
        List<Map<String, Object>> modified = new ArrayList<>();
        // 找出新增和修改的节点
        for (String nodeCode : nodeMap2.keySet()) {
            StructureInstanceNodeEntity node2 = nodeMap2.get(nodeCode);
            if (!nodeMap1.containsKey(nodeCode)) {
                // 新增的节点
                Map<String, Object> addedNode = new HashMap<>();
                addedNode.put("nodeCode", nodeCode);
                addedNode.put("node", node2);
                added.add(addedNode);
            } else {
                // 比较节点是否有修改
                StructureInstanceNodeEntity node1 = nodeMap1.get(nodeCode);
                if (!compareNodes(node1, node2)) {
                    Map<String, Object> modifiedNode = new HashMap<>();
                    modifiedNode.put("nodeCode", nodeCode);
                    modifiedNode.put("oldNode", node1);
                    modifiedNode.put("newNode", node2);
                    modified.add(modifiedNode);
                }
            }
        }
        // 找出删除的节点
        for (String nodeCode : nodeMap1.keySet()) {
            if (!nodeMap2.containsKey(nodeCode)) {
                // 删除的节点
                StructureInstanceNodeEntity node1 = nodeMap1.get(nodeCode);
                Map<String, Object> removedNode = new HashMap<>();
                removedNode.put("nodeCode", nodeCode);
                removedNode.put("node", node1);
                removed.add(removedNode);
            }
        }
        // 构建比较结果
        Map<String, Object> result = new HashMap<>();
        result.put("added", added);
        result.put("removed", removed);
        result.put("modified", modified);
        result.put("totalAdded", added.size());
        result.put("totalRemoved", removed.size());
        result.put("totalModified", modified.size());

        return result;
    }

    /**
     * 复制模板节点到实例
     */
    private void copyTemplateNodesToInstance(TemplateId templateId, InstanceId instanceId, String creator) {
        // 查询模板的所有节点
        List<StructureTemplateNodeEntity> templateNodes = templateNodeRepository.findByTemplateId(templateId);
        if (CollectionUtils.isEmpty(templateNodes)) {
            return;
        }
        // 构建节点ID映射关系（模板节点ID -> 实例节点）
        Map<TemplateNodeId, StructureInstanceNodeEntity> templateToInstanceNodeMap = new HashMap<>();
        // 先复制所有根节点
        List<StructureTemplateNodeEntity> rootNodes = templateNodes.stream()
                .filter(node -> node.getParentId() == null)
                .collect(Collectors.toList());
        for (StructureTemplateNodeEntity rootNode : rootNodes) {
            StructureInstanceNodeEntity newRootNode = StructureInstanceNodeEntity.createRoot(
                    instanceId,
                    rootNode.getNodeType(),
                    rootNode.getNodeCode(),
                    rootNode.getNodeName(),
                    rootNode.getNodeNameEn(),
                    rootNode.getSortOrder(),
                    creator
            );
            // 复制特定类型的ID
            copyNodeTypeSpecificId(rootNode, newRootNode);
            instanceNodeRepository.save(newRootNode);
            // 设置根节点路径与层级
            if (newRootNode.getId() != null) {
                String nodePath = String.valueOf(newRootNode.getId().getId());
                newRootNode.updatePathAndLevel(nodePath, 0);
                instanceNodeRepository.update(newRootNode);
            }
            templateToInstanceNodeMap.put(rootNode.getId(), newRootNode);
        }
        // 按层次复制子节点
        boolean hasNewNodes = true;
        Set<TemplateNodeId> processedNodeIds = new HashSet<>(rootNodes.stream()
                .map(StructureTemplateNodeEntity::getId)
                .collect(Collectors.toSet()));
        while (hasNewNodes) {
            hasNewNodes = false;
            for (StructureTemplateNodeEntity templateNode : templateNodes) {
                // 跳过已处理的节点
                if (processedNodeIds.contains(templateNode.getId())) {
                    continue;
                }
                // 如果父节点已处理，则可以处理当前节点
                TemplateNodeId parentId = templateNode.getParentId();
                if (parentId != null && processedNodeIds.contains(parentId)) {
                    // 获取新的父节点
                    StructureInstanceNodeEntity newParentNode = templateToInstanceNodeMap.get(parentId);
                    // 创建新节点
                    StructureInstanceNodeEntity newNode = StructureInstanceNodeEntity.createChild(
                            instanceId,
                            newParentNode.getId(),
                            templateNode.getNodeType(),
                            templateNode.getNodeCode(),
                            templateNode.getNodeName(),
                            templateNode.getNodeNameEn(),
                            templateNode.getSortOrder(),
                            creator
                    );
                    // 复制特定类型的ID
                    copyNodeTypeSpecificId(templateNode, newNode);
                    instanceNodeRepository.save(newNode);
                    // 设置子节点路径和层级
                    if (newNode.getId() != null) {
                        String parentPath = newParentNode.getNodePath();
                        String nodePath = parentPath + "-" + newNode.getId().getId();
                        int nodeLevel = newParentNode.getNodeLevel() + 1;
                        newNode.updatePathAndLevel(nodePath, nodeLevel);
                        instanceNodeRepository.update(newNode);
                    }
                    templateToInstanceNodeMap.put(templateNode.getId(), newNode);
                    processedNodeIds.add(templateNode.getId());
                    hasNewNodes = true;
                }
            }
        }
    }

    /**
     * 复制节点结构
     */
    private void copyNodeStructure(InstanceId sourceInstanceId, InstanceId targetInstanceId, String creator) {
        // 查询源实例的所有节点
        List<StructureInstanceNodeEntity> sourceNodes = instanceNodeRepository.findByInstanceId(sourceInstanceId);
        if (CollectionUtils.isEmpty(sourceNodes)) {
            return;
        }
        // 构建节点ID映射关系（旧ID -> 新节点）
        Map<InstanceNodeId, StructureInstanceNodeEntity> oldToNewNodeMap = new HashMap<>();

        // 先复制所有根节点
        List<StructureInstanceNodeEntity> rootNodes = sourceNodes.stream()
                .filter(node -> node.getParentId() == null)
                .collect(Collectors.toList());

        for (StructureInstanceNodeEntity rootNode : rootNodes) {
            StructureInstanceNodeEntity newRootNode = StructureInstanceNodeEntity.createRoot(
                    targetInstanceId,
                    rootNode.getNodeType(),
                    rootNode.getNodeCode(),
                    rootNode.getNodeName(),
                    rootNode.getNodeNameEn(),
                    rootNode.getSortOrder(),
                    creator
            );

            // 复制特定类型的ID
            newRootNode.setCategoryId(rootNode.getCategoryId());
            newRootNode.setGroupId(rootNode.getGroupId());
            newRootNode.setUsageId(rootNode.getUsageId());

            instanceNodeRepository.save(newRootNode);

            // 设置根节点路径
            if (newRootNode.getId() != null) {
                String nodePath = String.valueOf(newRootNode.getId().getId());
                newRootNode.updatePathAndLevel(nodePath, 0);
                instanceNodeRepository.update(newRootNode);
            }

            oldToNewNodeMap.put(rootNode.getId(), newRootNode);
        }

        // 按层次复制子节点
        boolean hasNewNodes = true;
        Set<InstanceNodeId> processedNodeIds = new HashSet<>(rootNodes.stream()
                .map(StructureInstanceNodeEntity::getId)
                .collect(Collectors.toSet()));

        while (hasNewNodes) {
            hasNewNodes = false;

            for (StructureInstanceNodeEntity sourceNode : sourceNodes) {
                // 跳过已处理的节点
                if (processedNodeIds.contains(sourceNode.getId())) {
                    continue;
                }

                // 如果父节点已处理，则可以处理当前节点
                InstanceNodeId parentId = sourceNode.getParentId();
                if (parentId != null && processedNodeIds.contains(parentId)) {
                    // 获取新的父节点
                    StructureInstanceNodeEntity newParentNode = oldToNewNodeMap.get(parentId);

                    // 创建新节点
                    StructureInstanceNodeEntity newNode = StructureInstanceNodeEntity.createChild(
                            targetInstanceId,
                            newParentNode.getId(),
                            sourceNode.getNodeType(),
                            sourceNode.getNodeCode(),
                            sourceNode.getNodeName(),
                            sourceNode.getNodeNameEn(),
                            sourceNode.getSortOrder(),
                            creator
                    );

                    // 复制特定类型的ID
                    newNode.setCategoryId(sourceNode.getCategoryId());
                    newNode.setGroupId(sourceNode.getGroupId());
                    newNode.setUsageId(sourceNode.getUsageId());

                    instanceNodeRepository.save(newNode);

                    // 设置子节点路径和层级
                    if (newNode.getId() != null) {
                        String parentPath = newParentNode.getNodePath();
                        String nodePath = parentPath + "-" + newNode.getId().getId();
                        int nodeLevel = newParentNode.getNodeLevel() + 1;
                        newNode.updatePathAndLevel(nodePath, nodeLevel);
                        instanceNodeRepository.update(newNode);
                    }

                    oldToNewNodeMap.put(sourceNode.getId(), newNode);
                    processedNodeIds.add(sourceNode.getId());
                    hasNewNodes = true;
                }
            }
        }
    }

    /**
     * 校验节点类型特定参数
     */
    private void validateNodeTypeSpecificParams(NodeType nodeType, Long categoryId, Long groupId, Long usageId) {
        if (nodeType == NodeType.CATEGORY && categoryId == null) {
            throw new AppException("CATEGORY类型节点必须指定系统大类ID");
        }
        if (nodeType == NodeType.GROUP && groupId == null) {
            throw new AppException("GROUP类型节点必须指定系统组ID");
        }
        if (nodeType == NodeType.USAGE && usageId == null) {
            throw new AppException("USAGE类型节点必须指定用法ID");
        }
    }

    /**
     * 设置节点类型特定ID
     */
    private void setNodeTypeSpecificId(StructureInstanceNodeEntity node, Long categoryId, Long groupId, Long usageId) {
        switch (node.getNodeType()) {
            case CATEGORY:
                node.setCategoryId(categoryId);
                break;
            case GROUP:
                node.setGroupId(groupId);
                break;
            case USAGE:
                node.setUsageId(usageId);
                break;
        }
    }

    /**
     * 复制节点类型特定ID
     */
    private void copyNodeTypeSpecificId(StructureTemplateNodeEntity source, StructureInstanceNodeEntity target) {
        switch (source.getNodeType()) {
            case CATEGORY:
                target.setCategoryId(source.getCategoryId());
                break;
            case GROUP:
                target.setGroupId(source.getGroupId());
                break;
            case USAGE:
                // 模板节点没有usageId，这里不复制
                break;
        }
    }

    /**
     * 生成节点编码
     */
    private String generateNodeCode(InstanceId instanceId, InstanceNodeId parentNodeId, NodeType nodeType) {
        // 实际项目中可能需要根据业务规则生成唯一编码
        // 这里简单实现，实际应用中可能需要更复杂的逻辑
        String prefix = nodeType.getCode().substring(0, 3);
        String timestamp = String.valueOf(System.currentTimeMillis());
        return prefix + "_" + timestamp;
    }

    /**
     * 检查节点之间是否存在循环引用
     */
    private boolean checkCyclicReference(List<StructureInstanceNodeEntity> nodes) {
        // 构建节点ID到节点的映射
        Map<InstanceNodeId, StructureInstanceNodeEntity> nodeMap = nodes.stream()
                .collect(Collectors.toMap(StructureInstanceNodeEntity::getId, node -> node));
        // 对每个节点进行循环检测
        for (StructureInstanceNodeEntity node : nodes) {
            Set<InstanceNodeId> visited = new HashSet<>();
            if (hasCycle(node.getId(), nodeMap, visited)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 深度优先搜索检测循环引用
     */
    private boolean hasCycle(InstanceNodeId nodeId, Map<InstanceNodeId, StructureInstanceNodeEntity> nodeMap, Set<InstanceNodeId> visited) {
        if (visited.contains(nodeId)) {
            return true;
        }
        visited.add(nodeId);
        StructureInstanceNodeEntity node = nodeMap.get(nodeId);
        if (node != null && node.getParentId() != null) {
            return hasCycle(node.getParentId(), nodeMap, visited);
        }
        visited.remove(nodeId);

        return false;
    }

    /**
     * 更新子树的路径和层级
     */
    private void updateSubtreePathAndLevel(String oldPath, String newPath, int oldLevel, int newLevel) {
        // 查询所有以oldPath开头的节点
        List<StructureInstanceNodeEntity> subtreeNodes = instanceNodeRepository.findByPathStartingWith(oldPath + "-");
        int levelDiff = newLevel - oldLevel;
        for (StructureInstanceNodeEntity node : subtreeNodes) {
            // 替换路径前缀
            String currentPath = node.getNodePath();
            String updatedPath = newPath + currentPath.substring(oldPath.length());
            // 更新层级
            int updatedLevel = node.getNodeLevel() + levelDiff;
            // 更新节点
            node.updatePathAndLevel(updatedPath, updatedLevel);
            instanceNodeRepository.update(node);
        }
    }

    /**
     * 调整节点排序
     */
    private boolean adjustNodeOrder(StructureInstanceNodeEntity node, Integer targetSortOrder) {
        // 获取同级节点列表（不包括当前节点）
        List<StructureInstanceNodeEntity> siblingNodes;
        if (node.getParentId() == null) {
            // 根节点
            siblingNodes = instanceNodeRepository.findRootNodesByInstanceId(node.getInstanceId())
                    .stream()
                    .filter(n -> !n.getId().equals(node.getId()))
                    .collect(Collectors.toList());
        } else {
            // 子节点
            siblingNodes = instanceNodeRepository.findByParentId(node.getParentId())
                    .stream()
                    .filter(n -> !n.getId().equals(node.getId()))
                    .collect(Collectors.toList());
        }
        // 如果没有指定排序号，放到最后
        if (targetSortOrder == null) {
            int maxSortOrder = siblingNodes.isEmpty() ? 0 :
                    siblingNodes.stream().mapToInt(StructureInstanceNodeEntity::getSortOrder).max().orElse(0);
            targetSortOrder = maxSortOrder + 1;
        }
        // 更新当前节点的排序号
        node.setSortOrder(targetSortOrder);
        instanceNodeRepository.update(node);
        // 调整其他节点的排序号
        // 1. 先按排序号升序排列
        siblingNodes.sort(Comparator.comparing(StructureInstanceNodeEntity::getSortOrder));
        // 2. 重新分配排序号
        int currentOrder = 1;
        for (StructureInstanceNodeEntity sibling : siblingNodes) {
            // 如果当前分配的序号等于目标序号，跳过这个序号
            if (currentOrder == targetSortOrder) {
                currentOrder++;
            }
            // 只有当排序号需要变更时才更新
            if (sibling.getSortOrder() != currentOrder) {
                sibling.setSortOrder(currentOrder);
                instanceNodeRepository.update(sibling);
            }
            currentOrder++;
        }

        return true;
    }

    /**
     * 构建节点映射，用于比较
     */
    private Map<String, StructureInstanceNodeEntity> buildNodeMap(List<StructureInstanceNodeEntity> nodes) {
        Map<String, StructureInstanceNodeEntity> nodeMap = new HashMap<>();
        for (StructureInstanceNodeEntity node : nodes) {
            nodeMap.put(node.getNodeCode(), node);
        }
        return nodeMap;
    }

    /**
     * 比较两个节点是否相同
     */
    private boolean compareNodes(StructureInstanceNodeEntity node1, StructureInstanceNodeEntity node2) {
        if (!Objects.equals(node1.getNodeName(), node2.getNodeName())) {
            return false;
        }
        if (!Objects.equals(node1.getNodeNameEn(), node2.getNodeNameEn())) {
            return false;
        }
        if (!Objects.equals(node1.getNodeType(), node2.getNodeType())) {
            return false;
        }
        // 根据节点类型比较特定字段
        switch (node1.getNodeType()) {
            case CATEGORY:
                if (!Objects.equals(node1.getCategoryId(), node2.getCategoryId())) {
                    return false;
                }
                break;
            case GROUP:
                if (!Objects.equals(node1.getGroupId(), node2.getGroupId())) {
                    return false;
                }
                break;
            case USAGE:
                if (!Objects.equals(node1.getUsageId(), node2.getUsageId())) {
                    return false;
                }
                break;
        }

        return true;
    }
}
