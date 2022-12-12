package com.example.seckill.vo;


import com.example.seckill.utils.ValidateUtil;
import com.example.seckill.validator.IsMobile;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (required){
            return ValidateUtil.isMoblie(value);
        }else {
            if (StringUtils.isEmpty(value)) return true;
            else return ValidateUtil.isMoblie(value);
        }
    }
}
