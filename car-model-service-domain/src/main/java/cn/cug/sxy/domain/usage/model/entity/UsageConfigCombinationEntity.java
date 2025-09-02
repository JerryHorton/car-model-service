package cn.cug.sxy.domain.usage.model.entity;

import cn.cug.sxy.domain.usage.model.valobj.ConfigItemId;
import cn.cug.sxy.domain.usage.model.valobj.UsageConfigCombinationId;
import cn.cug.sxy.domain.usage.model.valobj.UsageId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/5 19:45
 * @Description 用法配置组合实体
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsageConfigCombinationEntity {

    /**
     * 组合ID
     */
    private UsageConfigCombinationId id;
    /**
     * 用法ID
     */
    private UsageId usageId;
    /**
     * 组合名称
     * 例如："39kWh配置"、"43kWh+特定电机配置"
     */
    private String combinationName;
    /**
     * 排序序号
     * 用于控制组合的显示顺序
     */
    private Integer sortOrder;
    /**
     * 配置项ID列表
     * 组合内的所有配置项，它们之间是AND关系
     * 注意：这个字段在持久化时不直接存储，而是通过明细表管理
     */
    private List<ConfigItemId> configItemIds;
    /**
     * 配置项详细信息列表
     * 用于存储完整的配置项信息，不持久化到数据库
     */
    @Builder.Default
    private transient List<ConfigItemEntity> configItemDetails = new ArrayList<>();
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 创建用法配置组合
     *
     * @param usageId         用法ID
     * @param combinationName 组合名称
     * @param sortOrder       排序序号
     * @param configItemIds   配置项ID列表
     * @return 用法配置组合实体
     */
    public static UsageConfigCombinationEntity create(UsageId usageId, String combinationName,
                                                      Integer sortOrder, List<ConfigItemId> configItemIds) {
        return UsageConfigCombinationEntity.builder()
                .usageId(usageId)
                .combinationName(combinationName)
                .sortOrder(sortOrder != null ? sortOrder : 0)
                .configItemIds(configItemIds)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    /**
     * 更新组合信息
     *
     * @param combinationName 组合名称
     * @param sortOrder       排序序号
     */
    public void updateInfo(String combinationName, Integer sortOrder) {
        if (combinationName != null) {
            this.combinationName = combinationName;
        }
        if (sortOrder != null) {
            this.sortOrder = sortOrder;
        }
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 更新配置项列表
     *
     * @param configItemIds 新的配置项ID列表
     */
    public void updateConfigItems(List<ConfigItemId> configItemIds) {
        this.configItemIds = configItemIds;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 添加配置项
     *
     * @param configItemId 配置项ID
     */
    public void addConfigItem(ConfigItemId configItemId) {
        if (this.configItemIds != null && !this.configItemIds.contains(configItemId)) {
            this.configItemIds.add(configItemId);
            this.updatedTime = LocalDateTime.now();
        }
    }

    /**
     * 移除配置项
     *
     * @param configItemId 配置项ID
     */
    public void removeConfigItem(ConfigItemId configItemId) {
        if (this.configItemIds != null) {
            this.configItemIds.remove(configItemId);
            this.updatedTime = LocalDateTime.now();
        }
    }

    /**
     * 检查是否包含指定配置项
     *
     * @param configItemId 配置项ID
     * @return 是否包含
     */
    public boolean containsConfigItem(ConfigItemId configItemId) {
        return this.configItemIds != null && this.configItemIds.contains(configItemId);
    }

    /**
     * 获取配置项数量
     *
     * @return 配置项数量
     */
    public int getConfigItemCount() {
        return this.configItemIds != null ? this.configItemIds.size() : 0;
    }

    /**
     * 检查组合是否有效
     * 有效的组合至少包含一个配置项
     *
     * @return 是否有效
     */
    public boolean isValid() {
        return this.configItemIds != null && !this.configItemIds.isEmpty();
    }

    /**
     * 加载配置项详细信息
     * <p>
     * 将配置项的完整信息加载到实体中，用于详情展示
     *
     * @param configItems 配置项详细信息列表
     */
    public void loadConfigItemDetails(List<ConfigItemEntity> configItems) {
        if (configItems != null) {
            this.configItemDetails = new ArrayList<>(configItems);
        }
    }

}
