package cn.cug.sxy.domain.usage.service;

import cn.cug.sxy.domain.structure.adapter.repository.IInstanceNodeRepository;
import cn.cug.sxy.domain.structure.model.entity.StructureInstanceNodeEntity;
import cn.cug.sxy.domain.structure.model.valobj.InstanceId;
import cn.cug.sxy.domain.structure.model.valobj.InstanceNodeId;
import cn.cug.sxy.domain.structure.model.valobj.NodeType;
import cn.cug.sxy.domain.usage.adapter.repository.IUsageConfigCombinationRepository;
import cn.cug.sxy.domain.usage.adapter.repository.IUsageRepository;
import cn.cug.sxy.domain.usage.model.aggregate.UsageAggregate;
import cn.cug.sxy.domain.usage.model.entity.ConfigItemEntity;
import cn.cug.sxy.domain.usage.model.valobj.*;
import cn.cug.sxy.domain.usage.model.entity.UsageConfigCombinationEntity;
import cn.cug.sxy.domain.usage.model.entity.UsageEntity;
import cn.cug.sxy.types.enums.Status;
import cn.cug.sxy.types.exception.AppException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @version 1.0
 * @Date 2025/8/6 14:35
 * @Description 用法管理服务
 * @Author jerryhotton
 */

@Service
public class UsageManagementService implements IUsageManagementService {

    private final IUsageRepository usageRepository;
    private final IUsageConfigCombinationRepository combinationRepository;
    private final IInstanceNodeRepository instanceNodeRepository;
    private final IConfigQueryService configQueryService;
    private final IConfigManagementService configManagementService;

