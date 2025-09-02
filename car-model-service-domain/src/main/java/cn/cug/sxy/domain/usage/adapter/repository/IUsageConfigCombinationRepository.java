package cn.cug.sxy.domain.usage.adapter.repository;

import cn.cug.sxy.domain.usage.model.entity.UsageConfigCombinationEntity;
import cn.cug.sxy.domain.usage.model.valobj.UsageConfigCombinationId;
import cn.cug.sxy.domain.usage.model.valobj.UsageId;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/8/6 09:45
 * @Description 用法配置组合仓储接口
 * @Author jerryhotton
 */

public interface IUsageConfigCombinationRepository {

    /**
     * 保存用法配置组合
     * <p>
     * 如果实体没有ID，则执行插入操作（包括组合和明细）
     * 如果实体有ID，则执行更新操作（更新组合信息和明细）
     *
     * @param combination 用法配置组合实体
     * @return 保存后的用法配置组合实体（包含生成的ID）
     */
    UsageConfigCombinationEntity save(UsageConfigCombinationEntity combination);

    /**
     * 根据ID查找用法配置组合
     * <p>
     * 返回的实体包含完整的配置项ID列表
     *
     * @param combinationId 组合ID
     * @return 用法配置组合实体（可能为空）
     */
    Optional<UsageConfigCombinationEntity> findById(UsageConfigCombinationId combinationId);

    /**
     * 根据用法ID查找所有配置组合
     * <p>
     * 返回指定用法的所有配置组合，按排序序号排序
     * 这是核心查询，用于获取用法的完整配置信息
     *
     * @param usageId 用法ID
     * @return 配置组合列表
     */
    List<UsageConfigCombinationEntity> findByUsageId(UsageId usageId);

    /**
     * 删除用法配置组合
     * <p>
     * 同时删除组合和相关的明细记录
     *
     * @param combinationId 组合ID
     * @return 是否删除成功
     */
    boolean deleteById(UsageConfigCombinationId combinationId);

    /**
     * 根据用法ID删除所有配置组合
     * <p>
     * 级联删除操作，删除用法时使用
     * 同时删除所有组合和相关的明细记录
     *
     * @param usageId 用法ID
     * @return 删除的组合数量
     */
    int deleteByUsageId(UsageId usageId);

}
