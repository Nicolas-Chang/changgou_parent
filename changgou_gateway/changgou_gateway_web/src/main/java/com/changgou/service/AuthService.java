package com.changgou.service;

import com.sun.org.apache.regexp.internal.RE;
import org.aspectj.weaver.patterns.ITokenSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthService {


    @Autowired
    private StringRedisTemplate redisTemplate;


    //判断cookie 中的jtl是否存在

    public String jtiForCookie(ServerHttpRequest request){
        HttpCookie uid = request.getCookies().getFirst("uid");
        if (uid != null){
            return uid.getValue();
        }
        return null;
    }

    public String getReidsJti(String jti){
        String token = redisTemplate.boundValueOps(jti).get();
        return token;
    }


    //判断redis中的jtl是否过期
}
