package cn.cug.sxy.api;

import cn.cug.sxy.api.dto.SystemCategoryCreateRequestDTO;
import cn.cug.sxy.api.dto.SystemCategoryUpdateRequestDTO;
import cn.cug.sxy.api.dto.SystemGroupCreateRequestDTO;
import cn.cug.sxy.api.dto.SystemGroupUpdateRequestDTO;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.SystemCategoryVO;
import cn.cug.sxy.api.vo.SystemGroupVO;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/9/10 09:39
 * @Description 汽车系统服务接口
 * @Author jerryhotton
 */

public interface ISystemService {

    /**
     * 创建系统大类
     *
     * @param requestDTO 创建请求DTO
     * @return 创建结果
     */
    Response<SystemCategoryVO> createCategory(SystemCategoryCreateRequestDTO requestDTO);

    /**
     * 更新系统大类
     *
     * @param requestDTO 更新请求DTO
     * @return 更新结果
     */
    Response<SystemCategoryVO> updateCategory(SystemCategoryUpdateRequestDTO requestDTO);

    /**
     * 获取所有系统大类
     *
     * @return 系统大类列表
     */
    Response<List<SystemCategoryVO>> getAllCategories();

    /**
     * 创建系统分组
     *
     * @param requestDTO 创建请求DTO
     * @return 创建结果
     */
    Response<SystemGroupVO> createGroup(SystemGroupCreateRequestDTO requestDTO);

    /**
     * 更新系统分组
     *
     * @param requestDTO 更新请求DTO
     * @return 更新结果
     */
    Response<SystemGroupVO> updateGroup(SystemGroupUpdateRequestDTO requestDTO);

    /**
     * 根据大类ID获取系统分组
     *
     * @param categoryId 大类ID
     * @return 系统分组列表
     */
    Response<List<SystemGroupVO>> getGroupsByCategoryId(Long categoryId);

    /**
     * 删除系统大类
     *
     * @param categoryId 大类ID
     * @return 删除结果
     */
    Response<Boolean> deleteCategory(Long categoryId);

    /**
     * 删除系统分组
     *
     * @param groupId   分组ID
     * @return 删除结果
     */
    Response<Boolean> deleteGroup(Long groupId);

}
