package cn.cug.sxy.domain.model.model.valobj;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @version 1.0
 * @Date 2025/7/22 17:23
 * @Description VIN码值对象
 * @Author jerryhotton
 */

@Getter
@ToString
@EqualsAndHashCode
public class VinCode {

    private final String code;

    public VinCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("VIN码不能为空");
        }
        // 通常VIN码是17位字符
        if (code.trim().length() != 17) {
            throw new IllegalArgumentException("VIN码长度必须为17位");
        }
        this.code = code.trim().toUpperCase();
    }

}
