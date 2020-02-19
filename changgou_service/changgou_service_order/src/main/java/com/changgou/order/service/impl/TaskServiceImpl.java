package com.changgou.order.service.impl;

import com.changgou.order.dao.TaskHisMapper;
import com.changgou.order.dao.TaskMapper;
import com.changgou.order.pojo.Task;
import com.changgou.order.pojo.TaskHis;
import com.changgou.order.service.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskHisMapper taskHisMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Override
    public void delTask(Task task) {

        //设置删除时间
        task.setDeleteTime(new Date());
        Long id = task.getId();
        task.setId(null);

        //copy至his中
        TaskHis taskHis = new TaskHis();
        BeanUtils.copyProperties(task,taskHis);

        //记录历史
        taskHisMapper.insertSelective(taskHis);
        //删除原来的任务
        task.setId(id);
        taskMapper.deleteByPrimaryKey(task);

    }
}
