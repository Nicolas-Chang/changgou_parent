package com.changgou.order.listern;


import com.alibaba.fastjson.JSON;
import com.changgou.order.config.RabbitMQConfig;
import com.changgou.order.dao.TaskMapper;
import com.changgou.order.pojo.Task;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class QueryPointTask {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private TaskMapper taskMapper;


    @Scheduled(cron = "0/2 * * * *  ? ")
    public void queryTask(){
        List<Task> taskList = taskMapper.findByNowTimeBefore(new Date());
        if(taskList.size() > 0 && taskList != null){
            for (Task task : taskList) {
                rabbitTemplate.convertAndSend(RabbitMQConfig.EX_BUYING_ADDPOINTUSER,RabbitMQConfig.CG_BUYING_ADDPOINT_KEY, JSON.toJSONString(task));
            }
        }
    }

}
