package com.changgou.oauth.controller_20190929_105615;


import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.oauth.service.OauthService;
import com.changgou.oauth.util.AuthToken;
import com.changgou.oauth.util.CookieUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.print.attribute.standard.RequestingUserName;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/oauth")
public class AuthController {

    @Autowired
    private OauthService oauthService;

    @Value("${auth.clientId}")
    private String clientId;

    @Value("${auth.clientSecret}")
    private String clientSecret;

    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;

    @RequestMapping("/toLogin")
    public String toLogin(@RequestParam(value = "FROM",required = false,defaultValue = "") String from, Model model){
        model.addAttribute("from",from);
        return "login1";
    }
    @ResponseBody
    @PostMapping("/login")
    public Result Login( String username,String password,HttpServletResponse response){

        if(StringUtils.isEmpty(username)){
            throw new RuntimeException("用户名不存在");
        }
        if(StringUtils.isEmpty(password)){
            throw new RuntimeException("密码问题");
        }
             AuthToken token = oauthService.getToken(username, password, clientId, clientSecret);
        this.savecookJtl(token.getJti(),response);
        return new Result(true, StatusCode.OK,"登录成功",token.getJti());

    }

    private void savecookJtl(String jti,HttpServletResponse response) {
        CookieUtil.addCookie(response,cookieDomain,"/","uid",jti,cookieMaxAge,false);
    }


}
