package com.maigeiye.seckillsystem.controller;

import com.maigeiye.seckillsystem.error.BusinessException;
import com.maigeiye.seckillsystem.error.EmBusinessError;
import com.maigeiye.seckillsystem.response.CommonReturnType;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonReturnType doError(Exception e) {
        e.printStackTrace();
        Map<String, Object> responseData = new HashMap<>();
        if (e instanceof BusinessException) {
            BusinessException businessException = (BusinessException)e;
            responseData.put("errorCode", businessException.getErrorCode());
            responseData.put("errorMsg", businessException.getErrorMsg());
        } else if (e instanceof ServletRequestBindingException) {
            responseData.put("errorCode", EmBusinessError.UNKNOWN_ERROR.getErrorCode());
            responseData.put("errorMsg", "url绑定路由问题");
        } else if (e instanceof NoHandlerFoundException) {
            responseData.put("errorCode", EmBusinessError.UNKNOWN_ERROR.getErrorCode());
            responseData.put("errorMsg", "没有找到访问路径");
        } else {
            responseData.put("errorCode", EmBusinessError.UNKNOWN_ERROR.getErrorCode());
            responseData.put("errorMsg",EmBusinessError.UNKNOWN_ERROR.getErrorMsg());
        }
        return CommonReturnType.create(responseData, "fail");
    }
}
