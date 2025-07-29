package cn.cug.sxy.domain.structure.service;

import cn.cug.sxy.domain.structure.adapter.repository.ITemplateNodeRepository;
import cn.cug.sxy.domain.structure.adapter.repository.ITemplateRepository;
import cn.cug.sxy.domain.structure.model.entity.StructureTemplateEntity;
import cn.cug.sxy.domain.structure.model.entity.StructureTemplateNodeEntity;
import cn.cug.sxy.domain.structure.model.valobj.*;
import cn.cug.sxy.domain.system.model.valobj.CategoryId;
import cn.cug.sxy.domain.system.model.valobj.GroupId;
import cn.cug.sxy.types.exception.AppException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/7/28 18:33
 * @Description 车型结构树模板服务实现
 * @Author jerryhotton
 */

@Service
public class TemplateService implements ITemplateService {

    private final ITemplateRepository templateRepository;
    private final ITemplateNodeRepository templateNodeRepository;

    public TemplateService(ITemplateRepository templateRepository, ITemplateNodeRepository templateNodeRepository) {
        this.templateRepository = templateRepository;
        this.templateNodeRepository = templateNodeRepository;
    }

    @Override
    public StructureTemplateEntity createTemplate(TemplateCode templateCode, String templateName,
                                                  String templateDesc, String version, String creator) {
        // 参数校验
        if (templateCode == null) {
            throw new IllegalArgumentException("模板编码不能为空");
        }
        if (StringUtils.isBlank(templateName)) {
            throw new IllegalArgumentException("模板名称不能为空");
        }
        if (StringUtils.isBlank(version)) {
            throw new IllegalArgumentException("模板版本不能为空");
        }
        if (StringUtils.isBlank(creator)) {
            throw new IllegalArgumentException("创建者不能为空");
        }
        // 检查编码是否已存在
        if (templateRepository.existsByCode(templateCode)) {
            // 如果编码存在，检查编码和版本的组合是否存在
            if (templateRepository.existsByCodeAndVersion(templateCode, version)) {
                throw new AppException(
                        String.format("模板编码[%s]和版本[%s]的组合已存在", templateCode.getCode(), version));
            }
        }
        // 创建模板实体
        StructureTemplateEntity template = StructureTemplateEntity.create(
                templateCode, templateName, templateDesc, version, creator);
        // 保存到仓储
        templateRepository.save(template);

        return template;
    }

    @Override
    public boolean updateTemplateInfo(TemplateId templateId, String templateName, String templateDesc) {
        // 参数校验
        if (templateId == null) {
            throw new IllegalArgumentException("模板ID不能为空");
        }
        // 查询模板是否存在
        Optional<StructureTemplateEntity> optionalTemplate = templateRepository.findById(templateId);
        if (optionalTemplate.isEmpty()) {
            return false;
        }
        StructureTemplateEntity template = optionalTemplate.get();
        // 更新模板信息
        template.update(templateName, template.getVersion());
        // 保存更新
        int result = templateRepository.update(template);

        return result > 0;
    }

    @Override
    public StructureTemplateEntity createNewVersionWithNodes(TemplateId sourceTemplateId, String newVersion, String creator) {
        // 参数校验
        if (sourceTemplateId == null) {
            throw new IllegalArgumentException("源模板ID不能为空");
        }
        if (StringUtils.isBlank(newVersion)) {
            throw new IllegalArgumentException("新版本号不能为空");
        }
        if (StringUtils.isBlank(creator)) {
            throw new IllegalArgumentException("创建者不能为空");
        }
        // 查询源模板
        Optional<StructureTemplateEntity> sourceTemplateOpt = templateRepository.findById(sourceTemplateId);
        if (sourceTemplateOpt.isEmpty()) {
            throw new AppException("源模板不存在");
        }
        StructureTemplateEntity sourceTemplate = sourceTemplateOpt.get();
        TemplateCode templateCode = sourceTemplate.getTemplateCode();
        // 检查新版本是否已存在
        if (templateRepository.existsByCodeAndVersion(templateCode, newVersion)) {
            throw new AppException(
                    String.format("模板编码[%s]和版本[%s]的组合已存在", templateCode.getCode(), newVersion));
        }
        // 创建新版本模板
        StructureTemplateEntity newTemplate = StructureTemplateEntity.create(
                templateCode, sourceTemplate.getTemplateName(), sourceTemplate.getTemplateDesc(), newVersion, creator);

        templateRepository.save(newTemplate);
        // 复制节点结构
        copyNodeStructure(sourceTemplateId, newTemplate.getId(), creator);

        return newTemplate;
    }

