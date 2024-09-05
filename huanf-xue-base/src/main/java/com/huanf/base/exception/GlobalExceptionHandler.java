package com.xuecheng.base.exception;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.ognl.MethodFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;

/**
 * @description 全局异常处理器
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 对项目的自定义异常
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(com.xuecheng.base.exception.XueChengPlusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public com.xuecheng.base.exception.RestErrorResponse customException(com.xuecheng.base.exception.XueChengPlusException e) {
        log.error("【系统异常】{}",e.getErrMessage(),e);
        return new com.xuecheng.base.exception.RestErrorResponse(e.getErrMessage());

    }

    /**
     * 系统异常
     * @param e
     * @return
     */

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public com.xuecheng.base.exception.RestErrorResponse exception(Exception e) {

        log.error("【系统异常】{}",e.getMessage(),e);

        return new com.xuecheng.base.exception.RestErrorResponse(com.xuecheng.base.exception.CommonError.UNKOWN_ERROR.getErrMessage());

    }

    /**
     *MethodArgumentNotValidException
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public com.xuecheng.base.exception.RestErrorResponse methodArgumentNotValidException( MethodArgumentNotValidException e) {

        BindingResult bindingResult = e.getBindingResult();
        ArrayList<Object> errors = new ArrayList<>();
        bindingResult.getFieldErrors().stream().forEach(item->{
            errors.add(item.getDefaultMessage());
                });
        //将list中的错误信息拼接起来
        String errMessage = StringUtils.join(errors, ",");

        log.error("【系统异常】{}",e.getMessage(),errMessage);

        return new com.xuecheng.base.exception.RestErrorResponse(errMessage);

    }
}