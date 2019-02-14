package vip.ipav.okhttp;

import okhttp3.Call;
import okhttp3.Dispatcher;
import vip.ipav.okhttp.builder.*;

public class OkHttpClientTools {
    private okhttp3.OkHttpClient mOkHttpClient;

    private static class LazyHolder {
        private static final OkHttpClientTools INSTANCE = new OkHttpClientTools();
    }

    public static final OkHttpClientTools getInstance() {
        return LazyHolder.INSTANCE;
    }

    public okhttp3.OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * construct
     */
    private OkHttpClientTools() {
        this(null);
    }

    /**
     * 自定义客户端
     * @param okHttpClient
     */
    public OkHttpClientTools(okhttp3.OkHttpClient okHttpClient) {
        if(mOkHttpClient == null) {
            synchronized (OkHttpClientTools.class) {
                if (mOkHttpClient == null) {
                    if (okHttpClient == null) {
                        mOkHttpClient = new okhttp3.OkHttpClient();
                    } else {
                        mOkHttpClient = okHttpClient;
                    }
                }
            }
        }
    }

    public GetBuilder get() {
        return new GetBuilder(this);
    }

    public HeadBuilder head() {
        return new HeadBuilder(this);
    }

    public PostBuilder post() {
        return new PostBuilder(this);
    }

    public PutBuilder put(){
        return new PutBuilder(this);
    }

    public PatchBuilder patch(){
        return new PatchBuilder(this);
    }

    public DeleteBuilder delete(){
        return new DeleteBuilder(this);
    }

    public UploadBuilder upload() {
        return new UploadBuilder(this);
    }

    public DownloadBuilder download() {
        return new DownloadBuilder(this);
    }

    public WsBuilder ws() {
        return new WsBuilder(this);
    }

    public void cancel(Object tag) {
        Dispatcher dispatcher = mOkHttpClient.dispatcher();
        for (Call call : dispatcher.queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : dispatcher.runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }
}
