package com.campus.exception;

import com.campus.common.ResultCode;
import lombok.Getter;

/**
 * 自定义业务异常
 */
@Getter
public class ServiceException extends RuntimeException {

    private Integer code;

    public ServiceException(String message) {
        super(message);
        this.code = ResultCode.FAILED.getCode();
    }

    public ServiceException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
