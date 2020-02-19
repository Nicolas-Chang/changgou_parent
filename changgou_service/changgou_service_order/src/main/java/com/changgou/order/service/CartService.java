package com.changgou.order.service;

import java.util.Map;

public interface CartService {

    void add(String skuId,String username,Integer num);

    public Map list(String username);

}
