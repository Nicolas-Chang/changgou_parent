package com.changgou.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;


@Component
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(requestAttributes != null){
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            if(request != null){
                Enumeration<String> headerNames = request.getHeaderNames();
                if(headerNames != null){
                    while (headerNames.hasMoreElements()){
                        String headername = headerNames.nextElement();
                        if(headername.equals("authorization")){
                            String header = request.getHeader(headername);
                            template.header(headername,header);
                        }
                    }
                }
            }
        }
    }
}
