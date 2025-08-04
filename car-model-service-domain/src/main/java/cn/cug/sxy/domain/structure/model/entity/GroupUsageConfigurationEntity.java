package cn.cug.sxy.domain.structure.model.entity;

import cn.cug.sxy.domain.structure.model.valobj.ConfigurationId;
import cn.cug.sxy.domain.structure.model.valobj.UsageId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/8/4 16:10
 * @Description 用法与配置多对多关系实体
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupUsageConfigurationEntity {

    /**
     * 关系ID
     */
    private Long id;
    /**
     * 分组用法ID
     */
    private UsageId usageId;
    /**
     * 配置ID
     */
    private ConfigurationId configId;

    /**
     * 创建用法配置关联
     *
     * @param usageId  用法ID
     * @param configId 配置ID
     * @return 关联实体
     */
    public static GroupUsageConfigurationEntity create(UsageId usageId, ConfigurationId configId) {
        if (usageId == null) {
            throw new IllegalArgumentException("用法ID不能为空");
        }
        if (configId == null) {
            throw new IllegalArgumentException("配置ID不能为空");
        }

        return GroupUsageConfigurationEntity.builder()
                .usageId(usageId)
                .configId(configId)
                .build();
    }

}
