package cn.cug.sxy.domain.usage.model.aggregate;

import cn.cug.sxy.domain.usage.model.entity.UsageConfigCombinationEntity;
import cn.cug.sxy.domain.usage.model.entity.UsageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/6 16:10
 * @Description 用法聚合对象
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsageAggregate {

    /**
     * 用法实体
     */
    private UsageEntity usage;
    /**
     * 配置组合列表
     */
    private List<UsageConfigCombinationEntity> combinations;

}
