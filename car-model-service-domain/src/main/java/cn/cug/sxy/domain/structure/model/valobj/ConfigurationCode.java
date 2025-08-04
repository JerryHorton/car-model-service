package cn.cug.sxy.domain.structure.model.valobj;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * @version 1.0
 * @Date 2025/8/4 15:56
 * @Description 配置编码值对象
 * @Author jerryhotton
 */

@Getter
@ToString
@EqualsAndHashCode
public class ConfigurationCode {

    private final String code;

    public ConfigurationCode(String code) {
        if (StringUtils.isBlank(code)) {
            throw new IllegalArgumentException("Configuration code cannot be null or empty");
        }
        this.code = code;
    }

}
