package com.example.seckill.service;

import com.example.seckill.pojo.SeckillOrders;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface ISeckillOrdersService extends IService<SeckillOrders> {

    Long getResult(User user, Long goodsId);
}
