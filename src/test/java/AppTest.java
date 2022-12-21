import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.doobo.okhttp.response.DownloadResponseHandler;
import com.github.doobo.okhttp.response.GsonResponseHandler;
import com.github.doobo.okhttp.response.JsonResponseHandler;
import com.github.doobo.okhttp.response.RawResponseHandler;
import okhttp3.*;

import okhttp3.logging.HttpLoggingInterceptor;
import com.github.doobo.okhttp.OkHttpClientTools;
import com.github.doobo.okhttp.util.RegularUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * Unit test for simple App.
 */
public class AppTest {
    @Test
    public void testJsonOkHttp() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        OkHttpClientTools okHttpClientTools = OkHttpClientTools.getInstance();
        okHttpClientTools.get()
//                .url("https://gitee.com/doobo/codes/gqolr1s0bezfk4xmjw2n680")
                .url("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=218.4.255.255")
//                .url("http://api.t.sina.com.cn/short_url/shorten.json")
//                .addParam("source","3271760578")
//                .addParam("url_long","http://www.douban.com/note/249723561")
                .enqueue(new JsonResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        System.out.println(error_msg);
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        System.out.println(response);
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onSuccess(int statusCode, JSONArray response) {
                        System.out.println(response);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await();
    }

    @Test
    public void testGsonOkHttp() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        OkHttpClientTools okHttpClientTools = OkHttpClientTools.getInstance();
        okHttpClientTools.get()
//                .url("https://gitee.com/doobo/codes/gqolr1s0bezfk4xmjw2n680")
                .url("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=218.4.255.255")
//                .url("http://api.t.sina.com.cn/short_url/shorten.json")
//                .addParam("source","3271760578")
//                .addParam("url_long","http://www.douban.com/note/249723561")
                .enqueue(new GsonResponseHandler<Object>() {
                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        System.out.println(error_msg);
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onSuccess(int statusCode, Object response) {
                        System.out.println(response);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await();
    }

    @Test
    public void testSyncGet() throws IOException {
        String temp =
                OkHttpClientTools.getInstance()
                        .get()
                        .url("http://api.t.sina.com.cn/short_url/shorten.json")
                        .addParam("source", "3271760578")
                        .addParam("url_long", "http://www.douban.com/note/249723561")
                        .execute().body().string();
        System.out.println(temp);
    }

    @Test
    public void testUtils() {
        System.out.println(RegularUtils.hasWenHao("?abc=123"));
        System.out.println(RegularUtils.hasWenHao(".abc=123?"));
        System.out.println(RegularUtils.hasWenHao("https://www.baidu.com?abc=123"));
    }


    @Test
    public void testDownFile() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        OkHttpClientTools.getInstance().download()
                .url("http://p4.so.qhmsg.com/t01decceaa40a9f9a19.jpg")
                .filePath("/Users/doobo/next.jpg")
                .tag(this)
                .enqueue(new DownloadResponseHandler() {
                    @Override
                    public void onStart(long totalBytes) {
                        System.out.println("The File is Downing " + totalBytes);
                    }

                    @Override
                    public void onFinish(File downloadFile) {
                        System.out.println("File was Down!");
                        countDownLatch.countDown();

                    }

                    @Override
                    public void onProgress(long currentBytes, long totalBytes) {
                        System.out.println("doDownload onProgress:" + currentBytes + "/" + totalBytes);
                    }

                    @Override
                    public void onFailure(String error_msg) {
                        System.out.println(error_msg);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await();
    }

    @Test
    public void testCookie() throws IOException {
        /*通过header设置cookie*/
        String str = OkHttpClientTools.getInstance()
                .get()
                .url("http://ftxh5-daily.ttyingqiu.com/api/weibo/queryChargeInfo.json?agentId=100031&platform=wap&version=1.0.0")
                .addHeader("Cookie", "agentId=100031;device_uuid=ovUb1xnFAfYEu15-fPmUm4zzqClAmMVzt6y-m5zKgnfDo;MEIQIA_EXTRA_TRACK_ID=142LupsDtyHgMw5yRcYmM3qrhga;ftx_token=f960a6554b9968a4d69462aeadc29fc8")
                .execute().body().string();
        System.out.println(str);

        /*通过CookieJar自动管理cookie*/
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                        System.out.println(list);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                        System.out.println(httpUrl.pathSegments());
                        ArrayList<Cookie> cookies = new ArrayList<>(8);
                        cookies.add(new Cookie.Builder().name("agentId").value("100031").domain("ttyingqiu.com").build());
                        cookies.add(new Cookie.Builder().name("device_uuid").value("ovUb1xnFAfYEu15-fPmUm4zzqClAmMVzt6y-m5zKgnfDo").domain("ttyingqiu.com").build());
                        cookies.add(new Cookie.Builder().name("MEIQIA_EXTRA_TRACK_ID").value("142LupsDtyHgMw5yRcYmM3qrhga").domain("ttyingqiu.com").build());
                        cookies.add(new Cookie.Builder().name("ftx_token").value("f960a6554b9968a4d69462aeadc29fc8").domain("ttyingqiu.com").build());
                        return cookies;
                    }
                }).build();
        str = new OkHttpClientTools(okHttpClient)
                .get()
                .url("http://ftxh5-daily.ttyingqiu.com/api/weibo/queryChargeInfo.json?agentId=100031&platform=wap&version=1.0.0")
                .execute().body().string();
        System.out.println(str);
    }

    /**
     * 请求日志跟踪
     *
     * @throws IOException
     */
    @Test
    public void logTest() throws IOException {
        //创建日志拦截器
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLogger());
        //设置日志级别，共包含四个级别：NONE、BASIC、HEADERS、BODY
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        String str = new OkHttpClientTools(client)
                .get()
                .url("https://api.t.sina.com.cn/short_url/shorten.json")
                .addParam("source", "3271760578")
                .addParam("url_long", "http://www.douban.com/note/249723561")
                .execute().body().string();
        System.out.println(str);
    }

    @Test
    public void testCup() throws IOException {
        //创建日志拦截器
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLogger());
        //设置日志级别，共包含四个级别：NONE、BASIC、HEADERS、BODY
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        String result = new OkHttpClientTools(client)
                .get()
                .addHeader("auth","你好")
                .url("http://www.baidu.com")
                .addParam("a", "B")
                .execute().body().string();
        System.out.println(result);
    }

