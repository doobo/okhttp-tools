package vip.ipav.okhttp.response;

import okhttp3.Response;

public interface IResponseHandler {

    void onSuccess(Response response);

    void onFailure(int statusCode, String error_msg);

    void onProgress(long currentBytes, long totalBytes);
}
