package cn.cug.sxy.domain.usage.model.valobj;

import cn.cug.sxy.domain.structure.model.entity.StructureInstanceNodeEntity;
import cn.cug.sxy.domain.usage.model.entity.UsageConfigCombinationEntity;
import cn.cug.sxy.domain.usage.model.entity.UsageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/6 13:59
 * @Description 用法创建聚合对象
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsageCreationAggregate {

    /**
     * 用法实体
     */
    private UsageEntity usage;
    /**
     * 配置组合列表
     */
    private List<UsageConfigCombinationEntity> combinations;
    /**
     * 用法实例节点
     */
    private StructureInstanceNodeEntity instanceNode;

}
