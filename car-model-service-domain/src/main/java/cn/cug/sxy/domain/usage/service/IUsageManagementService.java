package cn.cug.sxy.domain.usage.service;

import cn.cug.sxy.domain.usage.model.aggregate.UsageAggregate;
import cn.cug.sxy.domain.usage.model.entity.UsageConfigCombinationEntity;
import cn.cug.sxy.domain.usage.model.valobj.UsageCreationAggregate;
import cn.cug.sxy.domain.usage.model.entity.UsageEntity;
import cn.cug.sxy.domain.usage.model.valobj.UsageConfigCombinationSpec;
import cn.cug.sxy.domain.usage.model.valobj.UsageId;
import org.springframework.web.multipart.MultipartFile;
import cn.cug.sxy.types.exception.AppException;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/6 11:05
 * @Description 用法管理服务接口
 * @Author jerryhotton
 */

public interface IUsageManagementService {

    /**
     * 创建用法及其配置组合
     * <p>
     * 这是一个完整的业务操作，创建用法的同时设置其配置组合
     *
     * @param instanceId        实例ID
     * @param parentGroupNodeId 父GROUP节点ID
     * @param groupId           归属系统分组ID
     * @param usageName         用法名称
     * @param explodedViewFile  爆炸图
     * @param creator           创建人
     * @param combinationSpecs  配置组合列表
     * @return 用法聚合对象
     * @throws IllegalArgumentException 当参数无效时
     * @throws AppException             当配置项不存在或业务规则违反时
     */
    UsageCreationAggregate createUsage(Long instanceId, Long parentGroupNodeId, Long groupId,
                                       String usageName, MultipartFile explodedViewFile, Integer sortOrder,
                                       String creator, List<UsageConfigCombinationSpec> combinationSpecs);

    /**
     * 更新用法及其配置组合
     * <p>
     * 更新用法基本信息，同时可以调整配置组合
     * 这是一个原子操作，要么全部成功，要么全部失败
     *
     * @param usageId          用法ID
     * @param usageName        新的用法名称
     * @param explodedViewFile 新的爆炸图文件（可选，null表示不更新爆炸图）
     * @param groupId          系统分组ID（用于生成文件路径）
     * @param combinationSpecs 新的配置组合列表（null表示不更新配置组合）
     * @return 更新后的用法聚合对象
     * @throws IllegalArgumentException 当参数无效时
     * @throws AppException             当用法不存在或配置项不存在时
     */
    UsageAggregate updateUsage(UsageId usageId, String usageName,
                               MultipartFile explodedViewFile, Long groupId,
                               List<UsageConfigCombinationSpec> combinationSpecs);

    /**
     * 查询某个组下的全部可用用法
     * <p>
     * 根据组类型实例节点ID，查询该组下所有可用的用法的基本信息
     * 不包含配置组合详情，用于组级别的用法列表展示
     *
     * @param groupNodeId 组类型实例节点ID
     * @return 该组下的用法基本信息列表
     * @throws IllegalArgumentException 当组节点ID无效时
     * @throws AppException             当组节点不存在或不是GROUP类型时
     */
    List<UsageEntity> findUsagesByGroup(Long groupNodeId);

    /**
     * 查询某个组下的全部用法
     * <p>
     * 根据组类型实例节点ID，查询该组下所有用法的基本信息，包括逻辑删除状态下的
     * 不包含配置组合详情，用于组级别的用法列表展示
     *
     * @param groupNodeId 组类型实例节点ID
     * @return 该组下的用法基本信息列表
     * @throws IllegalArgumentException 当组节点ID无效时
     * @throws AppException             当组节点不存在或不是GROUP类型时
     */
    List<UsageEntity> findAllUsagesByGroup(Long groupNodeId);

    /**
     * 查询用法详情
     * <p>
     * 根据用法ID查询用法的详细信息，包含配置组合
     * 用于用法详情页面展示
     *
     * @param usageId 用法ID
     * @return 用法详情（可能为空）
     */
    List<UsageConfigCombinationEntity> findUsageDetail(UsageId usageId);

    /**
     * 更新用法状态
     * <p>
     * 启用或禁用用法
     *
     * @param usageId 用法ID
     * @param enabled 是否启用
     * @return 更新后的用法实体
     */
    UsageEntity updateUsageStatus(UsageId usageId, boolean enabled);

    /**
     * 删除用法
     * <p>
     * 删除用法及其所有相关数据（配置组合、实例节点等）
     * 这是一个级联删除操作
     *
     * @param usageId 用法ID
     * @return 是否删除成功
     * @throws IllegalArgumentException 当参数无效时
     */
    boolean deleteUsage(UsageId usageId);

    /**
     * 恢复用法
     * <p>
     * 将已删除（DISABLED状态）的用法恢复为可用状态
     *
     * @param usageId 用法ID
     * @return 是否恢复成功
     * @throws IllegalArgumentException 当参数无效时
     */
    boolean restoreUsage(UsageId usageId);

    /**
     * 获取用法的配置组合描述
     * <p>
     * 生成用法配置逻辑的可读描述
     * 例如："电池包容量：39kWh || (电池包容量：43kWh && 后电机型号：Tz220XSP01)"
     *
     * @param usageId 用法ID
     * @return 配置逻辑描述
     */
    String getUsageConfigDescription(UsageId usageId);

    /**
     * 验证用法配置组合的有效性
     * <p>
     * 检查配置组合是否符合业务规则
     *
     * @param combinationSpecs 配置组合列表
     */
    void validateUsageCombinations(List<UsageConfigCombinationSpec> combinationSpecs);

}