    @Test
    public void testEqual() {
        OkHttpClientTools ok1 = OkHttpClientTools.getInstance();
        OkHttpClientTools ok2 = OkHttpClientTools.getInstance();
        System.out.println(ok1);
        System.out.println(ok2);

        OkHttpClientTools ok3 = new OkHttpClientTools(new OkHttpClient());
        OkHttpClientTools ok4 = new OkHttpClientTools(new OkHttpClient());
        System.out.println(ok3);
        System.out.println(ok4);
    }

    @Test
    public void testFileUpload() throws IOException {
        //创建日志拦截器
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLogger());
        //设置日志级别，共包含四个级别：NONE、BASIC、HEADERS、BODY
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        String str = new OkHttpClientTools(client)
                .upload()
                .url("http://pic.sogou.com/pic/upload_pic.jsp")
                .addParam("type","utf-8;text/json")
                .addFile("files", new File("/Users/doobo/Downloads/myAirTicket.png"))
                .execute().body().string();
        System.out.println(str);
    }

    @Test
    public void uploadImage() throws InterruptedException {
        //创建日志拦截器
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLogger());
        //设置日志级别，共包含四个级别：NONE、BASIC、HEADERS、BODY
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        new OkHttpClientTools(client)
                .upload()
                .url("http://pic.sogou.com/pic/upload_pic.jsp")
                .addParam("type","utf-8;text/json")
                .addFile("files", new File("/Users/doobo/Downloads/bbc.png"))
                .enqueue(new RawResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, String response) {
                        countDownLatch.countDown();
                        System.out.println(response);
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        countDownLatch.countDown();
                        System.out.println(error_msg);
                    }
                });
        countDownLatch.await();
    }

}
