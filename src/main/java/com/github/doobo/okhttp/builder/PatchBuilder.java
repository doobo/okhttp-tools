package com.github.doobo.okhttp.builder;

import com.github.doobo.okhttp.OkHttpClientTools;
import okhttp3.*;
import com.github.doobo.okhttp.callback.MyCallback;
import com.github.doobo.okhttp.response.IResponseHandler;

import java.io.IOException;
import java.util.Map;

public class PatchBuilder extends OkHttpRequestBuilderHasParam<PatchBuilder> {

    private String mJsonParams = "";

    public PatchBuilder(OkHttpClientTools okHttpClientTools) {
        super(okHttpClientTools);
    }

    /**
     * json格式参数
     * @param json
     * @return
     */
    public PatchBuilder jsonParams(String json) {
        this.mJsonParams = json;
        return this;
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

            if(mJsonParams.length() > 0) {//上传json格式参数
                RequestBody body = RequestBody.create(mJsonParams, MediaType.parse("application/json; charset=utf-8"));
                builder.patch(body);
            } else {//普通kv参数
                FormBody.Builder encodingBuilder = new FormBody.Builder();
                appendParams(encodingBuilder, mParams);
                builder.patch(encodingBuilder.build());
            }

            Request request = builder.build();

            mOkHttpClientTools.getOkHttpClient()
                    .newCall(request)
                    .enqueue(new MyCallback(responseHandler));
        } catch (Exception e) {
            responseHandler.onFailure(0, e.getMessage());
        }
    }

    //append params to form builder
    private void appendParams(FormBody.Builder builder, Map<String, String> params) {

        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }
    }

    /**
     * 同步执行
     * @return
     */
    public Response execute() {
        Request.Builder builder = new Request.Builder().url(mUrl);
        appendHeaders(builder, mHeaders);

        if (mTag != null) {
            builder.tag(mTag);
        }

        if (mJsonParams.length() > 0) {
            RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), mJsonParams);
            builder.patch(body);
        } else {
            FormBody.Builder encodingBuilder = new FormBody.Builder();
            appendParams(encodingBuilder, mParams);
            builder.patch(encodingBuilder.build());
        }
        Request request = builder.build();
        try {
            return mOkHttpClientTools.getOkHttpClient()
                    .newCall(request)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
