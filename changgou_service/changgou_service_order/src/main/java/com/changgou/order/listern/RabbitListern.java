package com.changgou.order.listern;

import com.alibaba.fastjson.JSON;
import com.changgou.order.config.RabbitMQConfig;
import com.changgou.order.service.OrderService;
import com.sun.javafx.collections.MappingChange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RabbitListern {


    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void function1(String message){
        System.out.println("接收到的信息是" + message);
        Map map = JSON.parseObject(message, Map.class);
        String orderId = (String) map.get("orderId");
        String transactionId = (String) map.get("transactionId");
        orderService.updatePayStatus(orderId,transactionId);
    }


    @RabbitListener(queues = "queue.ordertimeout")
    public void function2(String orderId){
        System.out.println("接收到的信息为" + orderId);
        try {
            orderService.closeOrder(orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
