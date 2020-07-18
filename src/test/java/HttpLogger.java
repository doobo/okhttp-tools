import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 网络日志拦截器
 * com.squareup.okhttp3:logging-interceptor
 */
public class HttpLogger implements HttpLoggingInterceptor.Logger {

    @Override
    public void log(String message) {
        System.out.printf("HttpLogInfo:%s\n",message);
    }
}