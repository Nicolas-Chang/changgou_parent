package com.changgou.user.config;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {


    //添加积分任务交换机
   public static final String EX_BUYING_ADDPOINTUSER = "ex_buying_addpointuser";

   //添加积分消息队列
   public static final String CG_BUYING_ADDPOINT = "cg_buying_addpoint";

   //完成添加积分消息队列
   public static final String CG_BUYING_FINISHADDPOINT = "cg_buying_finishaddpoint";

   //添加积分路由key
   public static final String CG_BUYING_ADDPOINT_KEY = "addpoint";

   //完成添加积分路由key
   public static final String CG_BUYING_FINISHADDPOINT_KEY = "finishaddpoint";

    //添加队列

    @Bean(CG_BUYING_ADDPOINT)
    public Queue queue(){
        return new Queue(CG_BUYING_ADDPOINT);
    }
    @Bean(EX_BUYING_ADDPOINTUSER)
    public Exchange exchange(){
        return ExchangeBuilder.directExchange(EX_BUYING_ADDPOINTUSER).durable(true).build();
    }
    @Bean
    public Binding CG_BUYING_ADDPOINT_KEY (@Qualifier(CG_BUYING_ADDPOINT) Queue queue, @Qualifier(EX_BUYING_ADDPOINTUSER) Exchange exchange){
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(CG_BUYING_ADDPOINT_KEY).noargs();
        return binding;
    }
    //完成添加队列
    @Bean(CG_BUYING_FINISHADDPOINT)
    public Queue queue1(){
        return new Queue(CG_BUYING_FINISHADDPOINT);
    }
    @Bean
    public Binding CG_BUYING_FINISHADDPOINT_KEY(@Qualifier(CG_BUYING_FINISHADDPOINT) Queue queue,@Qualifier(EX_BUYING_ADDPOINTUSER) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(CG_BUYING_FINISHADDPOINT_KEY).noargs();
    }

}
