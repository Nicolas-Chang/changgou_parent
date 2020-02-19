package com.changgou.order.listern;


import com.alibaba.fastjson.JSON;
import com.changgou.order.config.RabbitMQConfig;
import com.changgou.order.dao.TaskMapper;
import com.changgou.order.pojo.Task;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DelTask {

    @Autowired
    private TaskMapper taskMapper;



    @RabbitListener(queues = RabbitMQConfig.CG_BUYING_FINISHADDPOINT)
    public void delTask(String message){
        Task task = JSON.parseObject(message, Task.class);
        taskMapper.delete(task);
    }

}
