package cn.cug.sxy.domain.system.adapter.repository;

import cn.cug.sxy.domain.system.model.entity.SystemGroupEntity;
import cn.cug.sxy.domain.system.model.valobj.CategoryId;
import cn.cug.sxy.domain.system.model.valobj.GroupCode;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/28 08:53
 * @Description 系统分组仓储接口
 * @Author jerryhotton
 */

public interface ISystemGroupRepository {

    /**
     * 保存系统分组
     *
     * @param systemGroupEntity 系统分组实体
     */
    void save(SystemGroupEntity systemGroupEntity);

    /**
     * 更新系统分组
     *
     * @param systemGroupEntity 系统分组实体
     * @return 更新的记录数
     */
    int update(SystemGroupEntity systemGroupEntity);

    /**
     * 根据分组编码查询系统分组
     *
     * @param groupCode 分组编码
     * @return 系统分组实体，如果不存在则返回空
     */
    Optional<SystemGroupEntity> findByGroupCode(GroupCode groupCode);

    /**
     * 根据系统大类编码查询系统分组列表
     *
     * @param categoryId 系统大类编码
     * @return 系统分组实体列表
     */
    List<SystemGroupEntity> findByCategoryId(CategoryId categoryId);

    /**
     * 查询所有系统分组
     *
     * @return 系统分组实体列表
     */
    List<SystemGroupEntity> findAll();

    /**
     * 根据状态查询系统分组
     *
     * @param status 状态编码
     * @return 系统分组实体列表
     */
    List<SystemGroupEntity> findByStatus(String status);

    /**
     * 根据名称模糊查询系统分组
     *
     * @param groupName 分组名称（部分匹配）
     * @return 系统分组实体列表
     */
    List<SystemGroupEntity> findByGroupNameLike(String groupName);

    /**
     * 检查分组编码是否已存在
     *
     * @param groupCode 分组编码
     * @return 是否已存在
     */
    boolean existsByGroupCode(GroupCode groupCode);

}
