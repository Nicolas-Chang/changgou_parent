package com.changgou.oauth;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TokenTest {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;


    @Test
    public void getToken(){
        //从eureka获取客户端信息 url
        ServiceInstance instance = loadBalancerClient.choose("USER-AUTH");
        URI uri = instance.getUri();
        String url = uri + "/oauth/token";
        //封装头信息
        MultiValueMap<String,String> hearders = new LinkedMultiValueMap<>();
        //对 客户端密码账号进行basic 64进行加密

        hearders.add("Authorization",httpBasic("changgou","changgou"));
        //指定认证类型,密码,账号
        MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("grant_type","password");
        body.add("userName","itheima");
        body.add("password","itheima");
        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = new HttpEntity<MultiValueMap<String, String>>(body, hearders);

        //指定restTemplat遇到400,401时不要抛出来
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError( ClientHttpResponse response) throws IOException {
                if(response.getRawStatusCode() != 400 || response.getRawStatusCode() != 401){
                        super.handleError(response);//如果不是则放行
                }
            }
        });

        ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, multiValueMapHttpEntity, Map.class);
        Map map = exchange.getBody();
        System.out.println(map);

    }

    private String httpBasic(String clientId, String clientSecret) {
        String value =clientId+":"+clientSecret;
        byte[] encode = Base64Utils.encode(value.getBytes());
        return "Basic "+new String(encode);
    }
}
