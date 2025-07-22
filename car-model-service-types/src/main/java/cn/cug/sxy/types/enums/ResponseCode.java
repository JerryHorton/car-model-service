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

    ;

    private String code;
    private String info;

}
