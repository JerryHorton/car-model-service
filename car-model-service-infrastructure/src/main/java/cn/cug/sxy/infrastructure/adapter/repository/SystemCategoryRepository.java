package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.system.adapter.repository.ISystemCategoryRepository;
import cn.cug.sxy.domain.system.model.entity.SystemCategoryEntity;
import cn.cug.sxy.domain.system.model.valobj.CategoryCode;
import cn.cug.sxy.domain.system.model.valobj.CategoryId;
import cn.cug.sxy.infrastructure.converter.SystemCategoryConverter;
import cn.cug.sxy.infrastructure.dao.ISystemCategoryDao;
import cn.cug.sxy.infrastructure.dao.po.SystemCategoryPO;
import cn.cug.sxy.types.common.Constants;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/28 08:59
 * @Description
 * @Author jerryhotton
 */

@Repository
public class SystemCategoryRepository extends AbstractRepository implements ISystemCategoryRepository {

    private final ISystemCategoryDao systemCategoryDao;

    public SystemCategoryRepository(ISystemCategoryDao systemCategoryDao) {
        this.systemCategoryDao = systemCategoryDao;
    }

    @Override
    public void save(SystemCategoryEntity systemCategoryEntity) {
        systemCategoryDao.insert(SystemCategoryConverter.toPO(systemCategoryEntity));
    }

    @Override
    public int update(SystemCategoryEntity systemCategoryEntity) {
        return systemCategoryDao.update(SystemCategoryConverter.toPO(systemCategoryEntity));
    }

    @Override
    public Optional<SystemCategoryEntity> findById(CategoryId categoryId) {
        if (categoryId == null) {
            return Optional.empty();
        }
        String cacheKey = Constants.RedisKey.SYS_GROUP_BY_CATEGORY_ID_KEY + categoryId.getId();
        SystemCategoryPO systemCategoryPO = getDataFromCacheOrDB(cacheKey, () -> systemCategoryDao.selectById(categoryId.getId()));
        if (systemCategoryPO == null) {
            return Optional.empty();
        }

        return Optional.of(SystemCategoryConverter.toEntity(systemCategoryPO));
    }

    @Override
    public Optional<SystemCategoryEntity> findByCategoryCode(CategoryCode categoryCode) {
        if (categoryCode == null) {
            return Optional.empty();
        }
        String cacheKey = Constants.RedisKey.SYS_CATEGORY_BY_CODE_KEY + categoryCode.getCode();
        SystemCategoryPO systemCategoryPO = getDataFromCacheOrDB(cacheKey, () -> systemCategoryDao.selectByCategoryCode(categoryCode.getCode()));
        if (systemCategoryPO == null) {
            return Optional.empty();
        }

        return Optional.of(SystemCategoryConverter.toEntity(systemCategoryPO));
    }

    @Override
    public List<SystemCategoryEntity> findAll() {
        String cacheKey = Constants.RedisKey.SYS_CATEGORY_ALL_KEY;
        List<SystemCategoryPO> systemCategoryPOList = getDataFromCacheOrDB(cacheKey, systemCategoryDao::selectAll);
        if (systemCategoryPOList == null || systemCategoryPOList.isEmpty()) {
            return Collections.emptyList();
        }

        return SystemCategoryConverter.toEntityList(systemCategoryPOList);
    }

    @Override
    public List<SystemCategoryEntity> findByStatus(String status) {
        if (status == null) {
            return Collections.emptyList();
        }
        List<SystemCategoryPO> systemCategoryPOList = systemCategoryDao.selectByStatus(status);
        if (systemCategoryPOList == null || systemCategoryPOList.isEmpty()) {
            return Collections.emptyList();
        }

        return SystemCategoryConverter.toEntityList(systemCategoryPOList);
    }

    @Override
    public List<SystemCategoryEntity> findByCategoryNameLike(String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) {
            return Collections.emptyList();
        }
        // 转义特殊字符，防止意外的通配符行为
        categoryName = categoryName.replace("%", "\\%").replace("_", "\\_");
        String categoryNameLike = "%" + categoryName + "%";

        List<SystemCategoryPO> systemCategoryPOList = systemCategoryDao.selectByCategoryNameLike(categoryNameLike);
        if (systemCategoryPOList == null || systemCategoryPOList.isEmpty()) {
            return Collections.emptyList();
        }

        return SystemCategoryConverter.toEntityList(systemCategoryPOList);
    }

    @Override
    public boolean existsByCategoryCode(CategoryCode categoryCode) {
        if (categoryCode == null) {
            return false;
        }
        return systemCategoryDao.countByCategoryCode(categoryCode.getCode()) > 0;
    }

}
