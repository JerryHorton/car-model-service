package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.UsageConfigCombinationDetailPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/5 18:53
 * @Description 用法配置组合明细数据访问接口
 * @Author jerryhotton
 */

@Mapper
public interface IUsageConfigCombinationDetailDao {

    /**
     * 插入用法配置组合明细
     *
     * @param detailPO 用法配置组合明细PO
     * @return 影响的行数
     */
    int insert(UsageConfigCombinationDetailPO detailPO);

    /**
     * 批量插入用法配置组合明细
     *
     * @param details 明细列表
     * @return 影响的行数
     */
    int insertBatch(List<UsageConfigCombinationDetailPO> details);

    /**
     * 根据组合ID查询所有明细
     *
     * @param combinationId 组合ID
     * @return 明细列表
     */
    List<UsageConfigCombinationDetailPO> selectByCombinationId(Long combinationId);

    /**
     * 根据用法ID查询所有配置项ID
     *
     * @param usageId 用法ID
     * @return 配置项ID列表
     */
    List<Long> selectConfigItemIdsByUsageId(Long usageId);

    /**
     * 根据配置项ID查询明细
     *
     * @param configItemId 配置项ID
     * @return 明细列表
     */
    List<UsageConfigCombinationDetailPO> selectByConfigItemId(Long configItemId);

    /**
     * 根据组合ID删除所有明细
     *
     * @param combinationId 组合ID
     * @return 影响的行数
     */
    int deleteByCombinationId(Long combinationId);

    /**
     * 根据配置项ID删除相关明细
     *
     * @param configItemId 配置项ID
     * @return 影响的行数
     */
    int deleteByConfigItemId(Long configItemId);

}
