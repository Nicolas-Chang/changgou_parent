package com.changgou.page.listener;


import com.changgou.page.config.RabbitConfig;
import com.changgou.page.service.PageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
public class PageListener {

    @Autowired
    PageService pageService;


    @RabbitListener(queues = RabbitConfig.PAGE_QUEUE)
    public void recriver(String spuId){
        System.out.println(spuId);
        pageService.generatemplatePage(spuId);

    }

}
