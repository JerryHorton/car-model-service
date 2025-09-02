package cn.cug.sxy.domain.usage.model.valobj;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @version 1.0
 * @Date 2025/8/4 15:54
 * @Description 用法ID值对象
 * @Author jerryhotton
 */

@Getter
@ToString
@EqualsAndHashCode
public class UsageId {

    private final Long id;

    public UsageId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Usage ID cannot be null or less than or equal to 0");
        }
        this.id = id;
    }

}
