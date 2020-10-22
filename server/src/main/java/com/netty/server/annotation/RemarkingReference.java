package com.netty.server.annotation;

import java.lang.annotation.*;

/**
 *
 * 一个功能类似于@Autowired的注解，用于依赖注入的
 * 底层实现是基于动态代理的{@link RemarkingConsumerAutoConfiguration#beanPostProcessor()}
 *
 * @author 柳忠国
 * @date 2020-07-03 18:11
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RemarkingReference {
}