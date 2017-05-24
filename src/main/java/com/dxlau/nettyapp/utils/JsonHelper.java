package com.dxlau.nettyapp.utils;

import com.google.gson.Gson;

/**
 * Created by dxlau on 2017/5/24.
 */
public final class JsonHelper {
    private static Gson gson = new Gson();

    public static String toJson(Object src) {
        return gson.toJson(src);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
}
