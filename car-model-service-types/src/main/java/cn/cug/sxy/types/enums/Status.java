package cn.cug.sxy.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @version 1.0
 * @Date 2025/7/28 15:29
 * @Description 状态枚举
 * @Author jerryhotton
 */

@Getter
@AllArgsConstructor
public enum Status {

    ENABLED("ENABLED", "启用"),
    DISABLED("DISABLED", "禁用"),
    DELETED("DELETED", "删除");

    private final String code;
    private final String info;

    public static Status fromCode(String code) {
        switch (code) {
            case "ENABLED":
                return ENABLED;
            case "DISABLED":
                return DISABLED;
            case "DELETED":
                return DELETED;
            default:
                throw new IllegalArgumentException("Invalid CategoryStatus code: " + code);
        }
    }

}
