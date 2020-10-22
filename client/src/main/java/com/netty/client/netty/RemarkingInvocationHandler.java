package com.netty.client.netty;

import entity.Constants;
import entity.Invocation;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.netty.client.netty.BizHandler.doBiz;
import static com.netty.client.netty.SendMsgHandler.getRegistryInfo;
import static com.netty.client.netty.WisdomClientBootstrap.doConnect;


/**
 * @author 柳忠国
 * @date 2020-07-17 15:24
 */
@Slf4j
public class RemarkingInvocationHandler extends SimpleChannelInboundHandler<Invocation> {

    /**
     * key -> service name
     * value -> channel
     */
    public static final Map<String, ChannelHandlerContext> CHANNEL_MAP = new ConcurrentHashMap<>();

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Invocation invocation) throws Exception {
        switch (invocation.getMessageType()) {
            case Constants.REGISTRY:
                log.info("#######服务端发送过来的service-name:{}", invocation.getResult());
                // 保存channel对象
                CHANNEL_MAP.put((String) invocation.getResult(), ctx);
                break;
            case Constants.BIZ_REQUEST:
                log.info("###### 来自服务端端请求 ：{}", invocation);
                executorService.execute(() -> doBiz(ctx, invocation));
                break;
            default:
                log.info("####未知的请求类型##### :{}", invocation.getMessageType());
                break;
        }
    }
    /**
     * 连接建立起来后需要上报学校id
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("###### 与服务端建立好了连接...:{}", ctx.channel().remoteAddress());
        ctx.writeAndFlush(getRegistryInfo());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("###### 与服务端端开了连接...:{}", ctx.channel().remoteAddress());
        CHANNEL_MAP.remove(Constants.WISDOM_ELECTRIC_SERVICE_NAME);
        reConnect();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("##### 与服务端通信出现异常 ####### :{}", cause.getMessage());
        //reConnect();
    }

    private void reConnect() {
        doConnect();
    }
}
