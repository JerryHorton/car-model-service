package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.system.adapter.repository.ISystemGroupRepository;
import cn.cug.sxy.domain.system.model.entity.SystemGroupEntity;
import cn.cug.sxy.domain.system.model.valobj.CategoryId;
import cn.cug.sxy.domain.system.model.valobj.GroupCode;
import cn.cug.sxy.infrastructure.converter.SystemGroupConverter;
import cn.cug.sxy.infrastructure.dao.ISystemGroupDao;
import cn.cug.sxy.infrastructure.dao.po.SystemGroupPO;
import cn.cug.sxy.types.common.Constants;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/28 09:09
 * @Description 系统分组仓储实现
 * @Author jerryhotton
 */

@Repository
public class SystemGroupRepository extends AbstractRepository implements ISystemGroupRepository {

    private final ISystemGroupDao systemGroupDao;

    public SystemGroupRepository(ISystemGroupDao systemGroupDao) {
        this.systemGroupDao = systemGroupDao;
    }

    @Override
    public void save(SystemGroupEntity systemGroupEntity) {
        systemGroupDao.insert(SystemGroupConverter.toPO(systemGroupEntity));
    }

    @Override
    public int update(SystemGroupEntity systemGroupEntity) {
        return systemGroupDao.update(SystemGroupConverter.toPO(systemGroupEntity));
    }

    @Override
    public Optional<SystemGroupEntity> findByGroupCode(GroupCode groupCode) {
        if (groupCode == null) {
            return Optional.empty();
        }
        String cacheKey = Constants.RedisKey.SYS_GROUP_BY_CODE_KEY + groupCode.getCode();
        SystemGroupPO systemGroupPO = getDataFromCacheOrDB(cacheKey, () -> systemGroupDao.selectByGroupCode(groupCode.getCode()));
        if (systemGroupPO == null) {
            return Optional.empty();
        }

        return Optional.of(SystemGroupConverter.toEntity(systemGroupPO));
    }

    @Override
    public List<SystemGroupEntity> findByCategoryId(CategoryId categoryId) {
        if (categoryId == null) {
            return Collections.emptyList();
        }
        String cacheKey = Constants.RedisKey.SYS_GROUP_BY_CATEGORY_ID_KEY + categoryId.getId();
        List<SystemGroupPO> systemGroupPOList = getDataFromCacheOrDB(cacheKey, () -> systemGroupDao.selectByCategoryId(categoryId.getId()));
        if (systemGroupPOList == null || systemGroupPOList.isEmpty()) {
            return Collections.emptyList();
        }

        return SystemGroupConverter.toEntityList(systemGroupPOList);
    }

    @Override
    public List<SystemGroupEntity> findAll() {
        String cacheKey = Constants.RedisKey.SYS_GROUP_ALL_KEY;
        List<SystemGroupPO> systemGroupPOList = getDataFromCacheOrDB(cacheKey, systemGroupDao::selectAll);
        if (systemGroupPOList == null || systemGroupPOList.isEmpty()) {
            return Collections.emptyList();
        }

        return SystemGroupConverter.toEntityList(systemGroupPOList);
    }

    @Override
    public List<SystemGroupEntity> findByStatus(String status) {
        if (status == null) {
            return Collections.emptyList();
        }
        List<SystemGroupPO> systemGroupPOList = systemGroupDao.selectByStatus(status);
        if (systemGroupPOList == null || systemGroupPOList.isEmpty()) {
            return Collections.emptyList();
        }

        return SystemGroupConverter.toEntityList(systemGroupPOList);
    }

    @Override
    public List<SystemGroupEntity> findByGroupNameLike(String groupName) {
        if (groupName == null || groupName.isEmpty()) {
            return Collections.emptyList();
        }
        // 转义特殊字符，防止意外的通配符行为
        groupName = groupName.replace("%", "\\%").replace("_", "\\_");
        String groupNameLike = "%" + groupName + "%";

        List<SystemGroupPO> systemGroupPOList = systemGroupDao.selectByGroupNameLike(groupNameLike);
        if (systemGroupPOList == null || systemGroupPOList.isEmpty()) {
            return Collections.emptyList();
        }

        return SystemGroupConverter.toEntityList(systemGroupPOList);
    }

    @Override
    public boolean existsByGroupCode(GroupCode groupCode) {
        if (groupCode == null) {
            return false;
        }

        return systemGroupDao.countByGroupCode(groupCode.getCode()) > 0;
    }

}
