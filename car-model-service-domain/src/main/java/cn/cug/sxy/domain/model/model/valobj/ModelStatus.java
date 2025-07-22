package cn.cug.sxy.domain.model.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @version 1.0
 * @Date 2025/7/21 19:07
 * @Description 车型状态枚举
 * @Author jerryhotton
 */

@Getter
@AllArgsConstructor
public enum ModelStatus {

    PENDING("pending", "待发布"),
    PUBLISHED("published", "已发布"),
    DEPRECATED("deprecated", "废止");

    private final String code;
    private final String info;

    public static ModelStatus fromCode(String code) {
        switch (code) {
            case "pending":
                return PENDING;
            case "published":
                return PUBLISHED;
            case "deprecated":
                return DEPRECATED;
            default:
                throw new IllegalArgumentException("Invalid ModelStatus code: " + code);
        }
    }

    /**
     * 发布车型
     * 只有待发布状态的车型才能发布
     *
     * @return 发布后的状态
     * @throws IllegalStateException 如果当前状态不是待发布
     */
    public ModelStatus publish() {
        if (this == PENDING) {
            return PUBLISHED;
        }
        throw new IllegalStateException("Cannot publish model with status: " + code);
    }

    /**
     * 废止车型
     * 只有已发布状态的车型才能废止
     *
     * @return 废止后的状态
     * @throws IllegalStateException 如果当前状态不是已发布
     */
    public ModelStatus deprecate() {
        if (this == PUBLISHED) {
            return DEPRECATED;
        }
        throw new IllegalStateException("Cannot deprecate model with status: " + code);
    }

}