    public UsageManagementService(IUsageRepository usageRepository,
                                  IUsageConfigCombinationRepository combinationRepository,
                                  IInstanceNodeRepository instanceNodeRepository,
                                  IConfigQueryService configQueryService,
                                  IConfigManagementService configManagementService) {
        this.usageRepository = usageRepository;
        this.combinationRepository = combinationRepository;
        this.instanceNodeRepository = instanceNodeRepository;
        this.configQueryService = configQueryService;
        this.configManagementService = configManagementService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UsageCreationAggregate createUsage(Long instanceId, Long parentGroupNodeId, Long groupId,
                                              String usageName, MultipartFile explodedViewFile, Integer sortOrder,
                                              String creator, List<UsageConfigCombinationSpec> combinationSpecs) {
        // 1. 参数验证
        validateCreateUsageParams(instanceId, parentGroupNodeId, groupId, usageName, creator);
        // 2. 验证配置组合的有效性
        if (combinationSpecs != null && !combinationSpecs.isEmpty()) {
            validateUsageCombinations(combinationSpecs);
        }
        // 3. 验证父节点存在且为GROUP类型
        InstanceNodeId parentNodeId = new InstanceNodeId(parentGroupNodeId);
        Optional<StructureInstanceNodeEntity> parentNodeOpt = instanceNodeRepository.findById(parentNodeId);
        if (parentNodeOpt.isEmpty()) {
            throw new AppException("父节点不存在");
        }
        StructureInstanceNodeEntity parentNode = parentNodeOpt.get();
        if (parentNode.getNodeType() != NodeType.GROUP) {
            throw new AppException("父节点必须是GROUP类型");
        }
        // 4. 创建用法实体（Repository层处理文件上传）
        UsageEntity usage = UsageEntity.create(usageName, null, creator.trim());
        UsageEntity savedUsage = usageRepository.save(usage, explodedViewFile, groupId);
        // 5. 创建配置组合
        List<UsageConfigCombinationEntity> savedCombinations = new ArrayList<>();
        if (combinationSpecs != null && !combinationSpecs.isEmpty()) {
            savedCombinations = createCombinations(savedUsage.getId(), combinationSpecs);
        }
        // 6. 创建用法类型的实例节点
        StructureInstanceNodeEntity usageNode = createUsageInstanceNode(
                new InstanceId(instanceId), parentNodeId, savedUsage, sortOrder, creator, parentNode);
        // 7. 返回结果
        return new UsageCreationAggregate(savedUsage, savedCombinations, usageNode);
    }

    @Override
    public UsageAggregate updateUsage(UsageId usageId, String usageName,
                                      MultipartFile explodedViewFile, Long groupId,
                                      List<UsageConfigCombinationSpec> combinationSpecs) {
        // 参数验证
        if (usageId == null) {
            throw new IllegalArgumentException("用法ID不能为空");
        }
        if (usageName == null || usageName.trim().isEmpty()) {
            throw new IllegalArgumentException("用法名称不能为空");
        }
        // 查询现有用法
        Optional<UsageEntity> usageOpt = usageRepository.findById(usageId);
        if (usageOpt.isEmpty()) {
            throw new AppException("用法不存在，ID: " + usageId.getId());
        }
        // 更新用法基本信息（Repository层处理文件上传）
        UsageEntity usage = usageOpt.get();
        // 先不设置图片URL，让Repository处理
        usage.update(usageName.trim(), null);
        // 使用带文件处理的save方法
        UsageEntity updatedUsage;
        if (explodedViewFile != null && !explodedViewFile.isEmpty()) {
            // 有新文件，使用文件处理的save方法
            updatedUsage = usageRepository.save(usage, explodedViewFile, groupId);
        } else {
            // 没有新文件，使用普通save方法
            updatedUsage = usageRepository.save(usage);
        }
        // 更新配置组合（如果提供了新的配置组合）
        List<UsageConfigCombinationEntity> updatedCombinations;
        if (combinationSpecs != null) {
            // 验证新的配置组合
            validateUsageCombinations(combinationSpecs);
            // 删除现有配置组合
            combinationRepository.deleteByUsageId(usageId);
            // 创建新的配置组合
            updatedCombinations = createCombinations(usageId, combinationSpecs);
        } else {
            // 不更新配置组合，查询现有的
            updatedCombinations = combinationRepository.findByUsageId(usageId);
        }

        return new UsageAggregate(updatedUsage, updatedCombinations);
    }

    @Override
    public List<UsageEntity> findUsagesByGroup(Long groupNodeId) {
        return findUsagesByGroup(groupNodeId, Status.ENABLED);
    }

    @Override
    public List<UsageEntity> findAllUsagesByGroup(Long groupNodeId) {
        return findUsagesByGroup(groupNodeId, null);
    }

    @Override
    public List<UsageConfigCombinationEntity> findUsageDetail(UsageId usageId) {
        if (usageId == null) {
            throw new IllegalArgumentException("用法ID不能为空");
        }
        // 1. 查询用法基本信息
        Optional<UsageEntity> usageOpt = usageRepository.findById(usageId);
        if (!usageOpt.isPresent()) {
            throw new AppException("用法不存在，ID: " + usageId.getId());
        }
        // 2. 查询配置组合
        List<UsageConfigCombinationEntity> combinations = combinationRepository.findByUsageId(usageId);
        // 3. 为每个配置组合加载完整的配置项信息
        for (UsageConfigCombinationEntity combination : combinations) {
            // 获取配置项ID列表
            List<ConfigItemId> configItemIds = combination.getConfigItemIds();
            if (configItemIds != null && !configItemIds.isEmpty()) {
                // 通过配置管理服务获取完整的配置项信息
                List<ConfigItemEntity> configItems = configQueryService.findConfigItemsByIds(configItemIds);
                combination.loadConfigItemDetails(configItems);
            }
        }

        return combinations;
    }

    @Override
    public UsageEntity updateUsageStatus(UsageId usageId, boolean enabled) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUsage(UsageId usageId) {
        return updateStatus(usageId, Status.DELETED);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean restoreUsage(UsageId usageId) {
        return updateStatus(usageId, Status.ENABLED);
    }

    @Override
    public String getUsageConfigDescription(UsageId usageId) {
        return "";
    }

    @Override
    public void validateUsageCombinations(List<UsageConfigCombinationSpec> combinationSpecs) {
        if (combinationSpecs == null || combinationSpecs.isEmpty()) {
            // 允许没有配置组合
            return;
        }
        for (UsageConfigCombinationSpec spec : combinationSpecs) {
            // 使用规格说明的验证方法
            if (!spec.isValid()) {
                throw new AppException("配置组合规格说明无效");
            }
            // 验证配置项的有效性
            configManagementService.validateConfigItems(spec.getConfigItemIds());
            // 检查同类别冲突
            if (configManagementService.hasConflictingCategories(spec.getConfigItemIds())) {
                throw new AppException("配置组合中存在同类别的多个配置项");
            }
        }
    }

    @Override
    public boolean deleteCombination(Long combinationId) {
        return combinationRepository.deleteById(new UsageConfigCombinationId(combinationId));
    }

    private List<UsageEntity> findUsagesByGroup(Long groupNodeId, Status status) {
        if (groupNodeId == null) {
            throw new IllegalArgumentException("组节点ID不能为空");
        }
        // 1. 验证组节点存在且为GROUP类型
        InstanceNodeId nodeId = new InstanceNodeId(groupNodeId);
        Optional<StructureInstanceNodeEntity> groupNodeOpt = instanceNodeRepository.findById(nodeId);
        if (groupNodeOpt.isEmpty()) {
            throw new AppException("组节点不存在，ID: " + groupNodeId);
        }
        StructureInstanceNodeEntity groupNode = groupNodeOpt.get();
        if (groupNode.getNodeType() != NodeType.GROUP) {
            throw new AppException("节点不是GROUP类型，ID: " + groupNodeId + ", 类型: " + groupNode.getNodeType());
        }
        // 2. 查询该组下的所有用法类型子节点
        List<StructureInstanceNodeEntity> usageNodes = instanceNodeRepository.findByParentId(nodeId);
        if (usageNodes.isEmpty()) {
            return Collections.emptyList();
        }
        // 3. 提取用法ID列表
        Stream<StructureInstanceNodeEntity> stream = usageNodes.stream();
        if (status != null) {
            stream = stream.filter(node -> node.getStatus() == status);
        }
        List<UsageId> usageIds = stream
                .filter(node -> node.getUsageId() != null)
                .map(node -> new UsageId(node.getUsageId()))
                .collect(Collectors.toList());
        if (usageIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 4. 批量查询用法实体
        return usageRepository.findByIds(usageIds);
    }

    private boolean updateStatus(UsageId usageId, Status status) {
        if (usageId == null || status == null) {
            throw new IllegalArgumentException("用法ID不能为空");
        }
        // 查询用法实体
        Optional<UsageEntity> usageOpt = usageRepository.findById(usageId);
        if (!usageOpt.isPresent()) {
            throw new AppException("用法不存在，ID: " + usageId.getId());
        }
        UsageEntity usage = usageOpt.get();
        // 检查状态
        if (usage.getStatus() == status) {
            return true;
        }
        // 设置为删除状态（逻辑删除）
        usage.setStatus(status);
        usageRepository.save(usage);
        // 删除对应实例节点（逻辑删除）
        instanceNodeRepository.updateStatusByUsageId(usageId, status);

        return true;
    }

    /**
     * 创建配置组合
     *
     * @param usageId          用法ID
     * @param combinationSpecs 配置组合规格说明列表
     * @return 创建的配置组合实体列表
     */
    private List<UsageConfigCombinationEntity> createCombinations(UsageId usageId,
                                                                  List<UsageConfigCombinationSpec> combinationSpecs) {
        List<UsageConfigCombinationEntity> savedCombinations = new ArrayList<>();
        for (int i = 0; i < combinationSpecs.size(); i++) {
            UsageConfigCombinationSpec spec = combinationSpecs.get(i);
            // 创建配置组合实体
            UsageConfigCombinationEntity combination = UsageConfigCombinationEntity.create(
                    usageId,
                    spec.getCombinationName(),
                    spec.getSortOrder() != null ? spec.getSortOrder() : (i + 1),
                    spec.getConfigItemIds()
            );
            // 保存配置组合
            UsageConfigCombinationEntity saved = combinationRepository.save(combination);
            savedCombinations.add(saved);
        }

        return savedCombinations;
    }

    /**
     * 创建用法类型的实例节点
     */
    private StructureInstanceNodeEntity createUsageInstanceNode(
            InstanceId instanceId, InstanceNodeId parentNodeId, UsageEntity usage,
            Integer sortOrder, String creator, StructureInstanceNodeEntity parentNode) {
        // 生成节点编码
        String nodeCode = generateUsageNodeCode(usage.getId().getId());
        // 创建用法类型节点
        StructureInstanceNodeEntity usageNode = StructureInstanceNodeEntity.createChild(
                instanceId, parentNodeId, NodeType.USAGE, nodeCode, usage.getUsageName(),
                null, sortOrder, creator);
        // 设置用法ID关联
        usageNode.setUsageId(usage.getId().getId());
        // 保存节点
        instanceNodeRepository.save(usageNode);
        // 设置节点路径和层级
        if (usageNode.getId() != null) {
            String parentPath = parentNode.getNodePath();
            String nodePath = parentPath + "-" + usageNode.getId().getId();
            int nodeLevel = parentNode.getNodeLevel() + 1;
            usageNode.updatePathAndLevel(nodePath, nodeLevel);
            instanceNodeRepository.update(usageNode);
        }

        return usageNode;
    }

    /**
     * 验证创建用法的参数
     */
    private void validateCreateUsageParams(Long instanceId, Long parentGroupNodeId, Long groupId,
                                           String usageName, String creator) {
        if (instanceId == null) {
            throw new IllegalArgumentException("实例ID不能为空");
        }
        if (parentGroupNodeId == null) {
            throw new IllegalArgumentException("父节点ID不能为空");
        }
        if (groupId == null || groupId <= 0) {
            throw new IllegalArgumentException("分组ID不能为空或小于等于0");
        }
        if (usageName == null || usageName.trim().isEmpty()) {
            throw new IllegalArgumentException("用法名称不能为空");
        }
        if (creator == null || creator.trim().isEmpty()) {
            throw new IllegalArgumentException("创建人不能为空");
        }
    }

    /**
     * 生成用法节点编码
     */
    private String generateUsageNodeCode(Long usageId) {
        return String.format("USAGE_%d_%d", usageId, System.currentTimeMillis());
    }

}
