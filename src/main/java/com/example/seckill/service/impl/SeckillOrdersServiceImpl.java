package com.example.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckill.mapper.SeckillOrdersMapper;
import com.example.seckill.pojo.SeckillOrders;
import com.example.seckill.pojo.User;
import com.example.seckill.service.ISeckillOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 */
@Service
public class SeckillOrdersServiceImpl extends ServiceImpl<SeckillOrdersMapper, SeckillOrders> implements ISeckillOrdersService {

    @Autowired
    private SeckillOrdersMapper seckillOrdersMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrders seckillOrders = seckillOrdersMapper.selectOne(new QueryWrapper<SeckillOrders>().eq("user_id",user.getId()).eq("goods_id",goodsId));
        if(seckillOrders != null) return seckillOrders.getOrderId();
        else if (redisTemplate.hasKey("isStockEmpty:" + goodsId)) return -1L;
        else return 0L;

    }
}
