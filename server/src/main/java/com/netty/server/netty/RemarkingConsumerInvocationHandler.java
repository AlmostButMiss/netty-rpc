package com.netty.server.netty;

import com.netty.server.annotation.RemarkingReference;
import entity.Constants;
import entity.Invocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static com.netty.server.listener.RequestLogListener.SCHOOL_ID_THREAD_LOCAL;
import static com.netty.server.netty.RemarkingInvocationHandler.CHANNEL_MAP;
import static com.netty.server.netty.RemarkingInvocationHandler.RESPONSE_MAP;


/**
 * @author 柳忠国
 * @date 2020-07-03 20:21
 */
@Slf4j
public class RemarkingConsumerInvocationHandler implements InvocationHandler {

    /**
     * 等待客户端的响应时间
     */
    private static final long WAIT_MILLS = 3000L;

    private Class<?> clazz;

    private static final String BEAN_NAME_SUFFIX = "Impl";

    public RemarkingConsumerInvocationHandler(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * 被{@link RemarkingReference}标注到属性，会生成代理对象，实际会通过反射到方式调用
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //一般DEBUG时，我们查看变量时，会调用其toString()方法，由于该方法也会被代理，所以出现递归调用，
        //这里为了避免DEBUG时，重复调用方法，所以作特殊处理
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        Invocation invocation = new Invocation();
        invocation.setMessageType(Constants.BIZ_REQUEST);
        invocation.setRequestId(System.nanoTime());
        invocation.setMethodName(method.getName());
        invocation.setTargetBeanClass(method.getDeclaringClass());
        invocation.setTargetClassBeanName(getBeanName(method));
        invocation.setParameters(args);

        CHANNEL_MAP.get(SCHOOL_ID_THREAD_LOCAL.get()).writeAndFlush(invocation);

        return getResponse(invocation.getRequestId());
    }

    /**
     * 获取客户端的响应
     * @param requestId
     * @return
     */
    private Object getResponse(Long requestId) {
        long begin = System.currentTimeMillis();
        while (!RESPONSE_MAP.containsKey(requestId)) {
            long current = System.currentTimeMillis();
            if (current - begin > WAIT_MILLS) {
                break;
            }
        }
        // 如果超时还未获取到响应，目前返回null
        Object result = RESPONSE_MAP.getOrDefault(requestId, null);
        RESPONSE_MAP.remove(requestId);
        return result;
    }

    /**
     * 获取beanName 比如AirModelStatusService接口生成的beanName就是airModelStatusServiceImpl
     * 此beanName会发送到客户端，用于反射调用
     * @param method
     * @return
     */
    private String getBeanName(Method method) {
        return ClassUtils
                .getShortName(method.getDeclaringClass())
                .substring(0, 1).toLowerCase()
                .concat(ClassUtils.getShortName(method.getDeclaringClass()).substring(1))
                .concat(BEAN_NAME_SUFFIX);
    }
}