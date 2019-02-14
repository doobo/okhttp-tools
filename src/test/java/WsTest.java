import okhttp3.*;
import okio.ByteString;
import org.junit.Test;
import vip.ipav.okhttp.OkHttpClientTools;
import vip.ipav.okhttp.builder.WsBuilder;
import vip.ipav.okhttp.response.WsResponseHandler;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class WsTest {

    private static WebSocket mWebSocket = null;
    private static int msgCount = 0; //消息发送次数
    private static Timer mTimer;

    @Test
    public void testWsConnect() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String wsUrl = "ws://sport-daily.ttyingqiu.com/chatroomServer/1021";
        //新建client
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        //构造request对象
        Request request = new Request.Builder()
                .url(wsUrl)
                .build();

        //建立连接
        client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                mWebSocket = webSocket;
                //打印一些内容
                System.out.println("client onOpen");
                System.out.println("client request header:" + response.request().headers());
                System.out.println("client response header:" + response.headers());
                System.out.println("client response:" + response);
                //开启消息定时发送
                startTask();
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                //打印一些内容
                System.out.println("client onMessage");
                System.out.println("message:" + text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                System.out.println("client onClosing");
                System.out.println("code:" + code + " reason:" + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                //打印一些内容
                System.out.println("client onClosed");
                System.out.println("code:" + code + " reason:" + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                //出现异常会进入此回调
                System.out.println("client onFailure");
                System.out.println("throwable:" + t);
                System.out.println("response:" + response);
            }
        });
        countDownLatch.await();
    }

    //每秒发送一条消息
    private static void startTask() {
        mTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (mWebSocket == null) return;
                msgCount++;
                String msg = "{\n" +
                        "    \"ct\": \"自动消息测试\",\n" +
                        "    \"ctType\": 1,\n" +
                        "    \"from\": 2805,\n" +
                        "    \"nickName\": \"河蟹\",\n" +
                        "    \"fromPhoto\": \"http://m-daily.aicai.com//upload/memberphoto/tn_member_160_160.jpg\",\n" +
                        "    \"roomId\": 1021,\n" +
                        "    \"msgUuid\": \"88b7a610-4588-3797-e435-d7c76bcf62c0\"\n" +
                        "}";
                boolean isSuccessed = mWebSocket.send(msg);
                //除了文本内容外，还可以将如图像，声音，视频等内容转为ByteString发送
                //boolean send(ByteString bytes);
                System.out.println(isSuccessed);
            }
        };
        mTimer.schedule(timerTask, 0, 10000);
    }

    @Test
    public void testResult() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String wsUrl = "ws://sport-daily.ttyingqiu.com/chatroomServer/1021";


        WsBuilder builder = OkHttpClientTools.getInstance().ws()
                .url(wsUrl)
                .build();

        WsResponseHandler wsResponseHandler = new WsResponseHandler() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                mWebSocket = builder.getWebSocket();
                //打印一些内容
                System.out.println("client onOpen");
                System.out.println("client request header:" + response.request().headers());
                System.out.println("client response header:" + response.headers());
                System.out.println("client response:" + response);
                //开启消息定时发送
                startTask();
            }

            public void onMessage(WebSocket webSocket, String text) {
                //打印一些内容
                System.out.println("client onMessage");
                System.out.println("message:" + text);
            }

            public void onMessage(WebSocket webSocket, ByteString bytes) {
            }

            public void onClosing(WebSocket webSocket, int code, String reason) {
                System.out.println("client onClosing");
                System.out.println("code:" + code + " reason:" + reason);
            }

            public void onClosed(WebSocket webSocket, int code, String reason) {
                //打印一些内容
                System.out.println("client onClosed");
                System.out.println("code:" + code + " reason:" + reason);
            }

            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                //出现异常会进入此回调
                System.out.println("client onFailure");
                System.out.println("throwable:" + t);
                System.out.println("response:" + response);
            }
        };

        builder.enqueue(wsResponseHandler);
        countDownLatch.await();
    }

}
