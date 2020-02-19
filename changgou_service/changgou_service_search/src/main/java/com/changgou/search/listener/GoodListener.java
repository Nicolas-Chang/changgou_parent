package com.changgou.search.listener;


import com.changgou.search.config.RabbitConfig;
import com.changgou.search.service.SkuSearchService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodListener {


    @Autowired
    private SkuSearchService skuSearchService;

    @RabbitListener(queues = RabbitConfig.UPDATE_QUEUE)
    public void reciveMessage(String spuId){
        System.out.println("接收到的id为" + spuId);

        skuSearchService.importDateToEsById(spuId);
    }

    @RabbitListener(queues = RabbitConfig.PULL_QUEUE)
    public void pullMessage(String spuId){
        System.out.println("接收到的是" + spuId);
        skuSearchService.deleted(spuId);
    }

}
