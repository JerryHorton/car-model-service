package cn.cug.sxy.domain.usage.adapter.repository;

import cn.cug.sxy.domain.usage.model.entity.ConfigCategoryEntity;
import cn.cug.sxy.domain.usage.model.valobj.ConfigCategoryId;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/8/6 09:07
 * @Description 配置类别仓储接口
 * @Author jerryhotton
 */

public interface IConfigCategoryRepository {

    /**
     * 保存配置类别
     * <p>
     * 如果实体没有ID，则执行插入操作
     * 如果实体有ID，则执行更新操作
     *
     * @param configCategory 配置类别实体
     * @return 保存后的配置类别实体（包含生成的ID）
     */
    ConfigCategoryEntity save(ConfigCategoryEntity configCategory);

    /**
     * 根据ID查找配置类别
     *
     * @param categoryId 配置类别ID
     * @return 配置类别实体（可能为空）
     */
    Optional<ConfigCategoryEntity> findById(ConfigCategoryId categoryId);

    /**
     * 根据类别编码查找配置类别
     * <p>
     * 类别编码是业务唯一标识，常用于业务查询
     *
     * @param categoryCode 类别编码
     * @return 配置类别实体（可能为空）
     */
    Optional<ConfigCategoryEntity> findByCode(String categoryCode);

    /**
     * 查找所有可用的配置类别
     * <p>
     * 返回状态为ENABLED的配置类别，按排序序号排序
     * 用于前端下拉选择等场景
     *
     * @return 可用的配置类别列表
     */
    List<ConfigCategoryEntity> findAllEnabled();

    /**
     * 根据类别编码和名称模糊查找配置类别
     * <p>
     * 支持配置管理人员通过类别编码和名称搜索类别
     *
     * @param categoryCode 类别编码
     * @param nameKeyword  名称关键字（支持模糊匹配）
     * @return 匹配的配置类别列表
     */
    List<ConfigCategoryEntity> findByCategoryCodeAndNameLike(String categoryCode, String nameKeyword);

    /**
     * 检查类别编码是否已存在
     * <p>
     * 用于创建配置类别时的唯一性校验
     *
     * @param categoryCode 类别编码
     * @return 是否存在
     */
    boolean existsByCode(String categoryCode);

    /**
     * 删除配置类别
     * <p>
     * 注意：删除类别前应确保没有关联的配置项
     *
     * @param categoryId 配置类别ID
     * @return 是否删除成功
     */
    boolean deleteById(ConfigCategoryId categoryId);

}
