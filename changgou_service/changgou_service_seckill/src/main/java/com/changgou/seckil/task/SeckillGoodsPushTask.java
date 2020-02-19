package com.changgou.seckil.task;


import com.changgou.seckil.dao.SeckillGoddsMapper;
import com.changgou.seckil.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.utlis.DateUtil;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class SeckillGoodsPushTask {

    @Autowired
    private SeckillGoddsMapper seckillGoddsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String SECKILL_GOODS_KEY = "seckill_goods_";

    @Scheduled(cron = "0/30 * * * * ?")
    public void loadSecKillGoodsToRedis(){

        /**
         * 1.查询所有符合条件的秒杀商品
         *     1) 获取时间段集合并循环遍历出每一个时间段
         *     2) 获取每一个时间段名称,用于后续redis中key的设置
         *     3) 状态必须为审核通过 status=1
         *     4) 商品库存个数>0
         *     5) 秒杀商品开始时间>=当前时间段
         *     6) 秒杀商品结束<当前时间段+2小时
         *     7) 排除之前已经加载到Redis缓存中的商品数据
         *     8) 执行查询获取对应的结果集
         * 2.将秒杀商品存入缓存
         */
        List<Date> menus = DateUtil.getDateMenus();
        for (Date menu : menus) {

            String extendsName = DateUtil.date2Str(menu);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("status",1);
            criteria.andGreaterThan("stockCount",0);    //库存量不为零
            criteria.andGreaterThanOrEqualTo("startTime",simpleDateFormat.format(menu));  //时间大于该时间
            criteria.andLessThanOrEqualTo("endTime",simpleDateFormat.format(DateUtil.addDateHour(menu,2)));//小于等于
            //从redis中查
            Set keys = redisTemplate.boundHashOps(SECKILL_GOODS_KEY + extendsName).keys();
            if(keys != null && keys.size() >0){
                criteria.andNotIn("id",keys);
            }
            List<SeckillGoods> seckillGoods = seckillGoddsMapper.selectByExample(example);
            for (SeckillGoods seckillGood : seckillGoods) {
                redisTemplate.boundHashOps(SECKILL_GOODS_KEY+extendsName).put(seckillGood.getId(),seckillGood);
            }


        }
    }
}
