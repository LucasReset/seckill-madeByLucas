package com.example.seckill.exception;


import com.example.seckill.vo.ResponceBean;
import com.example.seckill.vo.ResponceEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandle {

    @ExceptionHandler(Exception.class)
    public ResponceBean ExceptionHandler(Exception e){
        if (e instanceof GlobalException){
            GlobalException ex = (GlobalException) e;
            return ResponceBean.error(ex.getResponceEnum());
        }else if(e instanceof BindException){
            BindException ex = (BindException) e;
            ResponceBean responceBean = ResponceBean.error(ResponceEnum.BIND_ERROR);
            responceBean.setMessage("参数效验异常: " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return responceBean;
        }

        return ResponceBean.error(ResponceEnum.ERROR);
    }
}
