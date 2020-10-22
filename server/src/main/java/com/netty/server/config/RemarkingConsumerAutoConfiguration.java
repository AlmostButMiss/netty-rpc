package com.netty.server.config;

import com.netty.server.annotation.RemarkingReference;
import com.netty.server.netty.RemarkingConsumerInvocationHandler;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * @author 柳忠国
 * @date 2020-07-03 18:12
 */
@Component
public class RemarkingConsumerAutoConfiguration implements BeanFactoryPostProcessor {

    private ConfigurableListableBeanFactory beanFactory;

    /**
     * Spring容器初始化Bean之前，把被@RemarkingReference标注的属性通过反射的方式自动注入到Spirng容器中
     * {@link RemarkingConsumerAutoConfiguration#getOrCreate(Field)}
     * @return
     */
    @Bean
    public BeanPostProcessor beanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

                Class<?> objClz;
                if (AopUtils.isAopProxy(bean)) {
                    objClz = AopUtils.getTargetClass(bean);
                } else {
                    objClz = bean.getClass();
                }

                ReflectionUtils.doWithFields(objClz,
                        field -> {
                            Object obj = getOrCreate(field);
                            field.setAccessible(true);
                            field.set(bean, obj);
                        },
                        field -> field.getAnnotation(RemarkingReference.class) != null);
                return bean;
            }
        };
    }

    private Object getOrCreate(Field field) {

        String beanName = ClassUtils.getShortName(field.getType());

        if (!beanFactory.containsSingleton(beanName)) {
            Object obj = Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class[]{field.getType()},
                    new RemarkingConsumerInvocationHandler(field.getType()));

            beanFactory.registerSingleton(beanName, obj);

            return obj;
        }
        return beanFactory.getBean(beanName);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}