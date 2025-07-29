package cn.cug.sxy.domain.system.service;

import cn.cug.sxy.domain.system.model.entity.SystemCategoryEntity;
import cn.cug.sxy.domain.system.model.valobj.CategoryCode;
import cn.cug.sxy.domain.system.model.valobj.CategoryId;
import cn.cug.sxy.domain.system.model.valobj.CategoryStatus;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/28 09:13
 * @Description 系统大类服务接口
 * @Author jerryhotton
 */

public interface ISystemCategoryService {

    /**
     * 创建系统大类
     *
     * @param categoryCode   大类编码
     * @param categoryName   大类名称
     * @param categoryNameEn 大类英文名称
     * @param sortOrder      排序顺序
     * @param creator        创建人
     * @return 系统大类实体
     * @throws IllegalArgumentException 如果大类编码已存在
     */
    SystemCategoryEntity createCategory(CategoryCode categoryCode, String categoryName, String categoryNameEn, Integer sortOrder, String creator);

    /**
     * 更新系统大类信息
     *
     * @param categoryId     大类ID
     * @param categoryCode   大类编码
     * @param categoryName   大类名称
     * @param categoryNameEn 大类英文名称
     * @param sortOrder      排序顺序
     * @return 更新结果
     */
    boolean updateCategory(CategoryId categoryId, CategoryCode categoryCode, String categoryName, String categoryNameEn, Integer sortOrder);

    /**
     * 更新系统大类状态
     *
     * @param categoryId 大类编码
     * @param status     状态
     * @return 更新结果
     */
    boolean updateStatus(CategoryId categoryId, CategoryStatus status);

    /**
     * 根据大类ID查询系统大类
     *
     * @param categoryId 大类ID
     * @return 系统大类实体，如果不存在则返回空
     */
    SystemCategoryEntity findById(CategoryId categoryId);

    /**
     * 根据大类编码查询系统大类
     *
     * @param categoryCode 大类编码
     * @return 系统大类实体，如果不存在则返回空
     */
    SystemCategoryEntity findByCode(CategoryCode categoryCode);

    /**
     * 查询所有系统大类
     *
     * @return 系统大类实体列表
     */
    List<SystemCategoryEntity> findAll();

    /**
     * 根据状态查询系统大类
     *
     * @param status 状态
     * @return 系统大类实体列表
     */
    List<SystemCategoryEntity> findByStatus(CategoryStatus status);

    /**
     * 根据名称模糊查询系统大类
     *
     * @param categoryName 大类名称（部分匹配）
     * @return 系统大类实体列表
     */
    List<SystemCategoryEntity> findByNameLike(String categoryName);

    /**
     * 检查大类编码是否已存在
     *
     * @param categoryCode 大类编码
     * @return 是否已存在
     */
    boolean isCodeExists(CategoryCode categoryCode);

}
