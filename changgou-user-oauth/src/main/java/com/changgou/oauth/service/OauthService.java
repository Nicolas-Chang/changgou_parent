package com.changgou.oauth.service;

import com.changgou.oauth.util.AuthToken;

public interface OauthService {

    AuthToken getToken(String username,String password,String clientId,String clientSecret);

}
