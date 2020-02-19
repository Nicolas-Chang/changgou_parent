package com.changgou.pay.service;


import java.util.Map;

public interface VxpayService {

    Map nativePay(String orderId , Integer money);


    Map queryOrder(String orderId);


    Map orderClose(String orderId);

}
