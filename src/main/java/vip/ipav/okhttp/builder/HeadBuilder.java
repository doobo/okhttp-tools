package vip.ipav.okhttp.builder;

import okhttp3.Request;
import okhttp3.Response;
import vip.ipav.okhttp.OkHttpClientTools;
import vip.ipav.okhttp.callback.MyCallback;
import vip.ipav.okhttp.response.IResponseHandler;
import vip.ipav.okhttp.util.RegularUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class HeadBuilder extends OkHttpRequestBuilderHasParam<HeadBuilder> {

    public HeadBuilder(OkHttpClientTools okHttpClientTools) {
        super(okHttpClientTools);
    }

    @Override
    public void enqueue(final IResponseHandler responseHandler) {
        try {
            if (mParams != null && mParams.size() > 0) {
                mUrl = appendParams(mUrl, mParams);
            }

            Request.Builder builder = new Request.Builder().url(mUrl).head();
            appendHeaders(builder, mHeaders);

            if (mTag != null) {
                builder.tag(mTag);
            }

            Request request = builder.build();

            mOkHttpClientTools.getOkHttpClient().
                    newCall(request).
                    enqueue(new MyCallback(responseHandler));
        } catch (Exception e) {
            responseHandler.onFailure(0, e.getMessage());
        }
    }

    /**
     * 同步执行
     * @return
     */
    public Response execute() {
        if (mParams != null && mParams.size() > 0) {
            mUrl = appendParams(mUrl, mParams);
        }
        Request.Builder builder = new Request.Builder().url(mUrl).head();
        appendHeaders(builder, mHeaders);

        if (mTag != null) {
            builder.tag(mTag);
        }
        Request request = builder.build();

        try {
            return mOkHttpClientTools.getOkHttpClient().
                    newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
