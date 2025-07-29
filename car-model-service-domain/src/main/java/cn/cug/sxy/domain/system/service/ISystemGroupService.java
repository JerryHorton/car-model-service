package cn.cug.sxy.domain.system.service;

import cn.cug.sxy.domain.system.model.entity.SystemGroupEntity;
import cn.cug.sxy.domain.system.model.valobj.CategoryId;
import cn.cug.sxy.domain.system.model.valobj.GroupCode;
import cn.cug.sxy.domain.system.model.valobj.GroupId;
import cn.cug.sxy.domain.system.model.valobj.GroupStatus;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/28 13:40
 * @Description 系统分组服务接口
 * @Author jerryhotton
 */

public interface ISystemGroupService {

    /**
     * 创建系统分组
     *
     * @param categoryId  大类ID
     * @param groupCode   分组编码
     * @param groupName   分组名称
     * @param groupNameEn 分组英文名称
     * @param sortOrder   排序顺序
     * @param creator     创建人
     * @return 系统分组实体
     * @throws IllegalArgumentException 如果分组编码已存在
     */
    SystemGroupEntity createGroup(CategoryId categoryId, GroupCode groupCode, String groupName, String groupNameEn, Integer sortOrder, String creator);

    /**
     * 更新系统分组信息
     *
     * @param groupId     分组ID
     * @param categoryId  大类ID
     * @param groupCode   分组编码
     * @param groupName   分组名称
     * @param groupNameEn 分组英文名称
     * @param sortOrder   排序顺序
     * @return 更新结果
     */
    boolean updateGroup(GroupId groupId, CategoryId categoryId, GroupCode groupCode, String groupName, String groupNameEn, Integer sortOrder);

    /**
     * 更新系统分组状态
     *
     * @param groupCode 分组编码
     * @param status    状态
     * @return 更新结果
     */
    boolean updateStatus(GroupCode groupCode, GroupStatus status);

    /**
     * 根据分组编码查询系统分组
     *
     * @param groupCode 分组编码
     * @return 系统分组实体，如果不存在则返回空
     */
    SystemGroupEntity findByCode(GroupCode groupCode);

    /**
     * 根据系统大类ID查询系统分组列表
     *
     * @param categoryId 系统大类ID
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
     * @param status 状态
     * @return 系统分组实体列表
     */
    List<SystemGroupEntity> findByStatus(GroupStatus status);

    /**
     * 根据名称模糊查询系统分组
     *
     * @param groupName 分组名称（部分匹配）
     * @return 系统分组实体列表
     */
    List<SystemGroupEntity> findByNameLike(String groupName);

    /**
     * 检查分组编码是否已存在
     *
     * @param groupCode 分组编码
     * @return 是否已存在
     */
    boolean isCodeExists(GroupCode groupCode);

}
