package com.example.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckill.exception.GlobalException;
import com.example.seckill.mapper.UserMapper;
import com.example.seckill.pojo.User;
import com.example.seckill.service.IUserService;
import com.example.seckill.utils.CookieUtil;
import com.example.seckill.utils.MD5;
import com.example.seckill.utils.UUIDUtil;
import com.example.seckill.vo.LoginVo;
import com.example.seckill.vo.ResponceBean;
import com.example.seckill.vo.ResponceEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 功能描述：登陆
     *
     * @param loginVo
     * @param request
     * @param response
     * @return
     */
    @Override
    public ResponceBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

//        //登陆判断
//        //输错和空的情况
//        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password))
//            return ResponceBean.error(ResponceEnum.LOGIN_ERROR);
//        if(!ValidateUtil.isMoblie(mobile)) return ResponceBean.error(ResponceEnum.MOBILE_ERROR);

        //根据手机号获取用户
        User user = userMapper.selectById(mobile);
        if(null == user) throw new GlobalException(ResponceEnum.LOGIN_ERROR);

        //判断密码是否正确
        if (!MD5.formPassToDBPass(password, user.getSalt()).equals(user.getPassword())){
            throw new GlobalException(ResponceEnum.LOGIN_ERROR);
        }

        //生成cookie
        String ticket = UUIDUtil.uuid();
        //将用户信息存入redis
        redisTemplate.opsForValue().set("user:" + ticket, user);
        CookieUtil.setCookie(request, response,"userTicket", ticket);
        return ResponceBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if(StringUtils.isEmpty(userTicket)) return null;

        User user = (User)redisTemplate.opsForValue().get("user:" + userTicket);
        if(user != null){
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }


    /**
     * 更新密码
     * @param userTicket
     * @param password
     * @param request
     * @param response
     * @return
     */
    @Override
    public ResponceBean updatePassword(String userTicket,  String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(userTicket, request,response);
        if(user == null){
            throw new GlobalException(ResponceEnum.MOBILE_NOT_EXIT);
        }
        user.setPassword(MD5.inputPasstoDBPass(password, user.getSalt()));
        int result = userMapper.updateById(user);
        if(1 == result){
            redisTemplate.delete("user:" + userTicket);
            return ResponceBean.success();
        }
        return ResponceBean.error(ResponceEnum.PASSWORD_UPDATE_FAIL);
    }
}
