package com.changgou.page.config;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    //更新Exchange
    public static final String UPDATE_EXCHANGE = "update_exchange";
    //更新queue
    public static final String UPDATE_QUEUE = "update_queue";
    //下架Exchange
    public static final String PULL_EXCHANGE = "pull_exchange";
    //下架queue
    public static final String PULL_QUEUE = "pull_queue";

    public static final String PAGE_QUEUE = "page_queue";

    @Bean(UPDATE_EXCHANGE)
    public Exchange update_exchange(){
        return ExchangeBuilder.fanoutExchange(UPDATE_EXCHANGE).durable(true).build();
    }

    @Bean(UPDATE_QUEUE)
    public Queue update(){
        return new Queue(UPDATE_QUEUE);
    }


    @Bean(PAGE_QUEUE)
    public Queue page_queue(){
        return new Queue(PAGE_QUEUE);
    }

    @Bean
    public Binding UPDATE_EXCHANGE_QUEUE(@Qualifier(UPDATE_QUEUE) Queue queue,@Qualifier(UPDATE_EXCHANGE) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }
    @Bean(PULL_EXCHANGE)
    public Exchange pull_exchange(){
        return ExchangeBuilder.fanoutExchange(PULL_EXCHANGE).durable(true).build();
    }
    @Bean(PULL_QUEUE)
    public Queue pull(){
        return new Queue(PULL_QUEUE);
    }
    @Bean
    public Binding PULL_EXCHANGE_QUEUE(@Qualifier(PULL_QUEUE) Queue queue,@Qualifier(PULL_EXCHANGE) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }
    @Bean
    public Binding PAGE_EXCHANGE_QUEUE(@Qualifier(PAGE_QUEUE) Queue queue,@Qualifier(UPDATE_EXCHANGE) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }
}
