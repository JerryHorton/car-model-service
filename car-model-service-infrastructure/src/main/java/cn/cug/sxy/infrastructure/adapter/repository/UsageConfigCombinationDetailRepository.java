package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.usage.adapter.repository.IUsageConfigCombinationDetailRepository;
import cn.cug.sxy.domain.usage.model.entity.UsageConfigCombinationDetailEntity;
import cn.cug.sxy.domain.usage.model.valobj.ConfigItemId;
import cn.cug.sxy.domain.usage.model.valobj.UsageConfigCombinationDetailId;
import cn.cug.sxy.domain.usage.model.valobj.UsageConfigCombinationId;
import cn.cug.sxy.domain.usage.model.valobj.UsageId;
import cn.cug.sxy.infrastructure.converter.UsageConfigCombinationDetailConverter;
import cn.cug.sxy.infrastructure.dao.IUsageConfigCombinationDetailDao;
import cn.cug.sxy.infrastructure.dao.po.UsageConfigCombinationDetailPO;
import cn.cug.sxy.types.common.Constants;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/8/6 09:50
 * @Description 用法配置组合明细仓储实现
 * @Author jerryhotton
 */

@Repository
public class UsageConfigCombinationDetailRepository implements IUsageConfigCombinationDetailRepository {

    private final IUsageConfigCombinationDetailDao detailDao;

    public UsageConfigCombinationDetailRepository(IUsageConfigCombinationDetailDao detailDao) {
        this.detailDao = detailDao;
    }

    @Override
    public UsageConfigCombinationDetailEntity save(UsageConfigCombinationDetailEntity detail) {
        if (detail == null) {
            throw new IllegalArgumentException("用法配置组合明细实体不能为空");
        }
        UsageConfigCombinationDetailPO detailPO = UsageConfigCombinationDetailConverter.toPO(detail);
        if (detail.getId() == null) {
            // 插入操作
            detailDao.insert(detailPO);
            // 回填ID
            if (detail.getId() == null) {
                detail.setId(new UsageConfigCombinationDetailId(detailPO.getId()));
            }
        }

        return detail;
    }

    @Override
    public List<UsageConfigCombinationDetailEntity> saveBatch(List<UsageConfigCombinationDetailEntity> details) {
        if (details == null || details.isEmpty()) {
            return Collections.emptyList();
        }
        // 创建业务键映射
        Map<String, UsageConfigCombinationDetailEntity> businessKeyMap = details.stream()
                .collect(Collectors.toMap(
                        UsageConfigCombinationDetailEntity::getBusinessKey,
                        Function.identity()
                ));
        // 批量插入
        List<UsageConfigCombinationDetailPO> detailPOs = UsageConfigCombinationDetailConverter.toPOList(details);
        detailDao.insertBatch(detailPOs);
        // 回填ID
        detailPOs.stream()
                .filter(po -> po.getId() != null)
                .forEach(po -> {
                    String businessKey = po.getCombinationId() + Constants.UNDERLINE + po.getConfigItemId();
                    UsageConfigCombinationDetailEntity entity = businessKeyMap.get(businessKey);
                    if (entity != null) {
                        entity.setId(new UsageConfigCombinationDetailId(po.getId()));
                    }
                });

        return details;
    }

    @Override
    public List<UsageConfigCombinationDetailEntity> findByCombinationId(UsageConfigCombinationId combinationId) {
        if (combinationId == null || combinationId.getId() == null) {
            return Collections.emptyList();
        }
        List<UsageConfigCombinationDetailPO> detailPOs = detailDao.selectByCombinationId(combinationId.getId());
        if (detailPOs == null || detailPOs.isEmpty()) {
            return Collections.emptyList();
        }

        return UsageConfigCombinationDetailConverter.toEntityList(detailPOs);
    }

    @Override
    public List<ConfigItemId> findConfigItemIdsByUsageId(UsageId usageId) {
        if (usageId == null) {
            return Collections.emptyList();
        }
        List<Long> configItemIds = detailDao.selectConfigItemIdsByUsageId(usageId.getId());
        if (configItemIds == null || configItemIds.isEmpty()) {
            return Collections.emptyList();
        }

        return configItemIds.stream()
                .map(ConfigItemId::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsageConfigCombinationDetailEntity> findByConfigItemId(ConfigItemId configItemId) {
        if (configItemId == null) {
            return Collections.emptyList();
        }
        List<UsageConfigCombinationDetailPO> detailPOS = detailDao.selectByConfigItemId(configItemId.getId());
        if (detailPOS == null || detailPOS.isEmpty()) {
            return Collections.emptyList();
        }

        return UsageConfigCombinationDetailConverter.toEntityList(detailPOS);
    }

    @Override
    public int deleteByCombinationId(UsageConfigCombinationId combinationId) {
        if (combinationId == null || combinationId.getId() == null) {
            return 0;
        }

        return detailDao.deleteByCombinationId(combinationId.getId());
    }

    @Override
    public int deleteByConfigItemId(ConfigItemId configItemId) {
        if (configItemId == null) {
            return 0;
        }

        return detailDao.deleteByConfigItemId(configItemId.getId());
    }

    @Override
    public boolean existsByCombinationIdAndConfigItemId(UsageConfigCombinationId combinationId, ConfigItemId configItemId) {
        if (combinationId == null || configItemId == null) {
            return false;
        }
        List<UsageConfigCombinationDetailEntity> details = findByCombinationId(combinationId);

        return details.stream()
                .anyMatch(detail -> detail.relatesConfigItem(configItemId));
    }

    @Override
    public int countByCombinationId(UsageConfigCombinationId combinationId) {
        if (combinationId == null) {
            return 0;
        }
        List<UsageConfigCombinationDetailEntity> details = findByCombinationId(combinationId);

        return details.size();
    }

}
