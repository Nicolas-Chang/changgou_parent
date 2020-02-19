package com.changgou.oauth.service.impl;

import com.changgou.oauth.service.OauthService;
import com.changgou.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.rmi.MarshalledObject;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class OauthServiceImpl implements OauthService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${auth.ttl}")
    private long ttl;
    @Override
    public AuthToken getToken(String username, String password, String clientId, String clientSecret) {


        //获取路径
        ServiceInstance choose = loadBalancerClient.choose("USER-AUTH");
        URI uri = choose.getUri();
        String url = uri + "/oauth/token";

        //头信息
        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization",httpbase64(clientId,clientSecret));
        //添加账户信息
        MultiValueMap<String,String> boday = new LinkedMultiValueMap<>();
        boday.add("grant_type","password");
        boday.add("username",username);
        boday.add("password",password);

        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(boday,headers);
        ResponseEntity<Map> exchange = restTemplate.exchange(url,HttpMethod.POST, entity, Map.class);
        Map map = exchange.getBody();
        if(map.get("access_token") == null || map.get("refresh_token")==null || map.get("jti")==null || map == null){
            throw new RuntimeException("令牌申请失败");
        }

        AuthToken authToken = new AuthToken();
        authToken.setAccessToken((String) map.get("access_token"));
        authToken.setRefreshToken((String)map.get("refresh_token"));
        authToken.setJti((String) map.get("jti"));

        stringRedisTemplate.boundValueOps(authToken.getJti()).set(authToken.getAccessToken(),ttl, TimeUnit.SECONDS);

        return authToken;
    }

    private String httpbase64(String clientId, String clientSecret) {
        String s = clientId + ":" + clientSecret;
        byte[] encode = Base64Utils.encode(s.getBytes());
        return "Basic " + new String(encode);
    }
}
