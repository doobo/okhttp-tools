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
                .url("https://5fu8.com/search.json")
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
                .url("https://5fu8.com/search.json")
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
                        .url("https://5fu8.com/search.json")
                        .addParam("source", "3271760578")
                        .addParam("url_long", "http://www.douban.com/note/249723561")
                        .execute().body().string();
        System.out.println(temp);
    }

    @Test
    public void testSyncPost() throws IOException {
        String temp =
                OkHttpClientTools.getInstance()
                        .post()
                        .url("https://5fu8.com/search.json")
                        .addParam("source", "3271760578")
                        .addParam("url_long", "http://www.douban.com/note/249723561")
                        .jsonParams("{}")
                        .execute().body().string();
        System.out.println(temp);
    }

    @Test
    public void testDownFile() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        OkHttpClientTools.getInstance().download()
                .url("https://5fu8.com/medias/logo.png")
                .filePath("./target/logo.png")
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
                .url("https://5fu8.com/search.json")
                .addParam("source", "3271760578")
                .addParam("url_long", "http://www.douban.com/note/249723561")
                .execute().body().string();
        System.out.println(str);
    }
}
