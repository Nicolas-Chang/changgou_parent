package com.changgou.order.controller;


import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.order.config.TokenDecode;
import com.changgou.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/cart")
public class CartController {


    @Autowired
    private CartService cartService;

    @Autowired
    private TokenDecode tokenDecode;

    @GetMapping("/add")
    public Result add(@RequestParam("skuId") String skuId,@RequestParam("num") Integer num){
        String username = tokenDecode.getUserInfo().get("username");
        cartService.add(skuId,username,num);
                return new Result(true, StatusCode.OK,"添加成功");
    }
    @GetMapping(value = "/list")
    public Map list(){
        String username = tokenDecode.getUserInfo().get("username");
        Map list = cartService.list(username);
        return list;
    }
}
