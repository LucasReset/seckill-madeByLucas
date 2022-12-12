package com.example.seckill.rabbitmq;


import com.example.seckill.pojo.SeckillMessage;
import com.example.seckill.pojo.SeckillOrders;
import com.example.seckill.pojo.User;
import com.example.seckill.service.IGoodsService;
import com.example.seckill.service.IOrderService;
import com.example.seckill.utils.JsonUtil;
import com.example.seckill.vo.GoodsVo;
import com.example.seckill.vo.ResponceBean;
import com.example.seckill.vo.ResponceEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;
    /**
     * 下单操作
     */
    @RabbitListener(queues = "seckillQueue")
    public void receive(String message){
        log.info("接收消息："+message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        //判断库存
        if(goodsVo.getStockCount() < 1) return;
        //判断重复抢购
        SeckillOrders seckillOrders = (SeckillOrders) redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
        if(seckillOrders != null){
            return;
        }
        //下单
        orderService.seckill(user,goodsVo);
    }
}
