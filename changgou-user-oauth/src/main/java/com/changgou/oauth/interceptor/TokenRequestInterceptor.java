package com.changgou.oauth.interceptor;

import com.changgou.oauth.util.AdminToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        /**
         * 从数据库加载查询用户信息
         * 1.没用令牌,Feign调用之前,生成令牌(admin)
         * 2.Feign调用之前,令牌存放到Header文件中
         * 3.请求->feign调用->拦截器RequestInterceptor->Feign调用之前执行拦截
         */
        //生成admin令牌
        String token = AdminToken.getAdminToken();
        requestTemplate.header("Authorization", "bearer " + token);

    }
}
