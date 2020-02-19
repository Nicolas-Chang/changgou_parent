package com.changgou.filter;

import com.changgou.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    private static final String LOGIN_URL="http://localhost:8001/api/oauth/toLogin";

    @Autowired
    private AuthService authService;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //判断路径是否是登录lujing
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
        if ("/api/oauth/login".equals(path)|| "/api/oauth/toLogin".equals(path) || !UrlFilter.hasAuthorize(path) ){
            //直接放行
            return chain.filter(exchange);
        }
        String jti = authService.jtiForCookie(request);
        if (StringUtils.isEmpty(jti)){

              return   this.toLoginPage(LOGIN_URL,exchange);
        }
        String jwt = authService.getReidsJti(jti);
        if (StringUtils.isEmpty(jwt)){
/*            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();*/
             return    this.toLoginPage(LOGIN_URL + "?FROM=" + request.getURI().getPath(),exchange);
        }
        request.mutate().header("Authorization","Bearer "+jwt);
        return chain.filter(exchange);
    }
    //跳转登录页面
    private Mono<Void> toLoginPage(String loginUrl, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.SEE_OTHER);
        response.getHeaders().set("Location",loginUrl);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
