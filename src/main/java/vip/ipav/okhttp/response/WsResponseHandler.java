package vip.ipav.okhttp.response;

import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

public class WsResponseHandler implements IResponseHandler{

    public void onOpen(Response response) {
    }

    public void onOpen(WebSocket webSocket, final Response response){}

    public void onMessage(String text) {
    }

    public void onMessage(ByteString bytes) {
    }

    public void onReconnect() {

    }
    public void onClosing(int code, String reason) {
    }

    public void onClosed(int code, String reason) {
    }

    public void onFailure(Throwable t, Response response) {
    }

    @Override
    public void onSuccess(Response response) {

    }

    @Override
    public void onFailure(int statusCode, String error_msg) {

    }

    @Override
    public void onProgress(long currentBytes, long totalBytes) {

    }
}
