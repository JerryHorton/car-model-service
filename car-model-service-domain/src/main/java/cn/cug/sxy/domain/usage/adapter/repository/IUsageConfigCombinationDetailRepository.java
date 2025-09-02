package cn.cug.sxy.domain.usage.adapter.repository;

import cn.cug.sxy.domain.usage.model.entity.UsageConfigCombinationDetailEntity;
import cn.cug.sxy.domain.usage.model.valobj.ConfigItemId;
import cn.cug.sxy.domain.usage.model.valobj.UsageConfigCombinationId;
import cn.cug.sxy.domain.usage.model.valobj.UsageId;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/6 09:48
 * @Description 用法配置组合明细仓储接口
 * @Author jerryhotton
 */

public interface IUsageConfigCombinationDetailRepository {

    /**
     * 保存用法配置组合明细
     *
     * @param detail 用法配置组合明细实体
     * @return 保存后的明细实体（包含生成的ID）
     */
    UsageConfigCombinationDetailEntity save(UsageConfigCombinationDetailEntity detail);

    /**
     * 批量保存用法配置组合明细
     * <p>
     * 用于创建组合时批量插入明细，提高性能
     *
     * @param details 明细实体列表
     * @return 保存后的明细实体列表
     */
    List<UsageConfigCombinationDetailEntity> saveBatch(List<UsageConfigCombinationDetailEntity> details);

    /**
     * 根据组合ID查找所有明细
     * <p>
     * 获取指定组合包含的所有配置项
     *
     * @param combinationId 组合ID
     * @return 明细实体列表
     */
    List<UsageConfigCombinationDetailEntity> findByCombinationId(UsageConfigCombinationId combinationId);

    /**
     * 根据用法ID查询所有配置项ID
     * <p>
     * 跨组合查询，获取用法关联的所有配置项ID（去重）
     * 用于快速获取用法的完整配置项列表
     *
     * @param usageId 用法ID
     * @return 配置项ID列表
     */
    List<ConfigItemId> findConfigItemIdsByUsageId(UsageId usageId);

    /**
     * 根据配置项ID查询明细
     *
     * @param configItemId 配置项ID
     * @return 引用该配置项的明细列表
     */
    List<UsageConfigCombinationDetailEntity> findByConfigItemId(ConfigItemId configItemId);

    /**
     * 根据组合ID删除所有明细
     * <p>
     * 删除组合时的级联操作
     *
     * @param combinationId 组合ID
     * @return 删除的明细数量
     */
    int deleteByCombinationId(UsageConfigCombinationId combinationId);

    /**
     * 根据配置项ID删除相关明细
     * <p>
     * 删除配置项时的级联操作
     * 清理所有引用该配置项的组合明细
     *
     * @param configItemId 配置项ID
     * @return 删除的明细数量
     */
    int deleteByConfigItemId(ConfigItemId configItemId);

    /**
     * 检查组合和配置项的关联是否存在
     * <p>
     * 用于避免重复添加相同的配置项到组合中
     *
     * @param combinationId 组合ID
     * @param configItemId  配置项ID
     * @return 是否存在关联
     */
    boolean existsByCombinationIdAndConfigItemId(UsageConfigCombinationId combinationId, ConfigItemId configItemId);

    /**
     * 统计组合包含的配置项数量
     * <p>
     * 用于验证组合的有效性
     *
     * @param combinationId 组合ID
     * @return 配置项数量
     */
    int countByCombinationId(UsageConfigCombinationId combinationId);

}
