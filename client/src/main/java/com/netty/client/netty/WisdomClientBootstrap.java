package com.netty.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * @author 柳忠国
 * @date 2020-07-17 15:16
 */
@Component
@EnableAsync
@Slf4j
public class WisdomClientBootstrap implements CommandLineRunner {

    private static int port = 8383 ;
    private static String host = "127.0.0.1";

    //@Value("${netty.server.host:127.0.0.1")
    public void setHost(String host) {
        WisdomClientBootstrap.host = host;
    }

    //@Value("${netty.server.port:8383}")
    public void setPort(int port) {
        WisdomClientBootstrap.port = port;
    }

    private static Bootstrap bootstrap;
    private static Channel channel;

    private void startClient() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);

        bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(host, port)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                                .addLast(new ObjectEncoder())
                                .addLast(new RemarkingInvocationHandler())
                        ;
                    }
                });
        doConnect();
    }

    protected static void doConnect() {
        log.info("=====channel======{}", channel);
        if (channel != null && channel.isActive()) {
            log.info("=====channel======{},{}", channel, channel.isActive());
            return;
        }
        ChannelFuture future = bootstrap.connect(host, port);

        future.addListener((ChannelFutureListener) futureListener -> {
            if (futureListener.isSuccess()) {
                channel = futureListener.channel();
                log.info("Connect to server successfully!");
            } else {
                log.info("Failed to connect to server, try connect after 10s,host:{},port{}", host, port);
                try {
                    futureListener.channel().eventLoop().schedule(WisdomClientBootstrap::doConnect, 10, TimeUnit.SECONDS);
                } catch (Exception e) {
                    log.info("连接服务器报错[{}]", e.getMessage());
                }
            }
        });
        try {
            future.sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.info("#######InterruptedException :{}", e.getMessage());
        }
    }

    @Async
    @Override
    public void run(String... args) {
        try {
            startClient();
        } catch (Exception e) {
            log.info("####### error :{}", e.getMessage());
        }
    }
}
