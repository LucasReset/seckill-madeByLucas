package com.example.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公共返回对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponceBean {

    private long code;
    private String message;
    private Object obj;

    /**
     * 成功返回结果
     * @return
     */
    public static ResponceBean success(){
        return new ResponceBean(ResponceEnum.SUCCESS.getCode(), ResponceEnum.SUCCESS.getMessage(), null);
    }

    public static ResponceBean success(Object o){
        return new ResponceBean(ResponceEnum.SUCCESS.getCode(), ResponceEnum.SUCCESS.getMessage(), o);
    }

    /**
     * 失败返回结果
     * @param responceEnum
     * @return
     */
    public static ResponceBean error(ResponceEnum responceEnum){
        return new ResponceBean(responceEnum.getCode(), responceEnum.getMessage(), null);
    }

    public static ResponceBean error(ResponceEnum responceEnum, Object o){
        return new ResponceBean(responceEnum.getCode(), responceEnum.getMessage(), o);
    }
}
