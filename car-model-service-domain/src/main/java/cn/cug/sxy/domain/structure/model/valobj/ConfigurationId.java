package cn.cug.sxy.domain.structure.model.valobj;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @version 1.0
 * @Date 2025/8/4 15:55
 * @Description 配置ID值对象
 * @Author jerryhotton
 */

@Getter
@ToString
@EqualsAndHashCode
public class ConfigurationId {

    private final Long id;

    public ConfigurationId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Configuration ID cannot be null or less than or equal to 0");
        }
        this.id = id;
    }

}
