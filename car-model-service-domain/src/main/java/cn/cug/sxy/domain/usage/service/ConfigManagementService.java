package cn.cug.sxy.domain.usage.service;

import cn.cug.sxy.domain.usage.adapter.repository.IConfigCategoryRepository;
import cn.cug.sxy.domain.usage.adapter.repository.IConfigItemRepository;
import cn.cug.sxy.domain.usage.adapter.repository.IUsageConfigCombinationDetailRepository;
import cn.cug.sxy.domain.usage.model.entity.ConfigCategoryEntity;
import cn.cug.sxy.domain.usage.model.entity.ConfigItemEntity;
import cn.cug.sxy.domain.usage.model.entity.UsageConfigCombinationDetailEntity;
import cn.cug.sxy.domain.usage.model.valobj.ConfigCategoryId;
import cn.cug.sxy.domain.usage.model.valobj.ConfigItemId;
import cn.cug.sxy.types.exception.AppException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/8/6 11:42
 * @Description 配置管理服务实现类
 * @Author jerryhotton
 */

@Service
public class ConfigManagementService implements IConfigManagementService {

    private final IConfigCategoryRepository configCategoryRepository;
    private final IConfigItemRepository configItemRepository;
    private final IUsageConfigCombinationDetailRepository detailRepository;
    private final String DEFAULT_CREATOR = "default";

    public ConfigManagementService(IConfigCategoryRepository configCategoryRepository,
                                   IConfigItemRepository configItemRepository,
                                   IUsageConfigCombinationDetailRepository detailRepository) {
        this.configCategoryRepository = configCategoryRepository;
        this.configItemRepository = configItemRepository;
        this.detailRepository = detailRepository;
    }

    @Override
    public ConfigCategoryEntity createCategory(String categoryCode, String categoryName, Integer sortOrder, String creator) {
        // 参数验证
        validateCategoryParams(categoryCode, categoryName, creator);
        // 检查编码唯一性
        if (configCategoryRepository.existsByCode(categoryCode)) {
            throw new AppException("配置类别编码已存在: " + categoryCode);
        }
        // 创建并保存
        ConfigCategoryEntity category = ConfigCategoryEntity.create(categoryCode, categoryName, sortOrder, creator);

        return configCategoryRepository.save(category);
    }

    @Override
    public ConfigCategoryEntity updateCategory(ConfigCategoryId categoryId, String categoryCode, String categoryName, Integer sortOrder) {
        if (categoryId == null) {
            throw new IllegalArgumentException("配置类别ID不能为空");
        }
        validateCategoryParams(categoryCode, categoryName, DEFAULT_CREATOR);
        Optional<ConfigCategoryEntity> categoryOpt = configCategoryRepository.findById(categoryId);
        if (!categoryOpt.isPresent()) {
            throw new AppException("配置类别不存在，ID: " + categoryId.getId());
        }
        ConfigCategoryEntity category = categoryOpt.get();
        // 检查编码唯一性
        if (!category.getCategoryCode().equals(categoryCode) && configCategoryRepository.existsByCode(categoryCode)) {
            throw new AppException("配置类别编码已存在: " + categoryCode);
        }
        // 如果排序序号有变化，需要处理排序逻辑
        Integer finalSortOrder = sortOrder;
        if (sortOrder != null && !Objects.equals(sortOrder, category.getSortOrder())) {
            finalSortOrder = adjustSortOrder(categoryId, sortOrder);
        }
        category.update(categoryCode, categoryName, finalSortOrder);

        return configCategoryRepository.save(category);
    }

    @Override
    public boolean deleteCategory(ConfigCategoryId categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("配置类别ID不能为空");
        }
        // 检查是否存在关联的配置项
        List<ConfigItemEntity> items = configItemRepository.findEnabledByCategoryId(categoryId);
        if (!items.isEmpty()) {
            throw new AppException("配置类别下存在配置项，无法删除。请先删除相关配置项。");
        }

