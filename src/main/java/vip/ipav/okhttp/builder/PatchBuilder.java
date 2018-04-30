package vip.ipav.okhttp.builder;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import vip.ipav.okhttp.OkHttpClientTools;
import vip.ipav.okhttp.callback.MyCallback;
import vip.ipav.okhttp.response.IResponseHandler;
import vip.ipav.okhttp.util.LogUtils;

public class PatchBuilder extends OkHttpRequestBuilder<PatchBuilder> {

    public PatchBuilder(OkHttpClientTools okHttpClientTools) {
        super(okHttpClientTools);
    }

    @Override
    public void enqueue(final IResponseHandler responseHandler) {
        try {
            if(mUrl == null || mUrl.length() == 0) {
                throw new IllegalArgumentException("url can not be null !");
            }

            Request.Builder builder = new Request.Builder().url(mUrl);
            appendHeaders(builder, mHeaders);

            if (mTag != null) {
                builder.tag(mTag);
            }

            builder.patch(RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), ""));
            Request request = builder.build();

            mOkHttpClientTools.getOkHttpClient()
                    .newCall(request)
                    .enqueue(new MyCallback(responseHandler));
        } catch (Exception e) {
            LogUtils.e("Patch enqueue error:" + e.getMessage());
            responseHandler.onFailure(0, e.getMessage());
        }
    }
}
