package cn.cug.sxy.infrastructure.dao;

import cn.cug.sxy.infrastructure.dao.po.SystemGroupPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/28 08:58
 * @Description 系统分组DAO接口
 * @Author jerryhotton
 */

@Mapper
public interface ISystemGroupDao {

    /**
     * 插入系统分组
     *
     * @param systemGroupPO 系统分组PO
     * @return 影响行数
     */
    int insert(SystemGroupPO systemGroupPO);

    /**
     * 更新系统分组
     *
     * @param systemGroupPO 系统分组PO
     * @return 影响行数
     */
    int update(SystemGroupPO systemGroupPO);

    /**
     * 根据ID查询系统分组
     *
     * @param id 分组ID
     * @return 系统分组PO
     */
    SystemGroupPO selectById(Long id);

    /**
     * 根据分组编码查询系统分组
     *
     * @param groupCode 分组编码
     * @return 系统分组PO
     */
    SystemGroupPO selectByGroupCode(String groupCode);

    /**
     * 根据系统大类编码查询系统分组列表
     *
     * @param categoryId 系统大类编码
     * @return 系统分组PO列表
     */
    List<SystemGroupPO> selectByCategoryId(Long categoryId);

    /**
     * 查询所有系统分组
     *
     * @return 系统分组PO列表
     */
    List<SystemGroupPO> selectAll();

    /**
     * 根据状态查询系统分组
     *
     * @param status 状态编码
     * @return 系统分组PO列表
     */
    List<SystemGroupPO> selectByStatus(String status);

    /**
     * 根据名称模糊查询系统分组
     *
     * @param groupNameLike 分组名称（模糊匹配）
     * @return 系统分组PO列表
     */
    List<SystemGroupPO> selectByGroupNameLike(String groupNameLike);

    /**
     * 统计指定分组编码的记录数
     *
     * @param groupCode 分组编码
     * @return 记录数
     */
    int countByGroupCode(String groupCode);

    /**
     * 根据ID物理删除系统分组
     *
     * @param id 分组ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据ID逻辑删除系统分组（更新状态为DELETED）
     *
     * @param id 分组ID
     * @return 影响行数
     */
    int logicalDeleteById(Long id);

    /**
     * 根据大类ID物理删除系统分组
     *
     * @param categoryId 大类ID
     * @return 影响行数
     */
    int deleteByCategoryId(Long categoryId);

    /**
     * 根据大类ID逻辑删除系统分组（更新状态为DELETED）
     *
     * @param categoryId 大类ID
     * @return 影响行数
     */
    int logicalDeleteByCategoryId(Long categoryId);

}
