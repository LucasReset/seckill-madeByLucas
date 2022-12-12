package com.example.seckill.exception;

import com.example.seckill.vo.ResponceEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalException extends RuntimeException{
    private ResponceEnum responceEnum;
}
