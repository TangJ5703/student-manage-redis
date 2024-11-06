package com.biz.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Result<T> {
    private T data;
    // 消息状态码
    private int code;

    // 消息内容
    private String msg;

    public Result(T data) {
        this.data = data;
        this.code = HttpStatus.SUCCESS;
        this.msg = "响应成功";
    }

    public Result(int code, String message) {
        this.code = code;
        this.msg = message;
        this.data = null;
    }

    public static <T> Result<T> success() {
        return new Result<>(null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message);
    }

}
