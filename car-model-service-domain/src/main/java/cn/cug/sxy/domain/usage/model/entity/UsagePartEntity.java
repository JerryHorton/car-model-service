package cn.cug.sxy.domain.usage.model.entity;

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
     * 用法ID
     */
    private Long usageId;
    /**
     * 备件ID
     */
    private Long partId;
    /**
     * 数量
     */
    private Integer count;

}
