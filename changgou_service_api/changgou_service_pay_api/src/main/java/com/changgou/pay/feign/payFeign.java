package com.changgou.pay.feign;


import com.changgou.entity.Result;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@FeignClient(name = "pay")
public interface payFeign {
    @GetMapping(value = "/wxpay/nativePay")
    public Result<Map> nativePay(@RequestParam("orderId") String orderId, @RequestParam("money") Integer money);

/*    @RequestMapping(value = "/wxpay/notify")
    public void queryOrder(HttpServletRequest request, HttpServletResponse response);*/

    @PutMapping(value = "/wxpay/closeOrder/{{orderId}")
    public Result<Map> closeOrder(@PathVariable("orderId") String orderId);

    @PutMapping(value = "/wxpay/queryOrder1/{orderId}")
    public Result<Map> queryOrderA(@PathVariable("orderId") String orderId);


}
