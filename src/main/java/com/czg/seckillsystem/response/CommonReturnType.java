package com.czg.seckillsystem.response;

/**
 * 响应通用类
 */
public class CommonReturnType {
    // 表明对应请求的返回处理结果，success或fail
    private String status;

    // success返回对应VO的json数据，fail则返回通用的错误码格式
    private Object data;

    public static CommonReturnType create(Object result) {
        return CommonReturnType.create(result, "success");
    }

    public static CommonReturnType create(Object result, String status) {
        CommonReturnType type = new CommonReturnType();
        type.setStatus(status);
        type.setData(result);
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
