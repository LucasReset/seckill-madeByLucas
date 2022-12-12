package com.example.seckill.controller;


import com.example.seckill.pojo.User;
import com.example.seckill.service.IOrderService;
import com.example.seckill.vo.OrderDetailVo;
import com.example.seckill.vo.ResponceBean;
import com.example.seckill.vo.ResponceEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @RequestMapping("/detail")
    @ResponseBody
    public ResponceBean detail(User user, Long orderId){
        if(user == null){
            return ResponceBean.error(ResponceEnum.SESSION_ERROR);
        }

        OrderDetailVo detail = orderService.detail(orderId);

        return ResponceBean.success(detail);
    }
}
