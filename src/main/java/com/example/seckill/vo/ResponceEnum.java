package com.example.seckill.vo;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 公共返回对象枚举
 */
@Getter
@ToString
@AllArgsConstructor
public enum ResponceEnum {
    //  通用
    SUCCESS(200, "SUCCESS"),
    ERROR(500, "服务异常"),

    //登陆模块5002
    LOGIN_ERROR(500210, "用户名或者密码错误"),
    MOBILE_ERROR(500211, "手机号码格式不正确"),
    BIND_ERROR(500212, "参数效验异常"),
    MOBILE_NOT_EXIT(500213, "手机号码不存在"),
    PASSWORD_UPDATE_FAIL(500214, "更新号码失败"),
    SESSION_ERROR(500215, "用户不存在"),

    //秒杀模块5005
    EMPTY_STOCK(500500, "库存不足"),
    REPEAT_ERROR(500501, "每人限购一件"),
    REQUEST_ILLEGAL(500502, "请求非法"),
    ERROR_CAPTCHA(500503, "验证码错误"),
    ACCESS_LIMIT_REACHED(500504, "访问过于频繁"),

    //订单模块
    ORDER_NOT_EXIT(500300, "订单信息不存在"),
    ;

    private final Integer code;
    private final String message;
}
