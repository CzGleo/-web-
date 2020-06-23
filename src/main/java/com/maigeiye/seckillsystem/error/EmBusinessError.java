package com.maigeiye.seckillsystem.error;

/**
 * 错误通用枚举类
 */
public enum EmBusinessError implements CommonError {
    // 通用错误
    UNKNOWN_ERROR(10001, "未知错误"),
    PARAMETER_VALIDATION_ERROR(10002, "参数错误"),
    // 用户错误
    USER_NOT_EXIT(20001, "用户不存在"),
    USER_LOGIN_FAIL(20002, "用户名或者密码不正确"),
    USER_NOT_LOGIN(20003, "用户未登录"),
    // 商品错误
    STOCK_NOT_ENOUGH(30001, "商品库存不足"),
    MQ_SEND_FAIL(30002, "库存异步消息失败")
    ;

    private EmBusinessError(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    private int errorCode;
    private String errorMsg;

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }

    @Override
    public CommonError setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }
}
