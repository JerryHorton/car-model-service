package cn.cug.sxy.domain.usage.service;

import cn.cug.sxy.domain.usage.model.entity.ConfigCategoryEntity;
import cn.cug.sxy.domain.usage.model.entity.ConfigItemEntity;
import cn.cug.sxy.domain.usage.model.valobj.ConfigCategoryId;
import cn.cug.sxy.domain.usage.model.valobj.ConfigItemId;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/6 11:01
 * @Description 配置管理服务接口
 * @Author jerryhotton
 */

public interface IConfigManagementService {

    /**
     * 创建配置类别
     *
     * @param categoryCode 类别编码
     * @param categoryName 类别名称
     * @param sortOrder    排序序号
     * @param creator      创建人
     * @return 创建的配置类别实体
     */
    ConfigCategoryEntity createCategory(String categoryCode, String categoryName, Integer sortOrder, String creator);

    /**
     * 更新配置类别
     *
     * @param categoryId   配置类别ID
     * @param categoryCode 新的类别编码
     * @param categoryName 新的类别名称
     * @param sortOrder    新的排序序号
     * @return 更新后的配置类别实体
     */
    ConfigCategoryEntity updateCategory(ConfigCategoryId categoryId, String categoryCode, String categoryName, Integer sortOrder);

    /**
     * 删除配置类别
     *
     * @param categoryId 配置类别ID
     * @return 是否删除成功
     */
    boolean deleteCategory(ConfigCategoryId categoryId);

    /**
     * 创建配置项
     *
     * @param categoryId 配置类别ID
     * @param itemCode   配置项编码
     * @param itemName   配置项名称
     * @param itemValue  配置值
     * @param creator    创建人
     * @return 创建的配置项实体
     */
    ConfigItemEntity createConfigItem(ConfigCategoryId categoryId, String itemCode,
                                      String itemName, String itemValue, String creator);

    /**
     * 更新配置项
     *
     * @param itemId    配置项ID
     * @param itemName  新的配置项名称
     * @param itemValue 新的配置值
     * @return 更新后的配置项实体
     */
    ConfigItemEntity updateConfigItem(ConfigItemId itemId, String itemName, String itemValue);

    /**
     * 删除配置项
     *
     * @param itemId 配置项ID
     * @return 是否删除成功
     */
    boolean deleteConfigItem(ConfigItemId itemId);

    /**
     * 更新配置类别状态
     *
     * @param categoryId 配置类别ID
     * @param enabled    是否启用
     * @return 更新后的配置类别实体
     */
    ConfigCategoryEntity updateCategoryStatus(ConfigCategoryId categoryId, boolean enabled);

    /**
     * 更新配置项状态
     *
     * @param itemId  配置项ID
     * @param enabled 是否启用
     * @return 更新后的配置项实体
     */
    ConfigItemEntity updateConfigItemStatus(ConfigItemId itemId, boolean enabled);

    /**
     * 验证配置项是否存在且可用
     *
     * @param itemIds 配置项ID列表
     * @throws RuntimeException 当配置项不存在或不可用时
     */
    void validateConfigItems(List<ConfigItemId> itemIds);

    /**
     * 检查配置项是否属于同一类别
     * <p>
     * 用于验证配置组合的合法性（同一类别下不能有多个配置项）
     *
     * @param itemIds 配置项ID列表
     * @return 是否存在同类别冲突
     */
    boolean hasConflictingCategories(List<ConfigItemId> itemIds);

}
