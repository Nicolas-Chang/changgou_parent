package com.changgou.pay.service.impl;

import com.changgou.pay.service.VxpayService;
import com.github.wxpay.sdk.WXPay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Service
public class VxpayServiceImpl implements VxpayService {
    @Autowired
    private WXPay wxPay;

    @Value( "${wxpay.notify_url}" )
    private String url;

    @Override
    public Map nativePay(String orderId, Integer money) {
        try {
            Map<String,String> map = new HashMap<>();
            map.put("body","畅购");//商品描述
            map.put("out_trade_no",orderId);//订单号
            BigDecimal decimal = new BigDecimal("0.01");
            BigDecimal fen = decimal.multiply(new BigDecimal("100"));
            fen = fen.setScale(0, BigDecimal.ROUND_UP);
            map.put("total_fee",String.valueOf(fen));
            map.put("spbill_create_ip","127.0.0.1");//终端ip
            map.put("notify_url",url);//回调地址
            map.put("trade_type","NATIVE");  //交易类型
            Map<String, String> result = wxPay.unifiedOrder(map);
            System.out.println(result);
            return  result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Map queryOrder(String orderId) {

        try {
            Map<String,String> map = new HashMap<>();
            map.put("out_trade_no",orderId);
            Map<String, String> resultmap = wxPay.orderQuery(map);
            return resultmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 关闭订单
     * @param orderId
     * @return
     */
    @Override
    public Map orderClose(String orderId) {
        try {
            Map<String,String> map = new HashMap<>();
            map.put("out_trade_no",orderId);
            Map<String, String> resultMao = wxPay.closeOrder(map);
            System.out.println("关闭数据后返回的是这个" + resultMao);
            return resultMao;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
