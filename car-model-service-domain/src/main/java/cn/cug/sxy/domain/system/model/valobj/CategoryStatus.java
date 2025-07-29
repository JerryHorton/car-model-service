package cn.cug.sxy.domain.system.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @version 1.0
 * @Date 2025/7/25 17:47
 * @Description
 * @Author jerryhotton
 */

@Getter
@AllArgsConstructor
public enum CategoryStatus {

    ENABLED("ENABLED", "启用"),
    DISABLED("DISABLED", "禁用"),
    DELETED("DELETED", "删除");

    private final String code;
    private final String info;

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    public static CategoryStatus fromCode(String code) {
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
