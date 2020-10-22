package com.netty.client.netty;

import entity.Constants;
import entity.Invocation;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.MethodUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;


/**
 * @author 柳忠国
 * @date 2020-07-17 17:24
 */
@Component
@Slf4j
public class BizHandler implements BeanFactoryAware {

    private static BeanFactory beanFactory;

    /**
     * 接收服务端发送过来到请求后，解析Invocation进行反射调用并响应结果给服务端
     * @param ctx
     * @param invocation
     */
    public static void doBiz(ChannelHandlerContext ctx, Invocation invocation) {

        try {
            Object targetObj = beanFactory.getBean(invocation.getTargetClassBeanName());
            log.info("##### targetObj :{}", targetObj);
            Object result = MethodUtils
                    .invokeMethod(
                            targetObj,
                            invocation.getMethodName(),
                            invocation.getParameters());
            log.info("#######准备响应服务端:{}", result);

            invocation.setResult(result);
            invocation.setMessageType(Constants.BIZ_RESPONSE);
            ctx.writeAndFlush(invocation);
        } catch (Exception e) {
            // log.info("########远程调用失败:{}", errorTrackSpace(e));
            log.info("########远程调用失败:{}", e.getMessage());
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        BizHandler.beanFactory = beanFactory;
    }
}
