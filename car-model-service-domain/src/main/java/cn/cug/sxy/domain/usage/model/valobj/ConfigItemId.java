package cn.cug.sxy.domain.usage.model.valobj;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @version 1.0
 * @Date 2025/8/5 19:21
 * @Description 配置项ID值对象
 * @Author jerryhotton
 */

@Getter
@ToString
@EqualsAndHashCode
public class ConfigItemId {

    private final Long id;

    public ConfigItemId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ConfigItem ID cannot be null or less than or equal to 0");
        }
        this.id = id;
    }

}
