package com.dxlau.nettyapp.push.service;

import com.dxlau.nettyapp.utils.JsonHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 推送服务提供的功能.
 * <ul>
 * <li>1.将消息广播给所有在线客户端</li>
 * <li>2.fuck sth</li>
 * </ul>
 * <p>
 * Created by dxlau on 2017/8/30.
 */
public class PushServiceImpl implements IPushService {
    private static final Logger LOG = LoggerFactory.getLogger(PushServiceImpl.class);
    EventExecutorGroup executors;
    ChannelGroup allClients;

    public PushServiceImpl(ChannelGroup channels) {
        executors = new DefaultEventExecutorGroup(1);
        allClients = channels;
    }

    @Override
    public boolean broadcast(String msg) {
        if (allClients.isEmpty()) {
            LOG.info("We haven't accept any remote-client");
            return false;
        }

        // 基于JSON格式进行通讯
        LOG.info("We already accept {} active-clients.", allClients.size());
        Map<String, Object> payload = new HashMap<>();
        payload.put("code", 0);
        payload.put("msg", msg);

        ByteBuf resp = Unpooled.buffer();
        resp.writeCharSequence(JsonHelper.toJson(payload), CharsetUtil.UTF_8);
        allClients.write(resp);

        // 释放资源
        ReferenceCountUtil.refCnt(resp);

        return true;
    }

    /**
     * 模拟运行.
     * 每隔20秒广播一次信息
     */
    public void mock() {
        final String msg = "Cool";
        executors.scheduleAtFixedRate(() -> {
            LOG.debug("We plan to broadcast message to all active clients!");
            broadcast(msg);
        }, 5L, 5L, TimeUnit.SECONDS);
    }
}
