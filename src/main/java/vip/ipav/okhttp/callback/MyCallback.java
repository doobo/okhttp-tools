package vip.ipav.okhttp.callback;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import vip.ipav.okhttp.response.IResponseHandler;

import java.io.IOException;

/**
 * Created by tsy on 16/9/18.
 */
public class MyCallback implements Callback {

    private IResponseHandler mResponseHandler;

    public MyCallback(IResponseHandler responseHandler) {
        mResponseHandler = responseHandler;
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        mResponseHandler.onFailure(409,e.getMessage());
    }

    @Override
    public void onResponse(Call call, final Response response) {
        if(response.isSuccessful()) {
            mResponseHandler.onSuccess(response);
        } else {
            mResponseHandler.onFailure(response.code(),response.message());
        }
    }
}
