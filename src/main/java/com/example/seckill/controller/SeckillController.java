package com.example.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.seckill.config.AccessLimit;
import com.example.seckill.exception.GlobalException;
import com.example.seckill.pojo.Order;
import com.example.seckill.pojo.SeckillMessage;
import com.example.seckill.pojo.SeckillOrders;
import com.example.seckill.pojo.User;
import com.example.seckill.rabbitmq.MQSender;
import com.example.seckill.service.IGoodsService;
import com.example.seckill.service.IOrderService;
import com.example.seckill.service.ISeckillOrdersService;
import com.example.seckill.utils.JsonUtil;
import com.example.seckill.vo.GoodsVo;
import com.example.seckill.vo.ResponceBean;
import com.example.seckill.vo.ResponceEnum;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.utils.CaptchaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {
    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private ISeckillOrdersService seckillOrdersService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MQSender mqSender;

    @Autowired
    private RedisScript<Long> redisScript;

    private Map<Long,Boolean> EmptyStockMap = new HashMap<>();



    @RequestMapping(value = "/{path}/doseckill", method = RequestMethod.POST)
    @ResponseBody
    public ResponceBean doseckill(@PathVariable String path, User user, Long goodsId){
        if(user == null) return ResponceBean.error(ResponceEnum.SESSION_ERROR);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        boolean check = orderService.checkPath(user,goodsId,path);
        if (!check){
            return ResponceBean.error(ResponceEnum.REQUEST_ILLEGAL);
        }

        //判断是否重复抢购
        SeckillOrders seckillOrders = (SeckillOrders) redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
        if(seckillOrders != null){
            return ResponceBean.error(ResponceEnum.REPEAT_ERROR);
        }

        //内存标记，减少redis访问
        if(EmptyStockMap.get(goodsId)) return ResponceBean.error(ResponceEnum.EMPTY_STOCK);

        //redis中预减库存
        Long stock = (Long)redisTemplate.execute(redisScript, Collections.singletonList("seckillGoods:"+goodsId),Collections.EMPTY_LIST);
        if (stock < 0){
            EmptyStockMap.put(goodsId, true);
            return ResponceBean.error(ResponceEnum.EMPTY_STOCK);
        }

        //下单操作，通过mqSender和mqReceiver
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return ResponceBean.success(0);


    }

    /**
     * 获取秒杀结果
     * 返回 成功：orderId 失败：-1 队列中：0
     * @return
     */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public ResponceBean getResult(User user, Long goodsId){
        if (user == null) return ResponceBean.error(ResponceEnum.SESSION_ERROR);
        Long orderId = seckillOrdersService.getResult(user, goodsId);
        return ResponceBean.success(orderId);
    }


    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @AccessLimit(seconds=5,maxCount=5,needLogin=true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public ResponceBean getPath(User user, Long goodsId, String captcha){
        if (user == null){
            return ResponceBean.error(ResponceEnum.SESSION_ERROR);
        }
        boolean check = orderService.checkCaptcha(user,goodsId,captcha);
        if (!check) return ResponceBean.error(ResponceEnum.ERROR_CAPTCHA);
        String str = orderService.createPath(user, goodsId);
        return ResponceBean.success(str);
    }

    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId, HttpServletRequest request, HttpServletResponse response) {
        if (null == user || goodsId < 0) {
            throw new GlobalException(ResponceEnum.REQUEST_ILLEGAL);
        }
        // 设置请求头为输出图片类型
        response.setContentType("image/jpg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码，将结果放入redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:"+user.getId()+":"+goodsId,captcha.text(),300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败",e.getMessage());
        }
    }

    /**
     * 系统初始化，将商品库存加载到redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:"+goodsVo.getId(), goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(), false);
        }
        );
    }

}
