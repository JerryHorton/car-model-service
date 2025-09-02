package cn.cug.sxy.domain.workhour.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @version 1.0
 * @Date 2025/1/27 10:00
 * @Description 工时状态枚举
 * @Author jerryhotton
 */

@Getter
@AllArgsConstructor
public enum WorkHourStatus {
    ENABLED("ENABLED", "启用"),
    DISABLED("DISABLED", "禁用"),
    DELETED("DELETED", "已删除");

    private final String code;
    private final String description;

    public static WorkHourStatus fromCode(String code) {
        for (WorkHourStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown work hour status code: " + code);
    }

} 