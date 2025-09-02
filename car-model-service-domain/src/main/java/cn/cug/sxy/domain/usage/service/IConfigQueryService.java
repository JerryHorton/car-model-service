package cn.cug.sxy.domain.usage.service;

import cn.cug.sxy.domain.usage.model.entity.ConfigCategoryEntity;
import cn.cug.sxy.domain.usage.model.entity.ConfigItemEntity;
import cn.cug.sxy.domain.usage.model.valobj.ConfigCategoryId;
import cn.cug.sxy.domain.usage.model.valobj.ConfigItemId;

import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @Date 2025/8/6 11:03
 * @Description 配置数据查询服务接口
 * @Author jerryhotton
 */

public interface IConfigQueryService {

    /**
     * 获取所有可用的配置数据
     * <p>
     * 返回按类别分组的配置项，用于前端配置选择
     *
     * @return 按类别分组的配置项Map，key为类别，value为该类别下的配置项列表
     */
    Map<ConfigCategoryEntity, List<ConfigItemEntity>> getAllAvailableConfigs();

    /**
     * 根据ID列表批量查询配置项
     * <p>
     * 用于验证用法配置组合中的配置项
     *
     * @param itemIds 配置项ID列表
     * @return 配置项列表
     */
    List<ConfigItemEntity> findConfigItemsByIds(List<ConfigItemId> itemIds);

    /**
     * 获取配置项的详细信息
     * <p>
     * 用于生成配置描述
     *
     * @param itemId 配置项ID
     * @return 配置项实体（可能为空）
     */
    ConfigItemEntity findConfigItemById(ConfigItemId itemId);

    /**
     * 查询所有可用的配置类别
     *
     * @return 可用的配置类别列表
     */
    List<ConfigCategoryEntity> findAllEnabledCategories();

    /**
     * 搜索配置类别
     *
     * @param categoryCode 类别编码
     * @param nameKeyword  名称关键字
     * @return 匹配的配置类别列表
     */
    List<ConfigCategoryEntity> searchCategories(String categoryCode, String nameKeyword);

    /**
     * 根据类别查询可用的配置项
     *
     * @param categoryId 配置类别ID
     * @return 可用的配置项列表
     */
    List<ConfigItemEntity> findEnabledItemsByCategoryId(ConfigCategoryId categoryId);

    /**
     * 根据配置值搜索配置项
     *
     * @param keyword 配置值关键字
     * @return 匹配的配置项列表
     */
    List<ConfigItemEntity> searchItemsByKeyword(String keyword);

}
