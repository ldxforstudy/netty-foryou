package com.dxlau.nettyapp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by dxlau on 2017/1/23.
 */
public class ByteBufQ {
    private static final String BYTEBUF_OUT_FORMAT = "ByteBuf==》 readerIndex: %d, writerIndex: %d, capacity: %d";

    public static void main(String[] args) {
        ByteBuf byteBuf1 = Unpooled.buffer(4);
        System.out.println("Init...");
        System.out.println(String.format(BYTEBUF_OUT_FORMAT, byteBuf1.readerIndex(), byteBuf1.writerIndex(), byteBuf1.capacity()));

        for (int i = 1; i <= 100; i++) {
            byteBuf1.writeInt(i);
            if ((i % 10) == 0) {
                System.out.println(String.format("After writer %d Integer:", i));
                System.out.println(String.format(BYTEBUF_OUT_FORMAT, byteBuf1.readerIndex(), byteBuf1.writerIndex(), byteBuf1.capacity()));
            }
        }

        while (byteBuf1.isReadable()) {
            System.out.print(byteBuf1.readInt() + ",");
        }
        System.out.println();
        System.out.println(String.format("After read %d Integer:", 100));
        System.out.println(String.format(BYTEBUF_OUT_FORMAT, byteBuf1.readerIndex(), byteBuf1.writerIndex(), byteBuf1.capacity()));
    }
}
