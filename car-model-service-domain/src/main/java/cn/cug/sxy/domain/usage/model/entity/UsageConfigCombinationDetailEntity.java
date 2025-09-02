package cn.cug.sxy.domain.usage.model.entity;

import cn.cug.sxy.domain.usage.model.valobj.ConfigItemId;
import cn.cug.sxy.domain.usage.model.valobj.UsageConfigCombinationDetailId;
import cn.cug.sxy.domain.usage.model.valobj.UsageConfigCombinationId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/5 19:46
 * @Description 用法配置组合明细实体
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsageConfigCombinationDetailEntity {

    /**
     * 明细ID
     */
    private UsageConfigCombinationDetailId id;
    /**
     * 组合ID
     * 关联到具体的配置组合
     */
    private UsageConfigCombinationId combinationId;
    /**
     * 配置项ID
     * 关联到具体的配置项
     */
    private ConfigItemId configItemId;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 创建用法配置组合明细
     *
     * @param combinationId 组合ID
     * @param configItemId  配置项ID
     * @return 用法配置组合明细实体
     */
    public static UsageConfigCombinationDetailEntity create(UsageConfigCombinationId combinationId,
                                                            ConfigItemId configItemId) {
        return UsageConfigCombinationDetailEntity.builder()
                .combinationId(combinationId)
                .configItemId(configItemId)
                .createdTime(LocalDateTime.now())
                .build();
    }

    /**
     * 检查是否属于指定组合
     *
     * @param combinationId 组合ID
     * @return 是否属于指定组合
     */
    public boolean belongsToCombination(UsageConfigCombinationId combinationId) {
        return this.combinationId != null && this.combinationId.equals(combinationId);
    }

    /**
     * 检查是否关联指定配置项
     *
     * @param configItemId 配置项ID
     * @return 是否关联指定配置项
     */
    public boolean relatesConfigItem(ConfigItemId configItemId) {
        return this.configItemId != null && this.configItemId.equals(configItemId);
    }

    /**
     * 获取组合ID的Long值
     *
     * @return 组合ID的Long值
     */
    public Long getCombinationIdValue() {
        return this.combinationId != null ? this.combinationId.getId() : null;
    }

    /**
     * 获取配置项ID的Long值
     *
     * @return 配置项ID的Long值
     */
    public Long getConfigItemIdValue() {
        return this.configItemId != null ? this.configItemId.getId() : null;
    }

    /**
     * 生成业务键
     *
     * @return 业务键
     */
    public String getBusinessKey() {
        return getCombinationIdValue() + "_" + getConfigItemIdValue();
    }

}
