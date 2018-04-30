import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;

import org.junit.Test;
import vip.ipav.okhttp.OkHttpClientTools;
import vip.ipav.okhttp.response.DownloadResponseHandler;
import vip.ipav.okhttp.response.GsonResponseHandler;
import vip.ipav.okhttp.response.JsonResponseHandler;
import vip.ipav.okhttp.response.RawResponseHandler;
import vip.ipav.okhttp.util.RegularUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
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
                .addParam("source","3271760578")
                .addParam("url_long","http://www.douban.com/note/249723561")
                .execute().body().string();
        System.out.println(temp);
    }

    @Test
    public void testUtils(){
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
                        System.out.println("The File is Downing "+totalBytes);
                    }

                    @Override
                    public void onFinish(File downloadFile) {
                        System.out.println("File was Down!");
                        countDownLatch.countDown();

                    }

                    @Override
                    public void onProgress(long currentBytes, long totalBytes) {
                        System.out.println( "doDownload onProgress:" + currentBytes + "/" + totalBytes);
                    }

                    @Override
                    public void onFailure(String error_msg) {
                        System.out.println(error_msg);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await();
    }


}
