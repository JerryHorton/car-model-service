package cn.cug.sxy.domain.workhour.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @version 1.0
 * @Date 2025/1/27 10:00
 * @Description 工时类型枚举
 * @Author jerryhotton
 */

@Getter
@AllArgsConstructor
public enum WorkHourType {
    MAIN("MAIN", "主工时"),
    SUB("SUB", "子工时");

    private final String code;
    private final String description;

    public static WorkHourType fromCode(String code) {
        for (WorkHourType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown work hour type code: " + code);
    }

} 