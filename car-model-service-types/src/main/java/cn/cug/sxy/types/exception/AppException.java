package cn.cug.sxy.types.exception;

import cn.cug.sxy.types.enums.ResponseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AppException extends RuntimeException {

    private static final long serialVersionUID = 5317680961212299217L;

    /**
     * 异常码
     */
    private String code;
    /**
     * 异常信息
     */
    private String info;

    public AppException(String code) {
        super(code);
        this.code = code;
    }

    public AppException(String code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public AppException(String code, String message) {
        super(message);
        this.code = code;
        this.info = message;
    }

    public AppException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.info = message;
    }

    public AppException(ResponseCode responseCode) {
        super(responseCode.getInfo());
        this.code = responseCode.getCode();
        this.info = responseCode.getInfo();
    }

    public AppException(ResponseCode responseCode, Throwable cause) {
        super(responseCode.getInfo(), cause);
        this.code = responseCode.getCode();
        this.info = responseCode.getInfo();
    }

    @Override
    public String toString() {
        return "cn.cug.sxy.x.api.types.exception.AppException{" +
                "code='" + code + '\'' +
                ", info='" + info + '\'' +
                '}';
    }

}
