package com.dxlau.nettyapp.push.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by dxlau on 2017/5/23.
 */
public class PushServerInboundHandler extends ChannelDuplexHandler {
    private static final Logger LOG = LoggerFactory.getLogger(PushServerInboundHandler.class);
    private static final String SUCCESS_RESP = "{\"code\":0, \"msg\":\"ok\"}";
    private static final String WELCOME = "{\"code\":0, \"msg\":\"welcome\"}";

    // 存储所有已连接的客户端
    // 它本身是线程安全的
    private ChannelGroup allClients;

    PushServerInboundHandler(ChannelGroup allClients) {
        this.allClients = allClients;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf msgBytebuf = (ByteBuf) msg;
            CharSequence payload = msgBytebuf.getCharSequence(0, msgBytebuf.readableBytes(), CharsetUtil.UTF_8);
            LOG.info("receive {}.", payload);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 1.加入在线客户端列表
        // 2.发送欢迎语
        allClients.add(ctx.channel());

        ByteBuf resp = ctx.alloc().buffer();
        resp.writeCharSequence(WELCOME, CharsetUtil.UTF_8);
        ctx.writeAndFlush(resp);
        ReferenceCountUtil.refCnt(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("", cause);
        ctx.close();
    }
}
