package cn.cug.sxy.domain.usage.service;

import cn.cug.sxy.domain.usage.adapter.repository.IConfigCategoryRepository;
import cn.cug.sxy.domain.usage.adapter.repository.IConfigItemRepository;
import cn.cug.sxy.domain.usage.model.entity.ConfigCategoryEntity;
import cn.cug.sxy.domain.usage.model.entity.ConfigItemEntity;
import cn.cug.sxy.domain.usage.model.valobj.ConfigCategoryId;
import cn.cug.sxy.domain.usage.model.valobj.ConfigItemId;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @version 1.0
 * @Date 2025/8/6 14:26
 * @Description
 * @Author jerryhotton
 */

@Service
public class ConfigQueryService implements IConfigQueryService {

    private final IConfigCategoryRepository configCategoryRepository;
    private final IConfigItemRepository configItemRepository;

    public ConfigQueryService(IConfigCategoryRepository configCategoryRepository,
                              IConfigItemRepository configItemRepository) {
        this.configCategoryRepository = configCategoryRepository;
        this.configItemRepository = configItemRepository;
    }

    @Override
    public Map<ConfigCategoryEntity, List<ConfigItemEntity>> getAllAvailableConfigs() {
        // 查询所有可用的配置类别
        List<ConfigCategoryEntity> categories = configCategoryRepository.findAllEnabled();
        // 为每个类别查询配置项
        Map<ConfigCategoryEntity, List<ConfigItemEntity>> configMap = new LinkedHashMap<>();
        for (ConfigCategoryEntity category : categories) {
            List<ConfigItemEntity> items = configItemRepository.findEnabledByCategoryId(category.getId());
            configMap.put(category, items);
        }

        return configMap;
    }

    @Override
    public List<ConfigItemEntity> findConfigItemsByIds(List<ConfigItemId> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return Collections.emptyList();
        }

        return configItemRepository.findByIds(itemIds);
    }

    @Override
    public ConfigItemEntity findConfigItemById(ConfigItemId itemId) {
        if (itemId == null) {
            return null;
        }
        Optional<ConfigItemEntity> itemOpt = configItemRepository.findById(itemId);
        if (!itemOpt.isPresent()) {
            return null;
        }

        return itemOpt.get();
    }

    @Override
    public List<ConfigCategoryEntity> findAllEnabledCategories() {
        return configCategoryRepository.findAllEnabled();
    }

    @Override
    public List<ConfigCategoryEntity> searchCategories(String categoryCode, String nameKeyword) {
        return configCategoryRepository.findByCategoryCodeAndNameLike(categoryCode, nameKeyword);
    }

    @Override
    public List<ConfigItemEntity> findEnabledItemsByCategoryId(ConfigCategoryId categoryId) {
        return configItemRepository.findEnabledByCategoryId(categoryId);
    }

    @Override
    public List<ConfigItemEntity> searchItemsByKeyword(String keyword) {
        return configItemRepository.findByValueLikeOrNameLike(keyword);
    }

}
