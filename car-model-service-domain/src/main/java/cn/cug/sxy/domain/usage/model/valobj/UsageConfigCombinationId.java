package cn.cug.sxy.domain.usage.model.valobj;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @version 1.0
 * @Date 2025/8/5 19:26
 * @Description 用法配置组合ID值对象
 * @Author jerryhotton
 */

@Getter
@ToString
@EqualsAndHashCode
public class UsageConfigCombinationId {

    private final Long id;

    public UsageConfigCombinationId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("UsageConfigCombination ID cannot be null or less than or equal to 0");
        }
        this.id = id;
    }

}
