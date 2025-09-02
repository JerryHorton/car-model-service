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
    TEMPLATE_NOT_FOUND("ERR_BIZ_106", "工时模板不存在"),
    TEMPLATE_READ_ERROR("ERR_BIZ_107", "工时模板读取失败");

    private String code;
    private String info;

}
