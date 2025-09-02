package cn.cug.sxy.domain.usage.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/6 13:56
 * @Description 用法配置组合规格说明
 * @Author jerryhotton
 */

@Getter
@AllArgsConstructor
public class UsageConfigCombinationSpec {

    /**
     * 组合名称
     */
    private final String combinationName;
    /**
     * 排序序号
     */
    private final Integer sortOrder;
    /**
     * 配置项ID列表
     */
    private final List<ConfigItemId> configItemIds;

    /**
     * 验证规格说明的有效性
     *
     * @return 是否有效
     */
    public boolean isValid() {
        return StringUtils.isNotBlank(combinationName) &&
                sortOrder != null &&
                configItemIds != null && !configItemIds.isEmpty();
    }

}
