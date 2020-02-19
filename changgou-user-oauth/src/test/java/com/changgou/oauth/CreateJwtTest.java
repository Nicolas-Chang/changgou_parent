package com.changgou.oauth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;


public class CreateJwtTest {



    String key_location="changgou.jks";
    //秘钥库密码
    String key_password="changgou";
    //秘钥密码
    String keypwd = "changgou";
    //秘钥别名
    String alias = "changgou";

    @Test
    public void test1(){
        //访问证书路径
        ClassPathResource classPathResource = new ClassPathResource(key_location);

        //创建密钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(classPathResource,key_password.toCharArray());

        //读取密钥对
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias,keypwd.toCharArray());

        //获取私钥
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        //定义payLoad
        Map<String, Object> tokenMap = new HashMap<>();

        tokenMap.put("id","1");
        tokenMap.put("name","itheima");
        tokenMap.put("roles", "ROLE_VIP,ROLE_USER");
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(tokenMap),new RsaSigner(rsaPrivateKey));


        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }






}
