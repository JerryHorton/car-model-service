package cn.cug.sxy.domain.structure.model.valobj;

import cn.cug.sxy.types.exception.AppException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * @version 1.0
 * @Date 2025/7/28 15:22
 * @Description 车型结构树实例编码值对象
 * @Author jerryhotton
 */

@Getter
@ToString
@EqualsAndHashCode
public class InstanceCode {

    private final String code;

    public InstanceCode(String code) {
        if (StringUtils.isBlank(code)) {
            throw new IllegalArgumentException("Instance code cannot be null or empty");
        }
        this.code = code;
    }

}
