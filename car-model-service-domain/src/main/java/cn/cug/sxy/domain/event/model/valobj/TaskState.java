package cn.cug.sxy.domain.event.model.valobj;

import lombok.Getter;

/**
 * @version 1.0
 * @Date 2025/8/12 15:46
 * @Description 任务状态枚举
 * @Author jerryhotton
 */

@Getter
public enum TaskState {

    CREATE("CREATE", "创建"),
    PUBLISHED("PUBLISHED", "已发布"),
    PROCESSING("PROCESSING", "处理中"),
    COMPLETED("COMPLETED", "完成"),
    FAILED("FAILED", "失败"),
    RETRY("RETRY", "重试中");

    private final String code;
    private final String info;


    TaskState(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public static TaskState fromCode(String code) {
        switch (code) {
            case "CREATE":
                return CREATE;
            case "PUBLISHED":
                return PUBLISHED;
            case "PROCESSING":
                return PROCESSING;
            case "COMPLETED":
                return COMPLETED;
            case "FAILED":
                return FAILED;
            case "RETRY":
                return RETRY;
            default:
                throw new IllegalArgumentException("unknown task status: " + code);
        }
    }

}
