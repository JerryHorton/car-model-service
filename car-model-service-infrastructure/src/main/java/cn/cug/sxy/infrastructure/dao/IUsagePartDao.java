package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.UsagePartPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/27 14:08
 * @Description 用法备件数据访问接口
 * @Author jerryhotton
 */

@Mapper
public interface IUsagePartDao {

    /**
     * 插入用法备件关联记录
     *
     * @param usagePartPO 用法备件关联对象
     * @return 影响行数
     */
    int insert(UsagePartPO usagePartPO);

    /**
     * 批量插入用法备件关联记录
     *
     * @param usagePartPOList 用法备件关联对象列表
     * @return 影响行数
     */
    int batchInsert(List<UsagePartPO> usagePartPOList);

    /**
     * 根据用法ID和备件ID删除关联
     *
     * @param usageId 用法ID
     * @param partId  备件ID
     * @return 影响行数
     */
    int deleteByUsageIdAndPartId(@Param("usageId") Long usageId, @Param("partId") Long partId);

    /**
     * 根据用法ID删除所有关联
     *
     * @param usageId 用法ID
     * @return 影响行数
     */
    int deleteByUsageId(Long usageId);

    /**
     * 根据备件ID删除所有关联
     *
     * @param partId 备件ID
     * @return 影响行数
     */
    int deleteByPartId(Long partId);

    /**
     * 更新用法备件关联数量
     *
     * @param usagePartPO 用法备件关联对象
     * @return 影响行数
     */
    int updateCount(UsagePartPO usagePartPO);

    /**
     * 根据用法ID查询所有关联的备件
     *
     * @param usageId 用法ID
     * @return 用法备件关联列表
     */
    List<UsagePartPO> selectByUsageId(Long usageId);

    /**
     * 根据备件ID查询所有关联的用法
     *
     * @param partId 备件ID
     * @return 用法备件关联列表
     */
    List<UsagePartPO> selectByPartId(Long partId);

    /**
     * 根据用法ID和备件ID查询关联
     *
     * @param usageId 用法ID
     * @param partId  备件ID
     * @return 用法备件关联对象
     */
    UsagePartPO selectByUsageIdAndPartId(@Param("usageId") Long usageId, @Param("partId") Long partId);

    /**
     * 检查用法和备件是否已关联
     *
     * @param usageId 用法ID
     * @param partId  备件ID
     * @return 是否存在关联
     */
    boolean exists(@Param("usageId") Long usageId, @Param("partId") Long partId);

}
