package com.dxlau.nettyapp.push.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Channel初始器
 * Created by dxlau on 2017/5/23.
 */
public class PushChannelInitializer extends ChannelInitializer<SocketChannel> {

    ChannelGroup channels;

    public PushChannelInitializer(ChannelGroup channels) {
        this.channels = channels;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new JsonObjectDecoder());
        pipeline.addLast(new PushServerInboundHandler(channels));
    }
}
