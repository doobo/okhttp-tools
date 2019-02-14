package vip.ipav.okhttp.ws_manager;

import okhttp3.*;
import okio.ByteString;
import vip.ipav.okhttp.builder.WsBuilder;
import vip.ipav.okhttp.response.WsResponseHandler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WsManager implements IWsManager {
    private final static int RECONNECT_INTERVAL = 10 * 1000;    //重连自增步长
    private final static long RECONNECT_MAX_TIME = 120 * 1000;   //最大重连间隔
    private final static String TTL = "TTL";
    private final static String PING = "ping ";
    private final static String MAIN = "main";
    private String wsUrl;
    private WebSocket mWebSocket;
    private OkHttpClient mOkHttpClient;
    private Request mRequest;
    private int mCurrentStatus = WsStatus.DISCONNECTED;     //websocket连接状态
    private boolean isNeedReconnect;          //是否需要断线自动重连
    private boolean isManualClose = false;         //是否为手动关闭websocket连接
    private WsResponseHandler wsResponseHandler;
    private Lock mLock;
    private int reconnectCount = 3;   //重连次数
    private String originHost = "www.baidu.com"; //检查网络是否正常的目标地址
    //重连
    private Runnable reconnectRunnable = new Runnable() {
        @Override
        public void run() {
            if (wsResponseHandler != null) {
                wsResponseHandler.onReconnect();
            }
            buildConnect();
        }
    };

    private WebSocketListener mWebSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, final Response response) {
            mWebSocket = webSocket;
            connected();
            if (wsResponseHandler != null) {
                if (!isMainThread()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            wsResponseHandler.onOpen(response);
                            wsResponseHandler.onOpen(webSocket,response);
                            wsResponseHandler.onSuccess(response);
                        }
                    }).start();
                } else {
                    wsResponseHandler.onOpen(response);
                    wsResponseHandler.onOpen(webSocket,response);
                    wsResponseHandler.onSuccess(response);
                }
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, final ByteString bytes) {
            if (wsResponseHandler != null) {
                if (!isMainThread()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            wsResponseHandler.onMessage(bytes);
                        }
                    }).start();
                } else {
                    wsResponseHandler.onMessage(bytes);
                }
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, final String text) {
            if (wsResponseHandler != null) {
                if (!isMainThread()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            wsResponseHandler.onMessage(text);
                        }
                    }).start();
                } else {
                    wsResponseHandler.onMessage(text);
                }
            }
        }

        @Override
        public void onClosing(WebSocket webSocket, final int code, final String reason) {
            if (wsResponseHandler != null) {
                if (!isMainThread()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            wsResponseHandler.onClosing(code, reason);
                        }
                    }).start();
                } else {
                    wsResponseHandler.onClosing(code, reason);
                }
            }
        }

        @Override
        public void onClosed(WebSocket webSocket, final int code, final String reason) {
            if (wsResponseHandler != null) {
                if (!isMainThread()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            wsResponseHandler.onClosed(code, reason);
                        }
                    }).start();
                } else {
                    wsResponseHandler.onClosed(code, reason);
                }
            }
        }

        @Override
        public void onFailure(WebSocket webSocket, final Throwable t, final Response response) {
            tryReconnect();
            if (wsResponseHandler != null) {
                if (!isMainThread()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            wsResponseHandler.onFailure(t, response);
                            wsResponseHandler.onFailure(400,response.message());
                        }
                    }).start();
                } else {
                    wsResponseHandler.onFailure(t, response);
                    wsResponseHandler.onFailure(400,response.message());
                }
            }
        }
    };

    public WsManager(WsBuilder builder) {
        wsUrl = builder.getUrl();
        isNeedReconnect = builder.isNeedReconnect();
        mOkHttpClient = builder.getOkHttpClient();
        this.mLock = new ReentrantLock();
    }

    private void initWebSocket() {
        //.retryOnConnectionFailure(true),断开重联
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .build();
        }
        if (mRequest == null) {
            mRequest = new Request.Builder()
                    .url(wsUrl)
                    .build();
        }
        mOkHttpClient.dispatcher().cancelAll();
        try {
            mLock.lockInterruptibly();
            try {
                mOkHttpClient.newWebSocket(mRequest, mWebSocketListener);
            } finally {
                mLock.unlock();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public WebSocket getWebSocket() {
        return mWebSocket;
    }


    public void setWsStatusListener(WsResponseHandler wsResponseHandler) {
        this.wsResponseHandler = wsResponseHandler;
    }

    @Override
    public synchronized boolean isWsConnected() {
        return mCurrentStatus == WsStatus.CONNECTED;
    }

    @Override
    public synchronized int getCurrentStatus() {
        return mCurrentStatus;
    }

    @Override
    public synchronized void setCurrentStatus(int currentStatus) {
        this.mCurrentStatus = currentStatus;
    }

    @Override
    public void startConnect() {
        isManualClose = false;
        buildConnect();
    }

    @Override
    public void stopConnect() {
        isManualClose = true;
        disconnect();
    }

    private void tryReconnect() {
        if (!isNeedReconnect | isManualClose) {
            return;
        }
        if (!isNetworkConnected()) {
            setCurrentStatus(WsStatus.DISCONNECTED);
            throw new RuntimeException("网络不通，重连失败");
        }
        setCurrentStatus(WsStatus.RECONNECT);
        long delay = reconnectCount * RECONNECT_INTERVAL;
        if (Long.valueOf(RECONNECT_MAX_TIME).compareTo(delay) < 0) {
            return;
        }
        try {
            Thread.sleep(delay);
            new Thread(reconnectRunnable).start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        reconnectCount++;
    }

    private void cancelReconnect() {
        //wsMainHandler.removeCallbacks(reconnectRunnable);
        //new Thread(reconnectRunnable).start();
        reconnectCount = 0;
    }

    private void connected() {
        //cancelReconnect();
        setCurrentStatus(WsStatus.CONNECTED);
    }

    private void disconnect() {
        if (mCurrentStatus == WsStatus.DISCONNECTED) {
            return;
        }
        //cancelReconnect();
        if (mOkHttpClient != null) {
            mOkHttpClient.dispatcher().cancelAll();
        }
        if (mWebSocket != null) {
            boolean isClosed = mWebSocket.close(WsStatus.CODE.NORMAL_CLOSE, WsStatus.TIP.NORMAL_CLOSE);
            //非正常关闭连接
            if (!isClosed) {
                if (wsResponseHandler != null) {
                    wsResponseHandler.onClosed(WsStatus.CODE.ABNORMAL_CLOSE, WsStatus.TIP.ABNORMAL_CLOSE);
                }
            }
        }
        setCurrentStatus(WsStatus.DISCONNECTED);
    }

    private synchronized void buildConnect() {
        switch (getCurrentStatus()) {
            case WsStatus.CONNECTED:
            case WsStatus.CONNECTING:
                break;
            default:
                setCurrentStatus(WsStatus.CONNECTING);
                initWebSocket();
        }
    }

    //发送消息
    @Override
    public boolean sendMessage(String msg) {
        return send(msg);
    }

    @Override
    public boolean sendMessage(ByteString byteString) {
        return send(byteString);
    }

    private boolean send(Object msg) {
        boolean isSend = false;
        if (mWebSocket != null && mCurrentStatus == WsStatus.CONNECTED) {
            if (msg instanceof String) {
                isSend = mWebSocket.send((String) msg);
            } else if (msg instanceof ByteString) {
                isSend = mWebSocket.send((ByteString) msg);
            }
            //发送消息失败，尝试重连
            if (!isSend) {
                tryReconnect();
            }
        }
        return isSend;
    }

    //判断是否是主线程
    public boolean isMainThread() {
        return MAIN.equals(Thread.currentThread().getName());
    }

    //检查网络是否连接
    private boolean isNetworkConnected() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec( PING + originHost);
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            StringBuffer sb = new StringBuffer();
            int i = 0;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                i++;
                if (i > 3) {
                    break;
                }
            }
            is.close();
            isr.close();
            br.close();
            if (null != sb && !sb.toString().equals("")) {
                if (sb.toString().toUpperCase().indexOf(TTL) > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getOriginHost() {
        return originHost;
    }

    public void setOriginHost(String originHost) {
        this.originHost = originHost;
    }
}