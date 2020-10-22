package com.netty.server.netty;

import entity.Constants;
import entity.Invocation;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 柳忠国
 * @date 2020-07-17 15:24
 */
@Slf4j
public class RemarkingInvocationHandler extends SimpleChannelInboundHandler<Invocation> {

    public static final Map<String, ChannelHandlerContext> CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 存放客户端的发送过来的响应数据
     * key -> {@link Invocation#requestId}
     * value -> {@link Invocation#result}
     */
    public static final Map<Long, Object> RESPONSE_MAP = new ConcurrentHashMap<>();

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    /**
     * 处理客户端的请求 {@link Constants}
     *
     * @param ctx
     * @param invocation
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Invocation invocation) throws Exception {

        switch (invocation.getMessageType()) {
            case Constants.BIZ_RESPONSE:
                RESPONSE_MAP.put(invocation.getRequestId(), invocation.getResult());
                break;
            case Constants.REGISTRY:
                log.info("#######客户端注册成功,学校id:{}", invocation.getSchoolId());
                CHANNEL_MAP.put(invocation.getSchoolId(), ctx);
                break;
            case Constants.DINGDING:
                log.info("#######客户端钉钉请求 {}", invocation.getResult());
                // todo 需要保存钉钉相关钉信息
                // invocation.getResult()
                // EXECUTOR.execute(() -> saveDingding(invocation));
                break;
            default:
                log.info("####未知的请求类型##### :{}", invocation.getMessageType());
                break;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("###### 与客户端建立好了连接...:{}", ctx.channel().remoteAddress());
        log.info("###### 下发service-name...:{}, 目标主机地址:{}", Constants.WISDOM_ELECTRIC_SERVICE_NAME, ctx.channel().remoteAddress());
        Invocation invocation = new Invocation();
        invocation.setMessageType(Constants.REGISTRY);
        invocation.setResult(Constants.WISDOM_ELECTRIC_SERVICE_NAME);
        ctx.writeAndFlush(invocation);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("##### 与客户端通信异常 ####### :{}", cause.getMessage());
    }
}
