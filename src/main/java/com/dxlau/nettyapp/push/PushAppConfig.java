package com.dxlau.nettyapp.push;

import com.dxlau.nettyapp.push.handler.PushChannelInitializer;
import com.dxlau.nettyapp.push.service.IPushService;
import com.dxlau.nettyapp.push.service.PushServiceImpl;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * Created by dxlau on 2017/5/23.
 */
@Configurable
@PropertySource("classpath:push-sysconfig.properties")
@ComponentScan("com.dxlau.nettyapp.push")
public final class PushAppConfig {
    private static final Logger LOG = LoggerFactory.getLogger(PushAppConfig.class);

    @Autowired
    Environment environment;

    @Bean
    public PushServer pushServer(ChannelInitializer pushChannelInitializer) {
        String host = environment.getProperty("push.server.host", "localhost");
        Integer port = Integer.valueOf(environment.getProperty("push.server.port", "9447"));
        return new PushServer(pushChannelInitializer, host, port);
    }

    @Bean
    public ChannelInitializer pushChannelInitializer(ChannelGroup channels) {
        return new PushChannelInitializer(channels);
    }

    @Bean
    public ChannelGroup channels() {
        return new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    @Bean
    public IPushService pushService(ChannelGroup channels) {
        return new PushServiceImpl(channels);
    }
}
