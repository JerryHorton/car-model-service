package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.usage.adapter.repository.IUsageConfigCombinationDetailRepository;
import cn.cug.sxy.domain.usage.adapter.repository.IUsageConfigCombinationRepository;
import cn.cug.sxy.domain.usage.model.entity.UsageConfigCombinationDetailEntity;
import cn.cug.sxy.domain.usage.model.entity.UsageConfigCombinationEntity;
import cn.cug.sxy.domain.usage.model.valobj.ConfigItemId;
import cn.cug.sxy.domain.usage.model.valobj.UsageConfigCombinationId;
import cn.cug.sxy.domain.usage.model.valobj.UsageId;
import cn.cug.sxy.infrastructure.converter.UsageConfigCombinationConverter;
import cn.cug.sxy.infrastructure.converter.UsageConfigCombinationDetailConverter;
import cn.cug.sxy.infrastructure.dao.IUsageConfigCombinationDao;
import cn.cug.sxy.infrastructure.dao.po.UsageConfigCombinationPO;
import cn.cug.sxy.types.exception.AppException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version 1.0
 * @Date 2025/8/6 09:46
 * @Description 用法配置组合仓储实现
 * @Author jerryhotton
 */

@Repository
public class UsageConfigCombinationRepository implements IUsageConfigCombinationRepository {

    private final IUsageConfigCombinationDao combinationDao;
    private final IUsageConfigCombinationDetailRepository detailRepository;
    private final TransactionTemplate transactionTemplate;

    public UsageConfigCombinationRepository(
            IUsageConfigCombinationDao combinationDao,
            IUsageConfigCombinationDetailRepository detailRepository,
            TransactionTemplate transactionTemplate) {
        this.combinationDao = combinationDao;
        this.detailRepository = detailRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public UsageConfigCombinationEntity save(UsageConfigCombinationEntity combination) {
        if (combination == null) {
            throw new IllegalArgumentException("用法配置组合实体不能为空");
        }
        UsageConfigCombinationPO combinationPO = UsageConfigCombinationConverter.toPO(combination);
        if (combination.getId() == null) {
            // 插入操作：保存组合和明细
            int result = combinationDao.insert(combinationPO);
            if (result > 0) {
                combination.setId(new UsageConfigCombinationId(combinationPO.getId()));
                // 保存组合明细
                saveDetails(combination);
            }
        } else {
            transactionTemplate.execute(status -> {
                try {
                    // 更新操作：更新组合信息和明细
                    int result = combinationDao.update(combinationPO);
                    if (result > 0) {
                        // 先删除旧的明细，再保存新的明细
                        detailRepository.deleteByCombinationId(combination.getId());
                        saveDetails(combination);
                    }

                    return 1;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    throw new AppException("更新用法配置组合失败", e);
                }
            });
        }

        return combination;
    }

    /**
     * 保存组合明细
     *
     * @param combination 组合实体
     */
    private void saveDetails(UsageConfigCombinationEntity combination) {
        if (combination.getConfigItemIds() != null && !combination.getConfigItemIds().isEmpty()) {
            List<UsageConfigCombinationDetailEntity> details =
                    UsageConfigCombinationDetailConverter.createDetailEntities(
                            combination.getId(), combination.getConfigItemIds());
            detailRepository.saveBatch(details);
        }
    }

    @Override
    public Optional<UsageConfigCombinationEntity> findById(UsageConfigCombinationId combinationId) {
        if (combinationId == null || combinationId.getId() == null) {
            return Optional.empty();
        }
        UsageConfigCombinationPO combinationPO = combinationDao.selectById(combinationId.getId());
        if (combinationPO == null) {
            return Optional.empty();
        }
        // 转换为实体
        UsageConfigCombinationEntity entity = UsageConfigCombinationConverter.toEntity(combinationPO);
        // 查询并设置配置项列表
        List<UsageConfigCombinationDetailEntity> details = detailRepository.findByCombinationId(combinationId);
        List<ConfigItemId> configItemIds = UsageConfigCombinationDetailConverter.extractConfigItemIds(details);
        entity.setConfigItemIds(configItemIds);

        return Optional.of(entity);
    }

    @Override
    public List<UsageConfigCombinationEntity> findByUsageId(UsageId usageId) {
        if (usageId == null || usageId.getId() == null) {
            return Collections.emptyList();
        }
        List<UsageConfigCombinationPO> combinationPOs = combinationDao.selectByUsageId(usageId.getId());
        if (combinationPOs == null || combinationPOs.isEmpty()) {
            return Collections.emptyList();
        }
        List<UsageConfigCombinationEntity> entities = UsageConfigCombinationConverter.toEntityList(combinationPOs);
        // 为每个组合查询并设置配置项列表
        for (UsageConfigCombinationEntity entity : entities) {
            List<UsageConfigCombinationDetailEntity> details =
                    detailRepository.findByCombinationId(entity.getId());
            List<ConfigItemId> configItemIds =
                    UsageConfigCombinationDetailConverter.extractConfigItemIds(details);
            entity.setConfigItemIds(configItemIds);
        }

        return entities;
    }

    @Override
    public boolean deleteById(UsageConfigCombinationId combinationId) {
        if (combinationId == null || combinationId.getId() == null) {
            return false;
        }
        transactionTemplate.execute(status -> {
            try {
                // 先删除明细
                detailRepository.deleteByCombinationId(combinationId);
                // 再删除组合
                combinationDao.deleteById(combinationId.getId());
                return 1;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw new AppException("删除用法配置组合失败", e);
            }
        });

        return true;
    }

    @Override
    public int deleteByUsageId(UsageId usageId) {
        if (usageId == null) {
            return 0;
        }
        AtomicInteger result = new AtomicInteger();
        // 先查询所有组合
        List<UsageConfigCombinationEntity> combinations = findByUsageId(usageId);
        transactionTemplate.execute(status -> {
            try {
                // 删除所有明细
                for (UsageConfigCombinationEntity combination : combinations) {
                    detailRepository.deleteByCombinationId(combination.getId());
                }
                // 删除所有组合
                result.set(combinationDao.deleteByUsageId(usageId.getId()));

                return 1;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw new AppException("删除用法配置组合失败", e);
            }
        });

        return result.get();
    }

}
