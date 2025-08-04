package cn.cug.sxy.domain.structure.adapter.repository;

import cn.cug.sxy.domain.structure.model.entity.GroupUsageEntity;
import cn.cug.sxy.domain.structure.model.valobj.Status;
import cn.cug.sxy.domain.structure.model.valobj.UsageId;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/8/4 15:58
 * @Description 系统分组用法仓储接口
 * @Author jerryhotton
 */

public interface IGroupUsageRepository {

    /**
     * 保存用法
     *
     * @param usage 用法实体
     */
    void save(GroupUsageEntity usage);

    /**
     * 批量保存用法
     *
     * @param usages 用法实体列表
     * @return 保存的记录数
     */
    int saveBatch(List<GroupUsageEntity> usages);

    /**
     * 更新用法
     *
     * @param usage 用法实体
     * @return 更新的记录数
     */
    int update(GroupUsageEntity usage);

    /**
     * 更新用法状态
     *
     * @param usageId 用法ID
     * @param status  状态
     * @return 更新的记录数
     */
    int updateStatus(UsageId usageId, Status status);

    /**
     * 根据ID查询用法
     *
     * @param usageId 用法ID
     * @return 用法实体
     */
    Optional<GroupUsageEntity> findById(UsageId usageId);

    /**
     * 根据分组ID查询所有用法
     *
     * @param groupId 分组ID
     * @return 用法实体列表
     */
    List<GroupUsageEntity> findByGroupId(Long groupId);

    /**
     * 根据分组ID和状态查询用法
     *
     * @param groupId 分组ID
     * @param status  状态
     * @return 用法实体列表
     */
    List<GroupUsageEntity> findByGroupIdAndStatus(Long groupId, Status status);

    /**
     * 根据用法名称模糊查询
     *
     * @param nameKeyword 名称关键字
     * @return 用法实体列表
     */
    List<GroupUsageEntity> findByNameLike(String nameKeyword);

    /**
     * 根据状态查询用法
     *
     * @param status 状态
     * @return 用法实体列表
     */
    List<GroupUsageEntity> findByStatus(Status status);

    /**
     * 检查分组下是否存在指定的用法ID
     *
     * @param groupId 分组ID
     * @param usageId 用法ID（业务ID）
     * @return 是否存在
     */
    boolean existsByGroupIdAndUsageId(Long groupId, Long usageId);

    /**
     * 检查分组下是否存在指定的用法名称
     *
     * @param groupId   分组ID
     * @param usageName 用法名称
     * @return 是否存在
     */
    boolean existsByGroupIdAndUsageName(Long groupId, String usageName);

    /**
     * 删除用法
     *
     * @param usageId 用法ID
     * @return 删除的记录数
     */
    int deleteById(UsageId usageId);

    /**
     * 根据分组ID删除所有用法
     *
     * @param groupId 分组ID
     * @return 删除的记录数
     */
    int deleteByGroupId(Long groupId);

    /**
     * 统计分组下的用法数量
     *
     * @param groupId 分组ID
     * @return 用法数量
     */
    int countByGroupId(Long groupId);

    /**
     * 统计分组下指定状态的用法数量
     *
     * @param groupId 分组ID
     * @param status  状态
     * @return 用法数量
     */
    int countByGroupIdAndStatus(Long groupId, Status status);

}
