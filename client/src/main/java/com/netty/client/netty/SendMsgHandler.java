package com.netty.client.netty;

import entity.Constants;
import entity.Invocation;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

import static com.netty.client.netty.RemarkingInvocationHandler.CHANNEL_MAP;


/**
 * 一个发送数据给云端服务器的处理类
 * @author 柳忠国
 * @date 2020-07-28 13:54
 */
@Component
@Slf4j
public class SendMsgHandler {

    /**
     * 一个发送请求到云端服务器的接口
     * {@link Constants} 可以补充请求类型
     *
     * 生成dingidng对象
     * 比如发送钉钉相关钉信息
     * invocation.setMessageType(Constants.DINGDING);
     * invocation.setResult(new DingDing());
     *
     * @param invocation
     */
    public static void sendMsg(Invocation invocation) {

        ChannelHandlerContext channelHandlerContext = CHANNEL_MAP.get(Constants.WISDOM_ELECTRIC_SERVICE_NAME);

        if (null != channelHandlerContext && channelHandlerContext.channel().isActive()) {
            channelHandlerContext.writeAndFlush(invocation);
        } else {
            log.info("########发送消息到服务端失败，原因: 云端不在线或连接通道未激活....");
        }
    }

    public static Invocation getRegistryInfo() {
        Invocation invocation = new Invocation();
        // 调试时使用的 school-1
        invocation.setSchoolId("school-1");
        invocation.setMessageType(Constants.REGISTRY);
        return invocation;
    }

}
