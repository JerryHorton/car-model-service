package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.usage.adapter.repository.IConfigCategoryRepository;
import cn.cug.sxy.domain.usage.model.entity.ConfigCategoryEntity;
import cn.cug.sxy.domain.usage.model.valobj.ConfigCategoryId;
import cn.cug.sxy.infrastructure.converter.ConfigCategoryConverter;
import cn.cug.sxy.infrastructure.dao.IConfigCategoryDao;
import cn.cug.sxy.infrastructure.dao.po.ConfigCategoryPO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/8/6 09:11
 * @Description 配置类别仓储实现
 * @Author jerryhotton
 */

@Repository
public class ConfigCategoryRepository implements IConfigCategoryRepository {

    private final IConfigCategoryDao configCategoryDao;

    public ConfigCategoryRepository(IConfigCategoryDao configCategoryDao) {
        this.configCategoryDao = configCategoryDao;
    }

    @Override
    public ConfigCategoryEntity save(ConfigCategoryEntity configCategory) {
        if (configCategory == null) {
            throw new IllegalArgumentException("配置类别实体不能为空");
        }
        ConfigCategoryPO categoryPO = ConfigCategoryConverter.toPO(configCategory);
        if (configCategory.getId() == null) {
            // 插入操作
            configCategoryDao.insert(categoryPO);
            if (categoryPO.getId() != null) {
                configCategory.setId(new ConfigCategoryId(categoryPO.getId()));
            }
        } else {
            // 更新操作
            configCategoryDao.update(categoryPO);
        }

        return configCategory;
    }

    @Override
    public Optional<ConfigCategoryEntity> findById(ConfigCategoryId categoryId) {
        if (categoryId == null || categoryId.getId() == null) {
            return Optional.empty();
        }
        ConfigCategoryPO categoryPO = configCategoryDao.selectById(categoryId.getId());
        if (categoryPO == null) {
            return Optional.empty();
        }
        ConfigCategoryEntity entity = ConfigCategoryConverter.toEntity(categoryPO);

        return Optional.of(entity);
    }

    @Override
    public Optional<ConfigCategoryEntity> findByCode(String categoryCode) {
        if (categoryCode == null || StringUtils.isBlank(categoryCode)) {
            return Optional.empty();
        }
        ConfigCategoryPO categoryPO = configCategoryDao.selectByCode(categoryCode);
        if (categoryPO == null) {
            return Optional.empty();
        }
        ConfigCategoryEntity entity = ConfigCategoryConverter.toEntity(categoryPO);

        return Optional.of(entity);

    }

    @Override
    public List<ConfigCategoryEntity> findAllEnabled() {
        List<ConfigCategoryPO> categoryPOs = configCategoryDao.selectAllEnabled();
        if (categoryPOs == null || categoryPOs.isEmpty()) {
            return Collections.emptyList();
        }

        return ConfigCategoryConverter.toEntityList(categoryPOs);

    }

    @Override
    public List<ConfigCategoryEntity> findByCategoryCodeAndNameLike(String categoryCode, String nameKeyword) {
        if (StringUtils.isNoneBlank(nameKeyword)) {
            nameKeyword = nameKeyword.trim().replace("%", "\\%").replace("_", "\\_");
        }
        String likePattern = "%" + nameKeyword.trim() + "%";
        ConfigCategoryPO categoryPO = new ConfigCategoryPO();
        categoryPO.setCategoryCode(categoryCode);
        categoryPO.setCategoryName(likePattern);
        List<ConfigCategoryPO> categoryPOs = configCategoryDao.selectByCondition(categoryPO);
        if (categoryPOs == null || categoryPOs.isEmpty()) {
            return Collections.emptyList();
        }

        return ConfigCategoryConverter.toEntityList(categoryPOs);
    }

    @Override
    public boolean existsByCode(String categoryCode) {
        if (categoryCode == null || categoryCode.trim().isEmpty()) {
            return false;
        }

        try {
            int count = configCategoryDao.countByCode(categoryCode);
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteById(ConfigCategoryId categoryId) {
        if (categoryId == null || categoryId.getId() == null) {
            return false;
        }
        try {
            int result = configCategoryDao.deleteById(categoryId.getId());
            return result > 0;
        } catch (Exception e) {
            return false;
        }
    }

}
