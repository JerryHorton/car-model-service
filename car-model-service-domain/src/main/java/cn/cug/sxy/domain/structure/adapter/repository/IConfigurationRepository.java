package cn.cug.sxy.domain.structure.adapter.repository;

import cn.cug.sxy.domain.structure.model.entity.ConfigurationEntity;
import cn.cug.sxy.domain.structure.model.valobj.ConfigurationCode;
import cn.cug.sxy.domain.structure.model.valobj.ConfigurationId;
import cn.cug.sxy.domain.structure.model.valobj.Status;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/8/4 16:12
 * @Description 车型配置仓储接口
 * @Author jerryhotton
 */

public interface IConfigurationRepository {

    /**
     * 保存配置
     *
     * @param configuration 配置实体
     */
    void save(ConfigurationEntity configuration);

    /**
     * 批量保存配置
     *
     * @param configurations 配置实体列表
     * @return 保存的记录数
     */
    int saveBatch(List<ConfigurationEntity> configurations);

    /**
     * 更新配置
     *
     * @param configuration 配置实体
     * @return 更新的记录数
     */
    int update(ConfigurationEntity configuration);

    /**
     * 更新配置状态
     *
     * @param configId 配置ID
     * @param status   状态
     * @return 更新的记录数
     */
    int updateStatus(ConfigurationId configId, Status status);

    /**
     * 根据ID查询配置
     *
     * @param configId 配置ID
     * @return 配置实体
     */
    Optional<ConfigurationEntity> findById(ConfigurationId configId);

    /**
     * 根据配置编码查询配置
     *
     * @param configCode 配置编码
     * @return 配置实体
     */
    Optional<ConfigurationEntity> findByCode(ConfigurationCode configCode);

    /**
     * 根据配置名称模糊查询
     *
     * @param nameKeyword 名称关键字
     * @return 配置实体列表
     */
    List<ConfigurationEntity> findByNameLike(String nameKeyword);

    /**
     * 根据状态查询配置
     *
     * @param status 状态
     * @return 配置实体列表
     */
    List<ConfigurationEntity> findByStatus(Status status);

    /**
     * 查询所有可用配置
     *
     * @return 配置实体列表
     */
    List<ConfigurationEntity> findAllEnabled();

    /**
     * 检查配置编码是否存在
     *
     * @param configCode 配置编码
     * @return 是否存在
     */
    boolean existsByCode(ConfigurationCode configCode);

    /**
     * 检查配置名称是否存在
     *
     * @param configName 配置名称
     * @return 是否存在
     */
    boolean existsByName(String configName);

    /**
     * 删除配置
     *
     * @param configId 配置ID
     * @return 删除的记录数
     */
    int deleteById(ConfigurationId configId);

    /**
     * 统计配置总数
     *
     * @return 配置总数
     */
    int count();

    /**
     * 统计指定状态的配置数量
     *
     * @param status 状态
     * @return 配置数量
     */
    int countByStatus(Status status);

    /**
     * 分页查询配置
     *
     * @param configCode 配置编码
     * @param configName 配置名称
     * @param status     状态
     * @return 配置实体列表
     */
    List<ConfigurationEntity> findWithPagination(ConfigurationCode configCode, String configName, Status status);

}
