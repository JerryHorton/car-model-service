package cn.cug.sxy.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @version 1.0
 * @Date 2025/9/3 17:25
 * @Description 模版文件类型
 * @Author jerryhotton
 */

@Getter
@AllArgsConstructor
public enum TemplateFileType {

    WORK_HOUR("work_hour") {
        @Override
        public String generateTemplatePath() {
            return "templates/workhour/template.xlsx";
        }
    },
    PART_HOUR("part_hour") {
        @Override
        public String generateTemplatePath() {
            return "templates/parthour/template.xlsx";
        }
    },
    ;

    private final String type;

    public abstract String generateTemplatePath();

    public static TemplateFileType fromType(String type) {
        for (TemplateFileType templateFileType : TemplateFileType.values()) {
            if (templateFileType.getType().equals(type)) {
                return templateFileType;
            }
        }
        throw new IllegalArgumentException("未知的模版文件类型: " + type);
    }

}