    @Override
    public StructureTemplateNodeEntity addNode(TemplateId templateId, TemplateNodeId parentNodeId,
                                               String nodeName, String nodeNameEn, String nodeTypeStr,
                                               Long categoryIdValue, Long groupIdValue, Integer sortOrder, String creator) {
        // 参数校验
        if (templateId == null) {
            throw new IllegalArgumentException("模板ID不能为空");
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
        // 转换系统大类ID和系统组ID
        CategoryId categoryId = categoryIdValue != null ? new CategoryId(categoryIdValue) : null;
        GroupId groupId = groupIdValue != null ? new GroupId(groupIdValue) : null;
        // 校验节点类型特定参数
        validateNodeTypeSpecificParams(nodeType, categoryId, groupId);
        // 检查模板是否存在
        Optional<StructureTemplateEntity> templateOpt = templateRepository.findById(templateId);
        if (templateOpt.isEmpty()) {
            throw new AppException("模板不存在");
        }
        StructureTemplateNodeEntity node;
        // 处理根节点
        if (parentNodeId == null) {
            // 生成节点编码
            String nodeCode = generateNodeCode(templateId, null, nodeType);
            // 创建根节点
            node = StructureTemplateNodeEntity.createRoot(
                    templateId, nodeType, nodeCode, nodeName, nodeNameEn, sortOrder, creator);
            node.setCategoryId(categoryIdValue);
            // 保存节点
            templateNodeRepository.save(node);
            // 设置根节点路径
            if (node.getId() != null) {
                String nodePath = String.valueOf(node.getId().getId());
                node.updatePathAndLevel(nodePath, 0);
                templateNodeRepository.update(node);
            }
        } else {
            // 查询父节点
            Optional<StructureTemplateNodeEntity> parentNodeOpt = templateNodeRepository.findById(parentNodeId);
            if (parentNodeOpt.isEmpty()) {
                throw new AppException("父节点不存在");
            }
            StructureTemplateNodeEntity parentNode = parentNodeOpt.get();
            // 生成节点编码
            String nodeCode = generateNodeCode(templateId, parentNodeId, nodeType);
            // 创建子节点
            node = StructureTemplateNodeEntity.createChild(
                    templateId, parentNodeId, nodeType, nodeCode, nodeName, nodeNameEn, sortOrder, creator);
            node.setGroupId(groupIdValue);
            // 保存节点
            templateNodeRepository.save(node);
            // 设置子节点路径和层级
            if (node.getId() != null) {
                String parentPath = parentNode.getNodePath();
                String nodePath = parentPath + "-" + node.getId().getId();
                int nodeLevel = parentNode.getNodeLevel() + 1;
                node.updatePathAndLevel(nodePath, nodeLevel);
                templateNodeRepository.update(node);
            }
        }

        return node;
    }

    @Override
    public boolean updateNodeInfo(TemplateNodeId nodeId, String nodeName, String nodeNameEn, Integer sortOrder) {
        if (nodeId == null) {
            throw new IllegalArgumentException("节点ID不能为空");
        }
        if (StringUtils.isBlank(nodeName)) {
            throw new IllegalArgumentException("节点名称不能为空");
        }
        // 查询节点
        Optional<StructureTemplateNodeEntity> nodeOpt = templateNodeRepository.findById(nodeId);
        if (nodeOpt.isEmpty()) {
            return false;
        }
        StructureTemplateNodeEntity node = nodeOpt.get();
        // 更新节点信息
        node.update(nodeName, nodeNameEn, sortOrder);
        // 保存更新
        int result = templateNodeRepository.update(node);

        return result > 0;
    }

    @Override
    public boolean moveNode(TemplateNodeId nodeId, TemplateNodeId newParentId, Integer sortOrder) {
        if (nodeId == null) {
            throw new IllegalArgumentException("节点ID不能为空");
        }
        // 查询节点
        Optional<StructureTemplateNodeEntity> nodeOpt = templateNodeRepository.findById(nodeId);
        if (nodeOpt.isEmpty()) {
            throw new AppException("节点不存在");
        }
        StructureTemplateNodeEntity node = nodeOpt.get();
        String oldPath = node.getNodePath();
        // 如果指定了新父节点，确保它存在
        if (newParentId != null) {
            Optional<StructureTemplateNodeEntity> parentNodeOpt = templateNodeRepository.findById(newParentId);
            if (parentNodeOpt.isEmpty()) {
                throw new AppException("新父节点不存在");
            }
            StructureTemplateNodeEntity parentNode = parentNodeOpt.get();
            // 确保不会形成循环引用
            if (parentNode.getNodePath() != null && parentNode.getNodePath().contains("-" + nodeId.getId() + "-")) {
                throw new AppException("不能将节点移动到其子节点下，会形成循环引用");
            }
            // 更新父节点引用
            int result = templateNodeRepository.updateParentId(nodeId, newParentId);
            if (result <= 0) {
                return false;
            }
            // 更新节点路径和层级
            String newPath = parentNode.getNodePath() + "-" + nodeId.getId();
            int newLevel = parentNode.getNodeLevel() + 1;
            node.updatePathAndLevel(newPath, newLevel);
            templateNodeRepository.update(node);
            // 更新所有子节点的路径和层级
            updateSubtreePathAndLevel(oldPath, newPath, node.getNodeLevel(), newLevel);
        } else {
            // 移动为根节点
            int result = templateNodeRepository.updateParentId(nodeId, null);
            if (result <= 0) {
                return false;
            }
            // 更新节点路径和层级
            String newPath = String.valueOf(nodeId.getId());
            node.updatePathAndLevel(newPath, 0);
            templateNodeRepository.update(node);
            // 更新所有子节点的路径和层级
            updateSubtreePathAndLevel(oldPath, newPath, node.getNodeLevel(), 0);
        }
        // 如果指定了排序序号，更新排序
        if (sortOrder != null) {
            templateNodeRepository.updateSortOrder(nodeId, sortOrder);
        }

        return true;
    }

    @Override
    public Map<String, Object> getTemplateWithFullTree(TemplateId templateId) {
        // 参数校验
        if (templateId == null) {
            throw new IllegalArgumentException("模板ID不能为空");
        }
        // 查询模板
        Optional<StructureTemplateEntity> templateOpt = templateRepository.findById(templateId);
        if (templateOpt.isEmpty()) {
            throw new AppException("模板不存在");
        }
        // 查询模板的所有节点
        List<StructureTemplateNodeEntity> nodes = templateNodeRepository.findByTemplateId(templateId);
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("template", templateOpt.get());
        result.put("nodes", nodes);

        return result;
    }

    @Override
    public Map<String, Object> getTemplateWithFullTreeByCodeAndVersion(TemplateCode templateCode, String version) {
        if (templateCode == null) {
            throw new IllegalArgumentException("模板编码不能为空");
        }
        if (StringUtils.isBlank(version)) {
            throw new IllegalArgumentException("版本号不能为空");
        }
        // 查询模板
        Optional<StructureTemplateEntity> templateOpt = templateRepository.findByTemplateCodeAndVersion(templateCode, version);
        if (templateOpt.isEmpty()) {
            throw new AppException("模板不存在");
        }
        StructureTemplateEntity template = templateOpt.get();
        // 获取模板和树结构
        return getTemplateWithFullTree(template.getId());
    }

    @Override
    public boolean deleteTemplateWithAllNodes(TemplateId templateId) {
        // 参数校验
        if (templateId == null) {
            throw new IllegalArgumentException("模板ID不能为空");
        }
        // 先删除所有节点
        templateNodeRepository.deleteByTemplateId(templateId);
        // 再删除模板（逻辑删除，将状态更新为删除）
        int result = templateRepository.deleteById(templateId);

        return result > 0;
    }

    @Override
    public int importNodeTree(TemplateId templateId, TemplateNodeId parentNodeId,
                              List<StructureTemplateNodeEntity> nodes, String creator) {
        if (templateId == null) {
            throw new IllegalArgumentException("模板ID不能为空");
        }
        if (CollectionUtils.isEmpty(nodes)) {
            return 0;
        }
        if (StringUtils.isBlank(creator)) {
            throw new IllegalArgumentException("创建者不能为空");
        }
        // 检查模板是否存在
        Optional<StructureTemplateEntity> templateOpt = templateRepository.findById(templateId);
        if (templateOpt.isEmpty()) {
            throw new AppException("模板不存在");
        }
        // 如果指定了父节点，检查父节点是否存在
        if (parentNodeId != null) {
            Optional<StructureTemplateNodeEntity> parentNodeOpt = templateNodeRepository.findById(parentNodeId);
            if (parentNodeOpt.isEmpty()) {
                throw new AppException("父节点不存在");
            }
        }
        // 为导入的节点设置正确的模板ID和父节点ID
        for (StructureTemplateNodeEntity node : nodes) {
            node.setTemplateId(templateId);
            if (parentNodeId != null) {
                node.setParentId(parentNodeId);
            }
            node.setCreator(creator);
        }

        // 批量保存节点
        return templateNodeRepository.saveBatch(nodes);
    }

    @Override
    public Map<String, Object> validateTemplateStructure(TemplateId templateId) {
        if (templateId == null) {
            throw new IllegalArgumentException("模板ID不能为空");
        }
        // 查询模板
        Optional<StructureTemplateEntity> templateOpt = templateRepository.findById(templateId);
        if (templateOpt.isEmpty()) {
            throw new AppException("模板不存在");
        }
        // 查询模板的所有节点
        List<StructureTemplateNodeEntity> nodes = templateNodeRepository.findByTemplateId(templateId);
        // 校验结果
        Map<String, Object> result = new HashMap<>();
        List<String> issues = new ArrayList<>();
        // 校验逻辑
        if (nodes.isEmpty()) {
            issues.add("模板没有任何节点");
            result.put("valid", false);
        } else {
            // 检查是否有根节点
            long rootNodeCount = nodes.stream()
                    .filter(node -> node.getParentId() == null)
                    .count();
            if (rootNodeCount == 0) {
                issues.add("模板没有根节点");
                result.put("valid", false);
            } else if (rootNodeCount > 1) {
                issues.add("模板有多个根节点，应该只有一个");
                result.put("valid", false);
            } else {
                // 检查节点引用的完整性
                Set<TemplateNodeId> allNodeIds = nodes.stream()
                        .map(StructureTemplateNodeEntity::getId)
                        .collect(Collectors.toSet());
                Set<TemplateNodeId> allParentIds = nodes.stream()
                        .map(StructureTemplateNodeEntity::getParentId)
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
    public List<StructureTemplateEntity> findTemplates(TemplateCode templateCode, Status status, String nameKeyword) {
        return templateRepository.findTemplates(templateCode, status, nameKeyword);
    }

    @Override
    public StructureTemplatePageVO findTemplates(TemplateCode templateCode, Status status, String nameKeyword, int pageNo, int pageSize) {
        return templateRepository.findTemplates(templateCode, status, nameKeyword, pageNo, pageSize);
    }

    @Override
    public int deleteNodeAndChildren(TemplateNodeId nodeId) {
        // 参数校验
        if (nodeId == null) {
            throw new IllegalArgumentException("节点ID不能为空");
        }

        // 删除节点及其子节点
        return templateNodeRepository.deleteSubTree(nodeId);
    }

    @Override
    public boolean enableTemplate(TemplateId templateId) {
        // 参数校验
        if (templateId == null) {
            throw new IllegalArgumentException("模板ID不能为空");
        }

        // 更新状态
        int result = templateRepository.updateStatus(templateId, Status.ENABLED);

        return result > 0;
    }

    @Override
    public boolean disableTemplate(TemplateId templateId) {
        // 参数校验
        if (templateId == null) {
            throw new IllegalArgumentException("模板ID不能为空");
        }

        // 更新状态
        int result = templateRepository.updateStatus(templateId, Status.DISABLED);

        return result > 0;
    }

    @Override
    public StructureTemplateEntity findTemplateById(TemplateId templateId) {
        if (templateId == null) {
            return null;
        }

        Optional<StructureTemplateEntity> structureTemplateEntityOpt = templateRepository.findById(templateId);
        if (!structureTemplateEntityOpt.isPresent()) {
            return null;
        }

        return structureTemplateEntityOpt.get();
    }

    @Override
    public StructureTemplateNodeEntity findNodeById(TemplateNodeId nodeId) {
        if (nodeId == null) {
            return null;
        }
        Optional<StructureTemplateNodeEntity> structureTemplateNodeEntityOpt = templateNodeRepository.findById(nodeId);
        if (!structureTemplateNodeEntityOpt.isPresent()) {
            return null;
        }

        return structureTemplateNodeEntityOpt.get();
    }

    @Override
    public List<StructureTemplateNodeEntity> findChildNodes(TemplateNodeId parentNodeId) {
        if (parentNodeId == null) {
            return Collections.emptyList();
        }
        return templateNodeRepository.findByParentId(parentNodeId);
    }

    @Override
    public List<StructureTemplateNodeEntity> findSubTree(TemplateNodeId rootNodeId) {
        if (rootNodeId == null) {
            return Collections.emptyList();
        }
        return templateNodeRepository.findSubTree(rootNodeId);
    }

    /**
     * 复制节点结构
     */
    private void copyNodeStructure(TemplateId sourceTemplateId, TemplateId targetTemplateId, String creator) {
        // 查询源模板的所有节点
        List<StructureTemplateNodeEntity> sourceNodes = templateNodeRepository.findByTemplateId(sourceTemplateId);
        if (CollectionUtils.isEmpty(sourceNodes)) {
            return;
        }

        // 构建节点ID映射关系（旧ID -> 新节点）
        Map<TemplateNodeId, StructureTemplateNodeEntity> oldToNewNodeMap = new HashMap<>();

        // 先复制所有根节点
        List<StructureTemplateNodeEntity> rootNodes = sourceNodes.stream()
                .filter(node -> node.getParentId() == null)
                .collect(Collectors.toList());

        for (StructureTemplateNodeEntity rootNode : rootNodes) {
            StructureTemplateNodeEntity newRootNode = StructureTemplateNodeEntity.createRoot(
                    targetTemplateId,
                    rootNode.getNodeType(),
                    rootNode.getNodeCode(),
                    rootNode.getNodeName(),
                    rootNode.getNodeNameEn(),
                    rootNode.getSortOrder(),
                    creator
            );

            templateNodeRepository.save(newRootNode);
            oldToNewNodeMap.put(rootNode.getId(), newRootNode);
        }

        // 按层次复制子节点
        boolean hasNewNodes = true;
        Set<TemplateNodeId> processedNodeIds = new HashSet<>(rootNodes.stream()
                .map(StructureTemplateNodeEntity::getId)
                .collect(Collectors.toSet()));

        while (hasNewNodes) {
            hasNewNodes = false;

            for (StructureTemplateNodeEntity sourceNode : sourceNodes) {
                // 跳过已处理的节点
                if (processedNodeIds.contains(sourceNode.getId())) {
                    continue;
                }

                // 如果父节点已处理，则可以处理当前节点
                TemplateNodeId parentId = sourceNode.getParentId();
                if (parentId != null && processedNodeIds.contains(parentId)) {
                    // 获取新的父节点
                    StructureTemplateNodeEntity newParentNode = oldToNewNodeMap.get(parentId);

                    // 创建新节点
                    StructureTemplateNodeEntity newNode = StructureTemplateNodeEntity.createChild(
                            targetTemplateId,
                            newParentNode.getId(),
                            sourceNode.getNodeType(),
                            sourceNode.getNodeCode(),
                            sourceNode.getNodeName(),
                            sourceNode.getNodeNameEn(),
                            sourceNode.getSortOrder(),
                            creator
                    );

                    templateNodeRepository.save(newNode);
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
    private void validateNodeTypeSpecificParams(NodeType nodeType, CategoryId categoryId, GroupId groupId) {
        if (nodeType == NodeType.CATEGORY && categoryId == null) {
            throw new AppException("CATEGORY类型节点必须指定系统大类ID");
        }
        if (nodeType == NodeType.GROUP && groupId == null) {
            throw new AppException("GROUP类型节点必须指定系统组ID");
        }
    }

    /**
     * 生成节点编码
     */
    private String generateNodeCode(TemplateId templateId, TemplateNodeId parentNodeId, NodeType nodeType) {
        // 实际项目中可能需要根据业务规则生成唯一编码
        // 这里简单实现，实际应用中可能需要更复杂的逻辑
        String prefix = nodeType.name().substring(0, 3);
        String timestamp = String.valueOf(System.currentTimeMillis());
        return prefix + "_" + timestamp;
    }

    /**
     * 检查节点是否在子树中（防止循环引用）
     */
    private boolean isNodeInSubtree(TemplateNodeId targetNodeId, TemplateNodeId subtreeRootId) {
        if (targetNodeId.equals(subtreeRootId)) {
            return true;
        }

        List<StructureTemplateNodeEntity> children = templateNodeRepository.findByParentId(subtreeRootId);
        for (StructureTemplateNodeEntity child : children) {
            if (isNodeInSubtree(targetNodeId, child.getId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查节点之间是否存在循环引用
     */
    private boolean checkCyclicReference(List<StructureTemplateNodeEntity> nodes) {
        // 构建节点ID到节点的映射
        Map<TemplateNodeId, StructureTemplateNodeEntity> nodeMap = nodes.stream()
                .collect(Collectors.toMap(StructureTemplateNodeEntity::getId, node -> node));

        // 对每个节点进行循环检测
        for (StructureTemplateNodeEntity node : nodes) {
            Set<TemplateNodeId> visited = new HashSet<>();
            if (hasCycle(node.getId(), nodeMap, visited)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 深度优先搜索检测循环引用
     */
    private boolean hasCycle(TemplateNodeId nodeId, Map<TemplateNodeId, StructureTemplateNodeEntity> nodeMap, Set<TemplateNodeId> visited) {
        if (visited.contains(nodeId)) {
            return true;
        }

        visited.add(nodeId);

        StructureTemplateNodeEntity node = nodeMap.get(nodeId);
        if (node != null && node.getParentId() != null) {
            return hasCycle(node.getParentId(), nodeMap, visited);
        }

        visited.remove(nodeId);
        return false;
    }

    /**
     * 更新子树的路径和层级
     *
     * @param oldPath  旧路径前缀
     * @param newPath  新路径前缀
     * @param oldLevel 旧层级
     * @param newLevel 新层级
     */
    private void updateSubtreePathAndLevel(String oldPath, String newPath, int oldLevel, int newLevel) {
        // 查询所有以oldPath开头的节点
        List<StructureTemplateNodeEntity> subtreeNodes = templateNodeRepository.findByPathStartingWith(oldPath + "-");

        int levelDiff = newLevel - oldLevel;

        for (StructureTemplateNodeEntity node : subtreeNodes) {
            // 替换路径前缀
            String currentPath = node.getNodePath();
            String updatedPath = newPath + currentPath.substring(oldPath.length());

            // 更新层级
            int updatedLevel = node.getNodeLevel() + levelDiff;

            // 更新节点
            node.updatePathAndLevel(updatedPath, updatedLevel);
            templateNodeRepository.update(node);
        }
    }

}
