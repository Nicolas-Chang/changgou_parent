package com.changgou.web.controller;


import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.order.feign.CartFegin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/wcart")
public class CartController {


    @Autowired
    private CartFegin cartFegin;

    private String url = "cart";
    /**
     * 查询
     * @param model
     * @return
     */
    @GetMapping(value = "/list")
    public String list(Model model){
        Map list = cartFegin.list();
        model.addAttribute("items",list);
        return url;
    }

    @GetMapping(value = "/add")
    @ResponseBody
    public Result add(String id,Integer num){
        cartFegin.add(id,num);
        Map map = cartFegin.list();
        return new Result(true, StatusCode.OK,"添加购物车成功",map);
    }
}
