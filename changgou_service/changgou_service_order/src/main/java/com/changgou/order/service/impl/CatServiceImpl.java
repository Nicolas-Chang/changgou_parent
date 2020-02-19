package com.changgou.order.service.impl;

import com.changgou.entity.Result;
import com.changgou.goods.feign.SkuFegin;
import com.changgou.goods.feign.SpuFegin;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CatServiceImpl implements CartService {

    private static final String CART = "cart_";

    @Autowired
    private SkuFegin skuFegin;

    @Autowired
    private SpuFegin spuFegin;


    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void add(String skuId, String username, Integer num) {
            //判断redis中是否有数据

        OrderItem orderItem = (OrderItem) redisTemplate.boundHashOps(CART + username).get(skuId);

        if(orderItem != null){
            //有数据 则追加
            orderItem.setNum(orderItem.getNum() + num);  //次数
            orderItem.setMoney(orderItem.getMoney() * num);  //j价格变化
            orderItem.setPayMoney(orderItem.getPayMoney() * num);   //支付金额
        }else {
            //没有则刷新
            Result<Sku> skuResult = skuFegin.findById(skuId);
            Sku sku = skuResult.getData();
            Result<Spu> spuResult = spuFegin.findById(sku.getSpuId());
            Spu spu = spuResult.getData();

            //新建方法 将sku数据全部追加进orderitemd对象中
            orderItem = sku2OrderItem(spu, sku, num);
        }
        redisTemplate   .boundHashOps(CART+username).put(skuId,orderItem);



    }

    @Override
    public Map list(String username) {
        Map map = new HashMap();
        List<OrderItem> list = redisTemplate.boundHashOps(CART + username).values();
        map.put("orderItemList",list);
        //商品总量与价格
        Integer totalNum = 0;
        Integer totalMoney = 0;
        for (OrderItem orderItem : list) {
            totalNum += orderItem.getNum();
            totalMoney += orderItem.getMoney();
        }
        map.put("totalNum",totalNum);
        map.put("totalMoney",totalMoney);
        return map;
    }

    private OrderItem sku2OrderItem(Spu spu,Sku sku,Integer num){
        OrderItem orderItem = new OrderItem();

        orderItem.setName(sku.getName());
        orderItem.setPrice(sku.getPrice());
        orderItem.setMoney(sku.getPrice());
        orderItem.setPayMoney(sku.getPrice() * num);
        orderItem.setSkuId(sku.getId());
        orderItem.setNum(num);
        orderItem.setImage(sku.getImage());
        orderItem.setWeight(sku.getWeight()*num);
        orderItem.setSpuId(sku.getSpuId());

        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());

        return orderItem;
    }
}
