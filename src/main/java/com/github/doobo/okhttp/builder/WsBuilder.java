package com.github.doobo.okhttp.builder;

import com.github.doobo.okhttp.OkHttpClientTools;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;
import com.github.doobo.okhttp.response.IResponseHandler;
import com.github.doobo.okhttp.response.WsResponseHandler;
import com.github.doobo.okhttp.ws_manager.WsManager;
import com.github.doobo.okhttp.ws_manager.WsStatus;

public class WsBuilder extends OkHttpRequestBuilderHasParam<WsBuilder> {

    private boolean needReconnect = true;

    private OkHttpClient mOkHttpClient;

    private WsManager wsManager;

    private String originHost = "5fu8.com";

    @Override
    void enqueue(IResponseHandler iResponseHandler) {
        if(wsManager == null){
            return;
        }
        WsResponseHandler wsResponseHandler = new WsResponseHandler(){
            @Override
            public void onSuccess(Response response) {
                iResponseHandler.onSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                iResponseHandler.onFailure(statusCode, error_msg);
            }
        };
        this.wsManager.setOriginHost(originHost);
        this.wsManager.setWsStatusListener(wsResponseHandler);
        this.wsManager.startConnect();

    }

    public WsManager enqueue(WsResponseHandler wsResponseHandler) {
        if(wsManager == null){
            return wsManager;
        }
        wsManager.setWsStatusListener(wsResponseHandler);
        this.wsManager.startConnect();
        return wsManager;
    }

    public WsBuilder(OkHttpClientTools okHttpClientTools) {
        super(okHttpClientTools);
        this.mOkHttpClient = okHttpClientTools.getOkHttpClient();
    }

    public WsBuilder needReconnect(boolean val) {
        needReconnect = val;
        return this;
    }

    public String getUrl(){
        if (mParams != null && mParams.size() > 0) {
            mUrl = appendParams(mUrl, mParams);
        }
        return mUrl;
    }

    public WsBuilder build() {
        if(this.wsManager == null)
            this.wsManager = new WsManager(this);
        return this;
    }

    public boolean isNeedReconnect() {
        return needReconnect;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public WsBuilder stopConnect() {
        if(this.wsManager == null){
            return this;
        }
        wsManager.stopConnect();
        return this;
    }

    public WsBuilder setOriginHost(String originHost) {
        this.originHost = originHost;
        return this;
    }

    public boolean sendMessage(String msg) {
        if(this.wsManager == null){
            return false;
        }
        return wsManager.sendMessage(msg);
    }

    public boolean sendMessage(ByteString byteString) {
        if(this.wsManager == null){
            return false;
        }
        return wsManager.sendMessage(byteString);
    }

    public synchronized int getCurrentStatus() {
        if(this.wsManager == null){
            return WsStatus.DISCONNECTED;
        }
        return wsManager.getCurrentStatus();
    }

    public synchronized boolean isWsConnected() {
        if(this.wsManager == null){
            return false;
        }
        return wsManager.isWsConnected();
    }

    public WebSocket getWebSocket() {
        if(this.wsManager == null){
            return null;
        }
        return wsManager.getWebSocket();
    }
}
