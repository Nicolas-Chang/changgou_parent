package com.changgou.web.controller;

import com.changgou.order.feign.OrderFegin;
import com.changgou.order.pojo.Order;
import com.changgou.pay.feign.payFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping(value = "/wxpay")
public class VxPayConteoller {
    @Autowired
    private OrderFegin orderFegin;
    @Autowired
    private payFeign payFeign;

    @GetMapping
    public String naticePay(String orderId, Model model){

        Order order = orderFegin.findById(orderId).getData();
        if(order == null ){
            return "fail";       // 订单列表不存在
        }
        if(!"0".equals(order.getPayStatus())){
            return "fail";       // 不是未支付状态
        }
        Map map = payFeign.nativePay(orderId, order.getPayMoney()).getData();
        if(map == null){
            return "fail";
        }
        map.put("payMoney",order.getPayMoney());
        map.put("orderId",orderId);
        model.addAllAttributes(map);
        return "wxpay";
    }

    @RequestMapping("/toPaySuccess")
    public String toPaySuccess(Integer money,Model model){
        model.addAttribute("payMoney",money);
        return "ptusuccess";
    }

}
