package cn.cug.sxy.domain.usage.model.entity;

import cn.cug.sxy.domain.part.model.valobj.PartId;
import cn.cug.sxy.domain.usage.model.valobj.UsageId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/27 13:49
 * @Description 用法备件实体
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsagePartEntity {

    /**
     * 主键
     */
    private Long id;
    /**
     * 用法ID
     */
    private UsageId usageId;
    /**
     * 备件ID
     */
    private PartId partId;
    /**
     * 数量
     */
    private Integer count;
    /**
     * 备件编码（非数据库字段，用于展示）
     */
    private String partCode;
    /**
     * 备件名称（非数据库字段，用于展示）
     */
    private String partName;

    /**
     * 创建用法备件关联
     *
     * @param usageId 用法ID
     * @param partId  备件ID
     * @param count   数量
     * @return 用法备件关联实体
     */
    public static UsagePartEntity create(UsageId usageId, PartId partId, Integer count) {
        return UsagePartEntity.builder()
                .usageId(usageId)
                .partId(partId)
                .count(count)
                .build();
    }

    /**
     * 更新数量
     *
     * @param count 数量
     */
    public void updateCount(Integer count) {
        this.count = count;
    }

}
