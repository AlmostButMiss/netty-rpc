package com.netty.server.listener;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 柳忠国
 * @date 2020-05-26 10:42
 */
@Configuration
public class ServletConfig {

    @Bean
    public ServletListenerRegistrationBean<RequestLogListener> requestListener() {
        ServletListenerRegistrationBean<RequestLogListener> register = new ServletListenerRegistrationBean<>();
        register.setListener(new RequestLogListener());
        return register;
    }
}