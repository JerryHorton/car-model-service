package cn.cug.sxy.domain.model.model.valobj;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @version 1.0
 * @Date 2025/7/21 18:57
 * @Description 车型编码值对象
 * @Author jerryhotton
 */

@Getter
@ToString
@EqualsAndHashCode
public class ModelCode {

    private final String code;

    public ModelCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Model code cannot be null or empty");
        }
        this.code = code;
    }

}
