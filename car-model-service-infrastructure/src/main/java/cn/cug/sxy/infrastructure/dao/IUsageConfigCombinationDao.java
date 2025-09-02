package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.UsageConfigCombinationPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/5 18:52
 * @Description 用法配置组合数据访问接口
 * @Author jerryhotton
 */

@Mapper
public interface IUsageConfigCombinationDao {

    /**
     * 插入用法配置组合
     *
     * @param combinationPO 用法配置组合PO
     * @return 影响的行数
     */
    int insert(UsageConfigCombinationPO combinationPO);

    /**
     * 更新用法配置组合
     *
     * @param combinationPO 用法配置组合PO
     * @return 影响的行数
     */
    int update(UsageConfigCombinationPO combinationPO);

    /**
     * 根据ID查询用法配置组合
     *
     * @param id 组合ID
     * @return 用法配置组合PO
     */
    UsageConfigCombinationPO selectById(Long id);

    /**
     * 根据用法ID查询所有配置组合
     *
     * @param usageId 用法ID
     * @return 配置组合列表
     */
    List<UsageConfigCombinationPO> selectByUsageId(Long usageId);

    /**
     * 删除用法配置组合
     *
     * @param id 组合ID
     * @return 影响的行数
     */
    int deleteById(Long id);

    /**
     * 根据用法ID删除所有配置组合
     *
     * @param usageId 用法ID
     * @return 影响的行数
     */
    int deleteByUsageId(Long usageId);

}
