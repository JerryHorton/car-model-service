package cn.cug.sxy.api;

import cn.cug.sxy.api.dto.ConfigCategoryCreateRequestDTO;
import cn.cug.sxy.api.dto.ConfigCategoryUpdateRequestDTO;
import cn.cug.sxy.api.dto.ConfigItemCreateRequestDTO;
import cn.cug.sxy.api.dto.SearchConfigCategoryRequestDTO;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.ConfigCategoryVO;
import cn.cug.sxy.api.vo.ConfigItemVO;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/6 19:25
 * @Description 配置服务接口
 * @Author jerryhotton
 */

public interface IConfigService {

    /**
     * 创建配置类别
     *
     * @param requestDTO 创建请求DTO
     * @return 配置类别VO
     */
    Response<ConfigCategoryVO> createCategory(ConfigCategoryCreateRequestDTO requestDTO);

    /**
     * 更新配置类别
     *
     * @param requestDTO 更新请求DTO
     * @return 配置类别VO
     */
    Response<ConfigCategoryVO> updateCategory(ConfigCategoryUpdateRequestDTO requestDTO);

    /**
     * 获取所有配置类别
     *
     * @return 配置类别VO列表
     */
    Response<List<ConfigCategoryVO>> getCategories();

    /**
     * 搜索配置类别
     *
     * @param requestDTO 搜索请求DTO
     * @return 配置类别VO列表
     */
    Response<List<ConfigCategoryVO>> searchCategories(SearchConfigCategoryRequestDTO requestDTO);

    /**
     * 删除配置类别
     *
     * @param categoryId 配置类别ID
     * @return 是否删除成功
     */
    Response<Boolean> deleteCategory(Long categoryId);

    /**
     * 创建配置项
     *
     * @param requestDTO 创建请求DTO
     * @return 配置项VO
     */
    Response<ConfigItemVO> createConfigItem(ConfigItemCreateRequestDTO requestDTO);

    /**
     * 获取配置项
     *
     * @param categoryId 配置类别ID
     * @return 配置项VO列表
     */
    Response<List<ConfigItemVO>> getConfigItems(Long categoryId);

    /**
     * 搜索配置项
     *
     * @param keyword 搜索关键词
     * @return 配置项VO列表
     */
    Response<List<ConfigItemVO>> searchConfigItems(String keyword);

    /**
     * 删除配置项
     *
     * @param itemId 配置项ID
     * @return 是否删除成功
     */
    Response<Boolean> deleteConfigItem(Long itemId);

}
