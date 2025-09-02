package cn.cug.sxy.domain.usage.adapter.repository;

import cn.cug.sxy.domain.usage.model.entity.ConfigItemEntity;
import cn.cug.sxy.domain.usage.model.valobj.ConfigCategoryId;
import cn.cug.sxy.domain.usage.model.valobj.ConfigItemId;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/8/6 09:21
 * @Description 配置项仓储接口
 * @Author jerryhotton
 */

public interface IConfigItemRepository {

    /**
     * 保存配置项
     * <p>
     * 如果实体没有ID，则执行插入操作
     * 如果实体有ID，则执行更新操作
     *
     * @param configItem 配置项实体
     * @return 保存后的配置项实体（包含生成的ID）
     */
    ConfigItemEntity save(ConfigItemEntity configItem);

    /**
     * 根据ID查找配置项
     *
     * @param itemId 配置项ID
     * @return 配置项实体（可能为空）
     */
    Optional<ConfigItemEntity> findById(ConfigItemId itemId);

    /**
     * 根据配置项编码查找配置项
     * <p>
     * 配置项编码是全局唯一标识
     *
     * @param itemCode 配置项编码
     * @return 配置项实体（可能为空）
     */
    Optional<ConfigItemEntity> findByCode(String itemCode);

    /**
     * 根据类别ID查找可用的配置项
     * <p>
     * 返回指定类别下状态为ENABLED的配置项
     * 这是最常用的查询，用于获取某类别下的所有可选配置
     *
     * @param categoryId 配置类别ID
     * @return 可用的配置项列表
     */
    List<ConfigItemEntity> findEnabledByCategoryId(ConfigCategoryId categoryId);

    /**
     * 根据配置值或名称模糊查找配置项
     * <p>
     * 支持用户通过配置值或名称搜索配置项
     * 例如：搜索"增程"可以找到"动力类型：增程式"
     *
     * @param keyword 配置值或名称关键字（支持模糊匹配）
     * @return 匹配的配置项列表
     */
    List<ConfigItemEntity> findByValueLikeOrNameLike(String keyword);

    /**
     * 根据多个ID批量查找配置项
     * <p>
     * 用于获取用法配置组合中的所有配置项详情
     * 避免N+1查询问题，提高性能
     *
     * @param itemIds 配置项ID列表
     * @return 配置项列表
     */
    List<ConfigItemEntity> findByIds(List<ConfigItemId> itemIds);

    /**
     * 检查配置项编码是否已存在
     * <p>
     * 用于创建配置项时的唯一性校验
     *
     * @param itemCode 配置项编码
     * @return 是否存在
     */
    boolean existsByCode(String itemCode);

    /**
     * 删除配置项
     * <p>
     * 注意：删除配置项前应确保没有被用法配置组合引用
     *
     * @param itemId 配置项ID
     * @return 是否删除成功
     */
    boolean deleteById(ConfigItemId itemId);

    /**
     * 根据类别ID删除所有配置项
     * <p>
     * 级联删除操作，删除类别时使用
     *
     * @param categoryId 配置类别ID
     * @return 删除的配置项数量
     */
    int deleteByCategoryId(ConfigCategoryId categoryId);

}
