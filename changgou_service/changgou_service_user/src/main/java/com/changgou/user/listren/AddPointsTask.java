package com.changgou.user.listren;


import com.alibaba.fastjson.JSON;
import com.changgou.order.pojo.Task;
import com.changgou.user.config.RabbitMQConfig;
import com.changgou.user.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AddPointsTask {


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private UserService userService;

    @RabbitListener(queues = RabbitMQConfig.CG_BUYING_ADDPOINT)
    public void receiverMessage(String message){
        Task task = JSON.parseObject(message, Task.class);
        if(task == null || StringUtils.isEmpty(task.getRequestBody())){
            return;
        }
        //判断redis中是否存在
        Object value = redisTemplate.boundValueOps(task.getId()).get();
        if(value != null){
            return;
        }
        int i = userService.uodateOptines(task);
        if(i<=0){
            return;
        }
        //返回通知结果
        rabbitTemplate.convertAndSend(RabbitMQConfig.EX_BUYING_ADDPOINTUSER,RabbitMQConfig.CG_BUYING_FINISHADDPOINT_KEY,JSON.toJSONString(task));
    }
}
