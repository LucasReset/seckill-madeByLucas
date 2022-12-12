package com.example.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckill.pojo.User;
import com.example.seckill.vo.LoginVo;
import com.example.seckill.vo.ResponceBean;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
@Service
public interface IUserService extends IService<User> {

    ResponceBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);


    //根据cookie获取用户
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

    ResponceBean updatePassword(String userTicket, String password,HttpServletRequest request, HttpServletResponse response);
}
