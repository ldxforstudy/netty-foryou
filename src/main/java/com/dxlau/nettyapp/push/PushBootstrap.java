package com.dxlau.nettyapp.push;

import com.dxlau.nettyapp.push.service.IPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by dxlau on 2017/5/23.
 */
public final class PushBootstrap {
    private static final Logger LOG = LoggerFactory.getLogger(PushBootstrap.class);

    public static void main(String[] args) {
        LOG.info("##############################");
        LOG.info("#### running push-server :P ##");
        LOG.info("##############################");
        ApplicationContext ctx = new AnnotationConfigApplicationContext(PushAppConfig.class);
        PushServer pushServer = (PushServer) ctx.getBean("pushServer");

        // 1. Mock
        IPushService pushService = (IPushService) ctx.getBean("pushService");
        pushService.mock();

        // 2. 启动Push服务
        pushServer.doIt();
    }
}
