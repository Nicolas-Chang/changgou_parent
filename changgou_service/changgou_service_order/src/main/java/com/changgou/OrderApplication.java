package com.changgou;


import com.changgou.order.config.TokenDecode;
import com.changgou.order.util.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

import javax.swing.*;

@SpringBootApplication
@EnableEurekaClient
@EnableScheduling  // 开启自动任务执行
@MapperScan(basePackages = {"com.changgou.order.dao"})
@EnableFeignClients(basePackages = {"com.changgou.goods.feign","com.changgou.user.feign","com.changgou.pay.feign"})
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run( OrderApplication.class);
    }

    @Bean
    public TokenDecode tokenDecode(){
        return  new TokenDecode();
    }

    @Bean
    public IdWorker idWorker(){
        return new IdWorker(1,1);
    }

}
