package com.dxlau.nettyapp.push.service;

/**
 * Created by dxlau on 2017/8/30.
 */
public interface IPushService {

    /**
     * 广播
     *
     * @param msg
     * @return
     */
    boolean broadcast(String msg);

    void mock();
}
