package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.GroupUsagePO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/4 16:22
 * @Description 系统分组用法数据访问接口
 * @Author jerryhotton
 */

@Mapper
public interface IGroupUsageDao {

    /**
     * 插入用法
     *
     * @param usage 用法PO
     * @return 影响行数
     */
    int insert(GroupUsagePO usage);

    /**
     * 批量插入用法
     *
     * @param usages 用法PO列表
     * @return 影响行数
     */
    int insertBatch(List<GroupUsagePO> usages);

    /**
     * 更新用法
     *
     * @param usage 用法PO
     * @return 影响行数
     */
    int update(GroupUsagePO usage);

    /**
     * 更新用法状态
     *
     * @param usage 用法PO
     * @return 影响行数
     */
    int updateStatus(GroupUsagePO usage);

    /**
     * 根据ID查询用法
     *
     * @param id 用法ID
     * @return 用法PO
     */
    GroupUsagePO selectById(Long id);

    /**
     * 根据分组ID查询所有用法
     *
     * @param groupId 分组ID
     * @return 用法PO列表
     */
    List<GroupUsagePO> selectByGroupId(Long groupId);

    /**
     * 根据分组ID和状态查询用法
     *
     * @param usage 用法PO
     * @return 用法PO列表
     */
    List<GroupUsagePO> selectByGroupIdAndStatus(GroupUsagePO usage);

    /**
     * 根据用法名称模糊查询
     *
     * @param nameKeyword 名称关键字
     * @return 用法PO列表
     */
    List<GroupUsagePO> selectByNameLike(String nameKeyword);

    /**
     * 根据状态查询用法
     *
     * @param status 状态
     * @return 用法PO列表
     */
    List<GroupUsagePO> selectByStatus(String status);

    /**
     * 检查分组下是否存在指定的用法ID
     *
     * @param usage 用法PO
     * @return 存在的记录数
     */
    int countByGroupIdAndUsageId(GroupUsagePO usage);

    /**
     * 检查分组下是否存在指定的用法名称
     *
     * @param usage 用法PO
     * @return 存在的记录数
     */
    int countByGroupIdAndUsageName(GroupUsagePO usage);

    /**
     * 删除用法
     *
     * @param id 用法ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据分组ID删除所有用法
     *
     * @param groupId 分组ID
     * @return 影响行数
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
     * @param usage 用法PO
     * @return 用法数量
     */
    int countByGroupIdAndStatus(GroupUsagePO usage);

}
