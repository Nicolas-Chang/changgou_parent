package com.changgou.web.controller;


import com.changgou.entity.Result;
import com.changgou.order.feign.CartFegin;
import com.changgou.order.feign.OrderFegin;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.changgou.user.feign.UserFeign;
import com.changgou.user.pojo.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/worder")
public class OrderController {


    @Autowired
    private UserFeign userFeign;

    @Autowired
    private OrderFegin orderFegin;

    @Autowired
    private CartFegin cartFegin;

    @GetMapping(value = "/order")
    public String function(Model model){
        List<Address> addressList = userFeign.findAddress().getData();

        for (Address address : addressList) {
            if("1".equals(address.getIsDefault())){
                model.addAttribute("deAddr",address);
            }
        }
        model.addAttribute("address",addressList);
        Map map = cartFegin.list();
        List<OrderItem> orderItemList = (List<OrderItem>) map.get("orderItemList");
        model.addAttribute("totalNum",(Integer)map.get("totalNum"));
        model.addAttribute("totalMoney",(Integer)map.get("totalMoney"));
        model.addAttribute("carts",orderItemList);

        return "order";

    }

    @ResponseBody
    @PostMapping(value = "/add")
    public Result add(@RequestBody Order order){
        Result result = orderFegin.add(order);
        return result;
    }

    @GetMapping(value = "/toPayPage")
    public String toPayPage(String orderId,Model model){
        Result<Order> result = orderFegin.findById(orderId);
        Order order = result.getData();
        model.addAttribute("orderId",orderId);
        model.addAttribute("payMoney",order.getPayMoney());
        return "pay";
    }



}
