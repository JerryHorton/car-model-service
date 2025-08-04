package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.structure.adapter.repository.IGroupUsageRepository;
import cn.cug.sxy.domain.structure.model.entity.GroupUsageEntity;
import cn.cug.sxy.domain.structure.model.valobj.Status;
import cn.cug.sxy.domain.structure.model.valobj.UsageId;
import cn.cug.sxy.infrastructure.converter.GroupUsageConverter;
import cn.cug.sxy.infrastructure.dao.IGroupUsageDao;
import cn.cug.sxy.infrastructure.dao.po.GroupUsagePO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/8/4 16:43
 * @Description 系统分组用法仓储实现
 * @Author jerryhotton
 */

@Repository
public class GroupUsageRepository implements IGroupUsageRepository {

    private final IGroupUsageDao groupUsageDao;

    public GroupUsageRepository(IGroupUsageDao groupUsageDao) {
        this.groupUsageDao = groupUsageDao;
    }

    @Override
    public void save(GroupUsageEntity usage) {
        if (usage == null) {
            return;
        }
        GroupUsagePO po = GroupUsageConverter.toPO(usage);
        groupUsageDao.insert(po);
        // 为插入后的实体设置ID
        if (po.getId() != null && usage.getId() == null) {
            usage.setId(new UsageId(po.getId()));
        }
    }

    @Override
    public int saveBatch(List<GroupUsageEntity> usages) {
        if (usages == null || usages.isEmpty()) {
            return 0;
        }
        List<GroupUsagePO> pos = GroupUsageConverter.toPOList(usages);

        return groupUsageDao.insertBatch(pos);
    }

    @Override
    public int update(GroupUsageEntity usage) {
        if (usage == null || usage.getId() == null) {
            return 0;
        }
        GroupUsagePO po = GroupUsageConverter.toPO(usage);

        return groupUsageDao.update(po);
    }

    @Override
    public int updateStatus(UsageId usageId, Status status) {
        if (usageId == null || status == null) {
            return 0;
        }
        GroupUsagePO po = new GroupUsagePO();
        po.setId(usageId.getId());
        po.setStatus(status.getCode());

        return groupUsageDao.updateStatus(po);
    }

    @Override
    public Optional<GroupUsageEntity> findById(UsageId usageId) {
        if (usageId == null) {
            return Optional.empty();
        }
        GroupUsagePO po = groupUsageDao.selectById(usageId.getId());
        if (po == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(GroupUsageConverter.toEntity(po));
    }

    @Override
    public List<GroupUsageEntity> findByGroupId(Long groupId) {
        if (groupId == null) {
            return Collections.emptyList();
        }
        List<GroupUsagePO> pos = groupUsageDao.selectByGroupId(groupId);
        if (pos == null || pos.isEmpty()) {
            return Collections.emptyList();
        }

        return GroupUsageConverter.toEntityList(pos);
    }

    @Override
    public List<GroupUsageEntity> findByGroupIdAndStatus(Long groupId, Status status) {
        if (groupId == null || status == null) {
            return Collections.emptyList();
        }
        GroupUsagePO po = new GroupUsagePO();
        po.setGroupId(groupId);
        po.setStatus(status.getCode());
        List<GroupUsagePO> pos = groupUsageDao.selectByGroupIdAndStatus(po);
        if (pos == null || pos.isEmpty()) {
            return Collections.emptyList();
        }

        return GroupUsageConverter.toEntityList(pos);
    }

    @Override
    public List<GroupUsageEntity> findByNameLike(String nameKeyword) {
        if (nameKeyword == null || nameKeyword.trim().isEmpty()) {
            return List.of();
        }
        nameKeyword = nameKeyword.replace("%", "\\%").replace("_", "\\_");
        nameKeyword = "%" + nameKeyword + "%";
        List<GroupUsagePO> pos = groupUsageDao.selectByNameLike(nameKeyword);
        if (pos == null || pos.isEmpty()) {
            return Collections.emptyList();
        }

        return GroupUsageConverter.toEntityList(pos);
    }

    @Override
    public List<GroupUsageEntity> findByStatus(Status status) {
        if (status == null) {
            return Collections.emptyList();
        }
        List<GroupUsagePO> pos = groupUsageDao.selectByStatus(status.getCode());
        if (pos == null || pos.isEmpty()) {
            return Collections.emptyList();
        }

        return GroupUsageConverter.toEntityList(pos);
    }

    @Override
    public boolean existsByGroupIdAndUsageId(Long groupId, Long usageId) {
        if (groupId == null || usageId == null) {
            return false;
        }
        GroupUsagePO po = new GroupUsagePO();
        po.setGroupId(groupId);
        po.setUsageId(usageId);

        return groupUsageDao.countByGroupIdAndUsageId(po) > 0;
    }

    @Override
    public boolean existsByGroupIdAndUsageName(Long groupId, String usageName) {
        if (groupId == null || StringUtils.isBlank(usageName)) {
            return false;
        }
        GroupUsagePO po = new GroupUsagePO();
        po.setGroupId(groupId);
        po.setUsageName(usageName);

        return groupUsageDao.countByGroupIdAndUsageName(po) > 0;
    }

    @Override
    public int deleteById(UsageId usageId) {
        if (usageId == null) {
            return 0;
        }

        return groupUsageDao.deleteById(usageId.getId());
    }

    @Override
    public int deleteByGroupId(Long groupId) {
        if (groupId == null) {
            return 0;
        }

        return groupUsageDao.deleteByGroupId(groupId);
    }

    @Override
    public int countByGroupId(Long groupId) {
        if (groupId == null) {
            return 0;
        }

        return groupUsageDao.countByGroupId(groupId);
    }

    @Override
    public int countByGroupIdAndStatus(Long groupId, Status status) {
        if (groupId == null || status == null) {
            return 0;
        }
        GroupUsagePO po = new GroupUsagePO();
        po.setGroupId(groupId);
        po.setStatus(status.getCode());

        return groupUsageDao.countByGroupIdAndStatus(po);
    }

}
