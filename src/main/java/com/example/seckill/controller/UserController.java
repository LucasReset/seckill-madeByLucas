package com.example.seckill.controller;


import com.example.seckill.pojo.User;
import com.example.seckill.rabbitmq.MQSender;
import com.example.seckill.vo.ResponceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MQSender mqSender;


    //用户信息（测试）
    @RequestMapping("/info")
    @ResponseBody
    public ResponceBean info(User user){
        return ResponceBean.success(user);
    }


    /*
    @RequestMapping("mq")
    @ResponseBody
    public void mq(){
        mqSender.send("Hello");
    }

    //fanout模式
    @RequestMapping("mq/fanout")
    @ResponseBody
    public void mq01(){
        mqSender.send("Hello");
    }


//    /**
//     * direct模式1
//     */
//    @RequestMapping("/mq/direct01")
//    @ResponseBody
//    public void mq02(){
//        mqSender.send01("Hello,red");
//    }
//
//    /**
//     * direct模式2
//     */
//    @RequestMapping("/mq/direct02")
//    @ResponseBody
//    public void mq03(){
//        mqSender.send02("Hello,green");
//    }
//
//
//    /**
//     * topic模式1
//     */
//    @RequestMapping("/mq/topic01")
//    @ResponseBody
//    public void mq04(){
//        mqSender.send03("Hello, red");
//    }
//
//    /**
//     * topic模式2
//     */
//    @RequestMapping("/mq/topic02")
//    @ResponseBody
//    public void mq05(){
//        mqSender.send04("Hello, green");
//    }




}
