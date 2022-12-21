package com.github.doobo.okhttp.response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

/**
 * json类型的回调接口
 * Created by tsy on 16/8/15.
 */
public abstract class JsonResponseHandler implements IResponseHandler {

    @Override
    public final void onSuccess(final Response response) {
        ResponseBody responseBody = response.body();
        String responseBodyStr = "";
        try {
            responseBodyStr = responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
            onFailure(response.code(),e.getMessage());
            return;
        } finally {
            responseBody.close();
        }

        final String finalResponseBodyStr = responseBodyStr;
        try {
            final Object result =  JSONArray.parse(finalResponseBodyStr);
            if(result instanceof JSONObject) {
                onSuccess(response.code(), (JSONObject) result);
            } else if(result instanceof JSONArray) {
                onSuccess(response.code(), (JSONArray) result);
            } else {
                onFailure(response.code(),finalResponseBodyStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onFailure(response.code(),finalResponseBodyStr);
        }
    }

    public void onSuccess(int statusCode, JSONObject response) {}

    public void onSuccess(int statusCode, JSONArray response) {}

    @Override
    public void onProgress(long currentBytes, long totalBytes) {}
}