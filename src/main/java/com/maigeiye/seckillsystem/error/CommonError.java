package com.maigeiye.seckillsystem.error;

public interface CommonError {
    int getErrorCode();
    String getErrorMsg();
    CommonError setErrorMsg(String errorMsg);
}
