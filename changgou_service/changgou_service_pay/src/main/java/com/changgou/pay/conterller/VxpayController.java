package com.changgou.pay.conterller;


import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.pay.config.RabbitMQConfig;
import com.changgou.pay.service.VxpayService;
import com.changgou.utlis.ConvertUtils;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.github.wxpay.sdk.WXPayXmlUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/wxpay")
@RestController
public class VxpayController {

    @Autowired
    private VxpayService vxpayService;



    @Autowired
    private RabbitTemplate rabbitTemplate;
    @GetMapping(value = "/nativePay")
    public Result<Map> nativePay(@RequestParam("orderId") String orderId, @RequestParam("money") Integer money) {
        Map map = vxpayService.nativePay(orderId, money);
        System.out.println(map);
        return new Result(true, StatusCode.OK,"支付成功",map);
    }

    @RequestMapping(value = "notify")
        public void queryOrder(HttpServletRequest request, HttpServletResponse response){
        System.out.println("支付成功回调");
        try {
            ServletInputStream inputStream = request.getInputStream();
            String xml = ConvertUtils.convertToString(inputStream);
            System.out.println(xml);
            Map<String, String> map = WXPayUtil.xmlToMap(xml);
            //查询订单返回项
            if(map != null && map.size() > 0){
                String orderId = map.get("out_trade_no");  //获取id
                Map map1 = vxpayService.queryOrder(orderId);
                System.out.println("返回结果"+map1);
                System.out.println("map1" + map1);
                //如果查询是SUCCESS 则通过rabbitmq发送信息
                if("SUCCESS".equals(map1.get("result_code"))){
                    Map orderMap = new HashMap();
                    orderMap.put("orderId",map1.get("out_trade_no"));
                    orderMap.put("transactionId",map1.get("transaction_id"));
                    rabbitTemplate.convertAndSend("", RabbitMQConfig.QUEUE, JSON.toJSONString(orderMap));
                    rabbitTemplate.convertAndSend("paynotify","",map1.get("out_trade_no"));
                    //返回给微信端一个响应
                    response.setContentType("text/xml");
                    String data = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
                    response.getWriter().write(data);
                }else {
                    System.err.println("错误信息"+map1.get("err_code_des"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PutMapping (value = "/closeOrder/{orderId}")
    public Result<Map> closeOrder(@PathVariable String orderId){
        Map map = vxpayService.orderClose(orderId);
        return new Result(true,StatusCode.OK,"关闭成功",map);
    };
    @PutMapping (value = "/queryOrder1/{orderId}")
    public Result<Map> queryOrderA(@PathVariable String orderId){
        Map map = vxpayService.queryOrder(orderId);
        return new Result(true,StatusCode.OK,"查询成功",map);
    };

}
