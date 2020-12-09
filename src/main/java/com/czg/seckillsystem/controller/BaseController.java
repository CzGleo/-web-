package com.czg.seckillsystem.controller;

public class BaseController {

    public static final String CONTENT_TYPE_FORMED = "application/x-www-form-urlencoded";

    /**
     * 定义exceptionHandler解决未被controller层吸收的exception
     * @param request
     * @param ex
     * @return
     */
/*    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handlerException(HttpServletRequest request, Exception ex) {
        Map<String, Object> responseData = new HashMap<>();
        if (ex instanceof BusinessException) {
            BusinessException businessException = (BusinessException) ex;
            responseData.put("errorCode", businessException.getErrorCode());
            responseData.put("errorMsg", businessException.getErrorMsg());
        } else {
            responseData.put("errorCode", EmBusinessError.UNKNOWN_ERROR.getErrorCode());
            responseData.put("errorMsg", EmBusinessError.UNKNOWN_ERROR.getErrorMsg());
        }
        return CommonReturnType.create(responseData, "fail");
    }*/
}
