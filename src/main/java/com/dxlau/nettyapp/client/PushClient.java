package com.dxlau.nettyapp.client;

import com.dxlau.nettyapp.utils.JsonHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dxlau on 2017/5/23.
 */
public class PushClient {
    public static final String HOST = "localhost";
    public static final int PORT = 9447;

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup(1);

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new JsonObjectDecoder());
                        pipeline.addLast(new PushClientHandler());
                    }
                });
        try {
            Channel ch = bootstrap.connect(HOST, PORT).sync().channel();
            ChannelFuture lastWriteFuture = null;

            System.out.println("Enter message (quit to end)");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (; ; ) {
                final String input = in.readLine();
                final String line = input != null ? input.trim() : null;
                if (line == null || "quit".equalsIgnoreCase(line)) { // EOF or "quit"
                    ch.close().sync();
                    break;
                } else if (line.isEmpty()) { // skip `enter` or `enter` with spaces.
                    continue;
                }
                // Sends the received line to the server.
                lastWriteFuture = ch.writeAndFlush(JsonHelper.toJson(message(line)));
                lastWriteFuture.addListener(future -> {
                    if (!future.isSuccess()) {
                        System.err.print("write failed: ");
                        future.cause().printStackTrace(System.err);
                    }
                });
            }
            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    // 创建push-client消息
    private static Map<String, Object> message(String msg) {
        Map<String, Object> frame = new HashMap<>();
        frame.put("from", "u000110086");
        frame.put("name", "定湘");
        frame.put("msg", msg);
        return frame;
    }

    static class PushClientHandler extends ChannelDuplexHandler {

        PushClientHandler() {
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof ByteBuf) {
                ByteBuf byteBufMsg = (ByteBuf) msg;
                CharSequence strMsg = byteBufMsg.getCharSequence(0, byteBufMsg.readableBytes(), Charset.forName("UTF-8"));
                System.out.println("Receive: " + strMsg);
            }
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            // TODO sth cool
            if (msg instanceof CharSequence) {
                ByteBuf buf = ctx.alloc().buffer();
                buf.writeCharSequence((CharSequence) msg, CharsetUtil.UTF_8);

                ctx.write(buf, promise);
                ReferenceCountUtil.refCnt(msg);
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
