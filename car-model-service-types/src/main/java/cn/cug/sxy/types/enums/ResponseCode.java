package cn.cug.sxy.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS("0000", "成功"),
    UN_ERROR("0001", "未知失败"),
    ILLEGAL_PARAMETER("0002", "非法参数"),

    NULL_RESPONSE("2001", "接口返回为空"),

    CAR_SERIES_CODE_EXISTS_ERROR("ERR_BIZ_001", "车系编码已存在"),
    CAR_SERIES_CODE_NOT_EXISTS_ERROR("ERR_BIZ_002", "车系编码不存在"),
    CAR_MODEL_EXIST_ERROR("ERR_BIZ_003", "车型已存在"),
    CAR_MODEL_NOT_EXIST_ERROR("ERR_BIZ_004", "车型不存在"),

    // 工时相关错误码
    WORK_HOUR_CODE_EXISTS_ERROR("ERR_BIZ_101", "工时代码已存在"),
    WORK_HOUR_NOT_FOUND_ERROR("ERR_BIZ_102", "工时不存在"),
    PARENT_WORK_HOUR_NOT_FOUND_ERROR("ERR_BIZ_103", "父工时不存在"),
    PARENT_WORK_HOUR_MUST_BE_MAIN_ERROR("ERR_BIZ_104", "父工时必须为主工时"),
    CANNOT_DELETE_MAIN_WORK_HOUR_WITH_SUBS_ERROR("ERR_BIZ_105", "主工时有子工时时不能删除"),

    // 模板相关错误码
    TEMPLATE_NOT_FOUND("ERR_BIZ_202", "模板不存在"),
    TEMPLATE_READ_ERROR("ERR_BIZ_203", "模板读取失败"),

    // 备件相关错误码
    PART_CODE_EXISTS_ERROR("ERR_BIZ_301", "备件编码已存在"),
    PART_NOT_FOUND_ERROR("ERR_BIZ_302", "备件不存在"),
    PART_BIND_FILE_FORMAT_ERROR("ERR_BIZ_303", "备件绑定工时文件格式错误"),
    PART_BIND_HOUR_NOT_FOUND_ERROR("ERR_BIZ_304", "文件中的工时不存在"),
    PART_BIND_HOUR_NOT_MAIN_ERROR("ERR_BIZ_305", "只能绑定主工时"),
    PART_BIND_DUPLICATE_ERROR("ERR_BIZ_306", "备件与该工时已绑定"),

    USAGE_NOT_FOUND("ERR_BIZ_401", "用法不存在"),
    PART_NOT_FOUND("ERR_BIZ_402", "备件不存在"),

    ;

    private String code;
    private String info;

}
