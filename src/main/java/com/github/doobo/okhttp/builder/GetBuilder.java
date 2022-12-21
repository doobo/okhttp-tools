package com.github.doobo.okhttp.builder;

import com.github.doobo.okhttp.OkHttpClientTools;
import okhttp3.Request;
import okhttp3.Response;
import com.github.doobo.okhttp.callback.MyCallback;
import com.github.doobo.okhttp.response.IResponseHandler;


import java.io.IOException;

public class GetBuilder extends OkHttpRequestBuilderHasParam<GetBuilder> {

    public GetBuilder(OkHttpClientTools okHttpClientTools) {
        super(okHttpClientTools);
    }

    @Override
    public void enqueue(final IResponseHandler responseHandler) {
        try {
            if (mParams != null && mParams.size() > 0) {
                mUrl = appendParams(mUrl, mParams);
            }

            Request.Builder builder = new Request.Builder().url(mUrl).get();
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
        Request.Builder builder = new Request.Builder().url(mUrl).get();
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
