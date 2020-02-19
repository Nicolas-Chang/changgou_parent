package com.changgou.oauth;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

public class ParseJwtTest {

    @Test
    public void test1(){
        //私钥
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU4MTEwNjI4MSwiYXV0aG9yaXRpZXMiOlsiYWNjb3VudGFudCIsInVzZXIiLCJzYWxlc21hbiJdLCJqdGkiOiI0Y2MzNDY1OC00ZWNjLTQ2OTEtOTdkNC1hMmE4OTFhZmY2MmQiLCJjbGllbnRfaWQiOiJjaGFuZ2dvdSIsInVzZXJuYW1lIjoiaGVpbWEifQ.vCnCmBOZO1XB6cYhM2NOxwUzHkcasOMdZdWRnuF7QZ879YKkfFpTjV8VcU3FY76QHvkQsXaMho27Q1OkZWZWMpgnNBl4j_Hl0EYwOLn6al2LvPG0Iq-WyXS4zkL3z5NM4rFSxT_QUT2PmpV6bXW588KhbBnd9vkfczMo8d3mxotnkCc64gsSUVse3m-f0YZxy1Bz6ZK0-v67R-mXU2J-43muoKlFUfZCwl7Z23Kh-30oR8hk1FwgQy5zItjwsFUZbOMto60ruLnlCiPCGmIRWAfzqt4tsc3Mw3hEI7Ep5k8LUs4lpclFH1zaUDaUh19VujpWEa3rnEkheMeFC7R01A";

        //公钥
        String publictoken = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvFsEiaLvij9C1Mz+oyAmt47whAaRkRu/8kePM+X8760UGU0RMwGti6Z9y3LQ0RvK6I0brXmbGB/RsN38PVnhcP8ZfxGUH26kX0RK+tlrxcrG+HkPYOH4XPAL8Q1lu1n9x3tLcIPxq8ZZtuIyKYEmoLKyMsvTviG5flTpDprT25unWgE4md1kthRWXOnfWHATVY7Y/r4obiOL1mS5bEa/iNKotQNnvIAKtjBM4RlIDWMa6dmz+lHtLtqDD2LF1qwoiSIHI75LQZ/CNYaHCfZSxtOydpNKq8eb1/PGiLNolD4La2zf0/1dlcr5mkesV570NxRmU1tFm8Zd3MZlZmyv9QIDAQAB-----END PUBLIC KEY-----";

        Jwt jwt = JwtHelper.decodeAndVerify(token,new RsaVerifier(publictoken));
        String claims = jwt.getClaims();
        System.out.println(claims);
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