        return configCategoryRepository.deleteById(categoryId);
    }

    @Override
    public ConfigItemEntity createConfigItem(ConfigCategoryId categoryId, String itemCode, String itemName, String itemValue, String creator) {
        // 参数验证
        validateConfigItemParams(categoryId, itemCode, itemName, itemValue, creator);
        // 验证编码格式
        if (!ConfigItemEntity.isValidItemCode(itemCode)) {
            throw new IllegalArgumentException("配置项编码格式无效，应为大写字母、数字和下划线组成");
        }
        // 检查编码唯一性
        if (configItemRepository.existsByCode(itemCode)) {
            throw new RuntimeException("配置项编码已存在: " + itemCode);
        }
        // 验证配置类别存在性
        Optional<ConfigCategoryEntity> categoryOpt = configCategoryRepository.findById(categoryId);
        if (!categoryOpt.isPresent()) {
            throw new AppException("配置类别不存在，ID: " + categoryId.getId());
        }
        ConfigCategoryEntity category = categoryOpt.get();
        if (!category.isEnabled()) {
            throw new AppException("配置类别已禁用，无法创建配置项");
        }
        // 创建并保存
        ConfigItemEntity item = ConfigItemEntity.create(categoryId, itemCode, itemName, itemValue, creator);

        return configItemRepository.save(item);
    }

    @Override
    public ConfigItemEntity updateConfigItem(ConfigItemId itemId, String itemName, String itemValue) {
        if (itemId == null) {
            throw new IllegalArgumentException("配置项ID不能为空");
        }
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException("配置项名称不能为空");
        }
        if (itemValue == null || itemValue.trim().isEmpty()) {
            throw new IllegalArgumentException("配置值不能为空");
        }
        Optional<ConfigItemEntity> itemOpt = configItemRepository.findById(itemId);
        if (!itemOpt.isPresent()) {
            throw new RuntimeException("配置项不存在，ID: " + itemId.getId());
        }
        ConfigItemEntity item = itemOpt.get();
        item.update(itemName.trim(), itemValue.trim());

        return configItemRepository.save(item);
    }

    @Override
    public boolean deleteConfigItem(ConfigItemId itemId) {
        if (itemId == null) {
            throw new IllegalArgumentException("配置项ID不能为空");
        }
        // 检查是否被用法配置组合引用（只检查，不删除）
        List<UsageConfigCombinationDetailEntity> references = detailRepository.findByConfigItemId(itemId);
        if (!references.isEmpty()) {
            throw new AppException("配置项被用法配置组合引用，无法删除。请先删除相关的用法配置组合。");
        }

        // 没有引用才删除配置项
        return configItemRepository.deleteById(itemId);
    }

    @Override
    public ConfigCategoryEntity updateCategoryStatus(ConfigCategoryId categoryId, boolean enabled) {
        if (categoryId == null) {
            throw new IllegalArgumentException("配置类别ID不能为空");
        }
        Optional<ConfigCategoryEntity> categoryOpt = configCategoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            throw new AppException("配置类别不存在，ID: " + categoryId.getId());
        }
        ConfigCategoryEntity category = categoryOpt.get();
        if (enabled) {
            category.enable();
        } else {
            category.disable();
        }

        return configCategoryRepository.save(category);
    }

    @Override
    public ConfigItemEntity updateConfigItemStatus(ConfigItemId itemId, boolean enabled) {
        if (itemId == null) {
            throw new IllegalArgumentException("配置项ID不能为空");
        }
        Optional<ConfigItemEntity> itemOpt = configItemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            throw new AppException("配置项不存在，ID: " + itemId.getId());
        }
        ConfigItemEntity item = itemOpt.get();
        if (enabled) {
            item.enable();
        } else {
            item.disable();
        }

        return configItemRepository.save(item);
    }

    @Override
    public void validateConfigItems(List<ConfigItemId> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            throw new IllegalArgumentException("配置项列表不能为空");
        }
        // 查询所有配置项
        List<ConfigItemEntity> configItems = configItemRepository.findByIds(itemIds);
        // 检查配置项是否都存在
        if (configItems.size() != itemIds.size()) {
            Set<ConfigItemId> foundIds = configItems.stream()
                    .map(ConfigItemEntity::getId)
                    .collect(Collectors.toSet());
            Set<ConfigItemId> missingIds = itemIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toSet());
            throw new AppException("配置项不存在: " + missingIds);
        }
        // 检查配置项是否都可用
        List<ConfigItemEntity> disabledItems = configItems.stream()
                .filter(item -> !item.isEnabled())
                .toList();
        if (!disabledItems.isEmpty()) {
            String disabledCodes = disabledItems.stream()
                    .map(ConfigItemEntity::getItemCode)
                    .collect(Collectors.joining(", "));
            throw new AppException("配置项已禁用: " + disabledCodes);
        }
    }

    @Override
    public boolean hasConflictingCategories(List<ConfigItemId> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return false;
        }
        // 查询配置项
        List<ConfigItemEntity> configItems = configItemRepository.findByIds(itemIds);
        // 按类别分组
        Map<ConfigCategoryId, List<ConfigItemEntity>> categoryGroups = configItems.stream()
                .collect(Collectors.groupingBy(ConfigItemEntity::getCategoryId));
        // 检查是否有类别包含多个配置项
        for (Map.Entry<ConfigCategoryId, List<ConfigItemEntity>> entry : categoryGroups.entrySet()) {
            if (entry.getValue().size() > 1) {
                // 存在冲突
                return true;
            }
        }

        return false;
    }

    /**
     * 处理排序序号更新的逻辑
     *
     * @param categoryId   要更新的类别ID
     * @param newSortOrder 新的排序序号
     */
    private Integer adjustSortOrder(ConfigCategoryId categoryId, Integer newSortOrder) {
        if (newSortOrder == null) {
            // 如果没有传排序序号，不处理排序
            return null;
        }
        // 1. 查询所有启用的类别，按排序排列
        List<ConfigCategoryEntity> allCategories = configCategoryRepository.findAllEnabled()
                .stream()
                .sorted(Comparator.comparing(ConfigCategoryEntity::getSortOrder))
                .collect(Collectors.toList());
        // 2. 智能调整排序序号范围
        Integer adjustedSortOrder = newSortOrder;
        if (newSortOrder < 1) {
            adjustedSortOrder = 1; // 小于1时排到第一个
        } else if (newSortOrder > allCategories.size()) {
            adjustedSortOrder = allCategories.size(); // 超过总数时排到最后
        }
        // 3. 找到目标类别
        ConfigCategoryEntity targetCategory = allCategories.stream()
                .filter(category -> category.getId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("类别不存在"));
        Integer oldSortOrder = targetCategory.getSortOrder();
        // 4. 如果排序没有变化，直接返回
        if (Objects.equals(oldSortOrder, adjustedSortOrder)) {
            return oldSortOrder;
        }
        // 5. 重新排序逻辑
        if (adjustedSortOrder < oldSortOrder) {
            // 向前移动：将目标位置到原位置之间的类别向后移动
            for (ConfigCategoryEntity category : allCategories) {
                Integer currentSort = category.getSortOrder();
                if (currentSort >= adjustedSortOrder && currentSort < oldSortOrder) {
                    category.updateSortOrder(currentSort + 1);
                    configCategoryRepository.save(category);
                }
            }
        } else {
            // 向后移动：将原位置到目标位置之间的类别向前移动
            for (ConfigCategoryEntity category : allCategories) {
                Integer currentSort = category.getSortOrder();
                if (currentSort > oldSortOrder && currentSort <= adjustedSortOrder) {
                    category.updateSortOrder(currentSort - 1);
                    configCategoryRepository.save(category);
                }
            }
        }

        return adjustedSortOrder;
    }

    /**
     * 验证配置类别参数
     */
    private void validateCategoryParams(String categoryCode, String categoryName, String creator) {
        if (StringUtils.isBlank(categoryCode)) {
            throw new IllegalArgumentException("配置类别编码不能为空");
        }
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("配置类别名称不能为空");
        }
        if (creator == null || creator.trim().isEmpty()) {
            throw new IllegalArgumentException("创建人不能为空");
        }
        // 验证编码格式
        if (!ConfigCategoryEntity.isValidCategoryCode(categoryCode)) {
            throw new IllegalArgumentException("配置类别编码格式无效，应为大写字母、下划线组成");
        }
    }

    /**
     * 验证配置项参数
     */
    private void validateConfigItemParams(ConfigCategoryId categoryId, String itemCode,
                                          String itemName, String itemValue, String creator) {
        if (categoryId == null) {
            throw new IllegalArgumentException("配置类别ID不能为空");
        }
        if (itemCode == null || itemCode.trim().isEmpty()) {
            throw new IllegalArgumentException("配置项编码不能为空");
        }
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException("配置项名称不能为空");
        }
        if (itemValue == null || itemValue.trim().isEmpty()) {
            throw new IllegalArgumentException("配置值不能为空");
        }
        if (creator == null || creator.trim().isEmpty()) {
            throw new IllegalArgumentException("创建人不能为空");
        }
    }

}
