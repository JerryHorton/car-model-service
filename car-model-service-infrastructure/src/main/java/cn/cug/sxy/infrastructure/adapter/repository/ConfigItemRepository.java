package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.usage.adapter.repository.IConfigItemRepository;
import cn.cug.sxy.domain.usage.model.entity.ConfigItemEntity;
import cn.cug.sxy.domain.usage.model.valobj.ConfigCategoryId;
import cn.cug.sxy.domain.usage.model.valobj.ConfigItemId;
import cn.cug.sxy.infrastructure.converter.ConfigItemConverter;
import cn.cug.sxy.infrastructure.dao.IConfigItemDao;
import cn.cug.sxy.infrastructure.dao.po.ConfigItemPO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/8/6 09:28
 * @Description 配置项仓储实现
 * @Author jerryhotton
 */

@Repository
public class ConfigItemRepository implements IConfigItemRepository {

    private final IConfigItemDao configItemDao;

    public ConfigItemRepository(IConfigItemDao configItemDao) {
        this.configItemDao = configItemDao;
    }

    @Override
    public ConfigItemEntity save(ConfigItemEntity configItem) {
        if (configItem == null) {
            throw new IllegalArgumentException("配置项实体不能为空");
        }
        ConfigItemPO itemPO = ConfigItemConverter.toPO(configItem);
        if (configItem.getId() == null) {
            // 插入操作
            configItemDao.insert(itemPO);
            if (itemPO.getId() != null) {
                configItem.setId(new ConfigItemId(itemPO.getId()));
            }
        } else {
            // 更新操作
            configItemDao.update(itemPO);
        }
        return configItem;
    }

    @Override
    public Optional<ConfigItemEntity> findById(ConfigItemId itemId) {
        if (itemId == null) {
            return Optional.empty();
        }
        ConfigItemPO itemPO = configItemDao.selectById(itemId.getId());
        if (itemPO == null) {
            return Optional.empty();
        }
        ConfigItemEntity entity = ConfigItemConverter.toEntity(itemPO);

        return Optional.of(entity);
    }

    @Override
    public Optional<ConfigItemEntity> findByCode(String itemCode) {
        if (StringUtils.isBlank(itemCode)) {
            return Optional.empty();
        }
        ConfigItemPO itemPO = configItemDao.selectByCode(itemCode);
        if (itemPO == null) {
            return Optional.empty();
        }
        ConfigItemEntity entity = ConfigItemConverter.toEntity(itemPO);

        return Optional.of(entity);
    }

    @Override
    public List<ConfigItemEntity> findEnabledByCategoryId(ConfigCategoryId categoryId) {
        if (categoryId == null) {
            return Collections.emptyList();
        }
        List<ConfigItemPO> itemPOs = configItemDao.selectEnabledByCategoryId(categoryId.getId());
        if (itemPOs == null || itemPOs.isEmpty()) {
            return Collections.emptyList();
        }

        return ConfigItemConverter.toEntityList(itemPOs);
    }

    @Override
    public List<ConfigItemEntity> findByValueLikeOrNameLike(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            keyword = keyword.trim().replace("%", "\\%").replace("_", "\\_");
        }
        String likePattern = "%" + keyword + "%";
        List<ConfigItemPO> itemPOs = configItemDao.selectByValueLikeOrNameLike(likePattern);
        if (itemPOs == null || itemPOs.isEmpty()) {
            return Collections.emptyList();
        }

        return ConfigItemConverter.toEntityList(itemPOs);
    }

    @Override
    public List<ConfigItemEntity> findByIds(List<ConfigItemId> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> ids = itemIds.stream()
                .map(ConfigItemId::getId)
                .collect(Collectors.toList());
        List<ConfigItemPO> itemPOs = configItemDao.selectByIds(ids);
        if (itemPOs == null || itemPOs.isEmpty()) {
            return Collections.emptyList();
        }

        return ConfigItemConverter.toEntityList(itemPOs);
    }

    @Override
    public boolean existsByCode(String itemCode) {
        if (itemCode == null || itemCode.trim().isEmpty()) {
            return false;
        }

        return configItemDao.countByCode(itemCode) > 0;
    }

    @Override
    public boolean deleteById(ConfigItemId itemId) {
        if (itemId == null) {
            return false;
        }

        return configItemDao.deleteById(itemId.getId()) > 0;
    }

    @Override
    public int deleteByCategoryId(ConfigCategoryId categoryId) {
        if (categoryId == null) {
            return 0;
        }

        return configItemDao.deleteByCategoryId(categoryId.getId());
    }

}
