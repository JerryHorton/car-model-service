package cn.cug.sxy.domain.structure.model.valobj;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * @version 1.0
 * @Date 2025/7/28 15:26
 * @Description 车型结构树模板编码值对象
 * @Author jerryhotton
 */

@Getter
@ToString
@EqualsAndHashCode
public class TemplateCode {

    private final String code;

    public TemplateCode(String code) {
        if (StringUtils.isEmpty(code)) {
            throw new IllegalArgumentException("Template Code cannot be null or empty");
        }
        this.code = code;
    }

}
