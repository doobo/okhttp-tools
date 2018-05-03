package vip.ipav.okhttp.util;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 网络日志拦截器
 * com.squareup.okhttp3:logging-interceptor:3.1.0
 */
public class HttpLogger implements HttpLoggingInterceptor.Logger {

    @Override
    public void log(String message) {
        System.out.printf("HttpLogInfo:%s\n",message);
    }
}