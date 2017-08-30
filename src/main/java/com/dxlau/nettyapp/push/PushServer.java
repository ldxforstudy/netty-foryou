package com.dxlau.nettyapp.push;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Created by dxlau on 2017/5/23.
 */
public final class PushServer {
    private static final Logger LOG = LoggerFactory.getLogger(PushServer.class);
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9447;
    private static final int BOSS_NUM = 1;
    private static final int WORKER_NUM = Runtime.getRuntime().availableProcessors() / 2;

    private String host;
    private int port;
    private int bossNum;
    private int workerNum;
    private ChannelInitializer channelInitializer;

    PushServer() {
        this((ChannelInitializer) null, HOST, PORT);
    }

    PushServer(ChannelInitializer channelInitializer, String host, int port) {
        if (channelInitializer == null) {
            throw new NullPointerException("channelInitializer must not null");
        }

        if (host == null || host.isEmpty()) {
            host = HOST;
        }
        if (PORT <= 1024) {
            port = PORT;
        }
        this.host = host;
        this.port = port;
        this.bossNum = BOSS_NUM;
        this.workerNum = WORKER_NUM;
        this.channelInitializer = channelInitializer;
    }

    public void doIt() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(bossNum);
        EventLoopGroup workGroup = new NioEventLoopGroup(workerNum);
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(channelInitializer);
        try {
            ChannelFuture f = bootstrap.bind(host, port).sync();
            LOG.info("################################");
            LOG.info("# run server on {}:{} #", host, port);
            LOG.info("################################");

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("bind {}:{} fail!", host, port);
            LOG.error("", e);
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
