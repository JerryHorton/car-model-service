package cn.cug.sxy.domain.series.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @version 1.0
 * @Date 2025/7/21 19:10
 * @Description 动力类型枚举
 * @Author jerryhotton
 */

@Getter
@AllArgsConstructor
public enum PowerType {

    GASOLINE("gasoline", "汽油"),
    DIESEL("diesel", "柴油"),
    HYBRID("hybrid", "混合动力"),
    ELECTRIC("electric", "纯电动");

    private final String code;
    private final String info;

    public static PowerType fromCode(String code) {
        switch (code) {
            case "gasoline":
                return GASOLINE;
            case "diesel":
                return DIESEL;
            case " hybrid":
                return HYBRID;
            case "electric":
                return ELECTRIC;
            default:
                throw new IllegalArgumentException("Invalid PowerType code: " + code);
        }
    }

}
