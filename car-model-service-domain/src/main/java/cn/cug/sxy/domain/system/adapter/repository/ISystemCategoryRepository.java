package cn.cug.sxy.domain.system.adapter.repository;

import cn.cug.sxy.domain.system.model.entity.SystemCategoryEntity;
import cn.cug.sxy.domain.system.model.valobj.CategoryCode;
import cn.cug.sxy.domain.system.model.valobj.CategoryId;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/28 08:54
 * @Description 系统大类仓储接口
 * @Author jerryhotton
 */

public interface ISystemCategoryRepository {

    /**
     * 保存系统大类
     *
     * @param systemCategoryEntity 系统大类实体
     */
    void save(SystemCategoryEntity systemCategoryEntity);

    /**
     * 更新系统大类
     *
     * @param systemCategoryEntity 系统大类实体
     * @return 更新的记录数
     */
    int update(SystemCategoryEntity systemCategoryEntity);

    /**
     * 根据大类ID查询系统大类
     *
     * @param categoryId 大类ID
     * @return 系统大类实体，如果不存在则返回空
     */
    Optional<SystemCategoryEntity> findById(CategoryId categoryId);

    /**
     * 根据大类编码查询系统大类
     *
     * @param categoryCode 大类编码
     * @return 系统大类实体，如果不存在则返回空
     */
    Optional<SystemCategoryEntity> findByCategoryCode(CategoryCode categoryCode);

    /**
     * 查询所有系统大类
     *
     * @return 系统大类实体列表
     */
    List<SystemCategoryEntity> findAll();

    /**
     * 根据状态查询系统大类
     *
     * @param status 状态编码
     * @return 系统大类实体列表
     */
    List<SystemCategoryEntity> findByStatus(String status);

    /**
     * 根据名称模糊查询系统大类
     *
     * @param categoryName 大类名称（部分匹配）
     * @return 系统大类实体列表
     */
    List<SystemCategoryEntity> findByCategoryNameLike(String categoryName);

    /**
     * 检查大类编码是否已存在
     *
     * @param categoryCode 大类编码
     * @return 是否已存在
     */
    boolean existsByCategoryCode(CategoryCode categoryCode);

}
