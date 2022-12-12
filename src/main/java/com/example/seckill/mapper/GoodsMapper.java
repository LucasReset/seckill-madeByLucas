package com.example.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seckill.pojo.Goods;
import com.example.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 */
public interface GoodsMapper extends BaseMapper<Goods> {
    
    
    //获取商品列表
    @Select({
            "<script>",
            "SELECT",
            "g.id,",
            "g.goods_name,",
            "g.goods_title,",
            "g.goods_img,",
            "g.goods_detail,",
            "g.goods_price,",
            "g.goods_stock,",
            "sg.seckill_price,",
            "sg.stock_count,",
            "sg.Start_date,",
            "sg.end_date",
            "FROM t_goods g LEFT JOIN t_seckill_goods as sg ",
            "ON g.id = sg.goods_id",
            "</script>"
    })
    List<GoodsVo> findGoodsVo();


    @Select({
            "<script>",
            "SELECT",
            "g.id,",
            "g.goods_name,",
            "g.goods_title,",
            "g.goods_img,",
            "g.goods_detail,",
            "g.goods_price,",
            "g.goods_stock,",
            "sg.seckill_price,",
            "sg.stock_count,",
            "sg.Start_date,",
            "sg.end_date",
            "FROM t_goods g LEFT JOIN t_seckill_goods as sg ",
            "ON g.id = sg.goods_id",
            "WHERE g.id = #{goodsId}",
            "</script>"
    })
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
