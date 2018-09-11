import okhttp3.OkHttpClient;
import org.junit.Test;
import vip.ipav.okhttp.OkHttpClientTools;
import vip.ipav.okhttp.builder.GetBuilder;
import vip.ipav.okhttp.util.RegularUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class HttpThread {

    @Test
    public void multiThreadHttp() throws Exception {
        ThreadLocal<OkHttpClient> myOkHttpClient = new ThreadLocal<>();
        myOkHttpClient.set(new OkHttpClient());
        String temp =
                new OkHttpClientTools(myOkHttpClient.get())
                        .get()
                        .url("http://api.t.sina.com.cn/short_url/shorten.json")
                        .addParam("source","3271760578")
                        .addParam("url_long","http://www.douban.com/note/249723561")
                        .execute().body().string();
        System.out.println(temp);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    testSyncGet();
                    testSyncGet();
                    testSyncGet2();
                    testSyncGet2();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            }
        }).start();
        countDownLatch.await();
    }

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

    public void testSyncGet2() throws IOException {
        String temp =
                new OkHttpClientTools(new OkHttpClient())
                        .get()
                        .url("http://api.t.sina.com.cn/short_url/shorten.json")
                        .addParam("source","3271760578")
                        .addParam("url_long","http://www.douban.com/note/249723561")
                        .execute().body().string();
        System.out.println(temp);
    }

    @Test
    public void testThreadLocal() throws InterruptedException {
        /*创建线程局部变量，使之线程安全*/
        ThreadLocal<String> myLocalString = new ThreadLocal<>();
        myLocalString.set("www.ipav.vip");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.printf("%s\t%d\n",myLocalString.get(),0);
                countDownLatch.countDown();
            }
        }).start();
        System.out.printf("%s\t%d\n",myLocalString.get(),1);
        countDownLatch.await();
    }

    @Test
    public void testRegular(){
        System.out.println(RegularUtils.isUrl("https://hao.360.cn#home"));
        System.out.println(RegularUtils.isUrl("https://hao.360.cn!"));
        System.out.println(RegularUtils.isUrl("https://hao.360.cn;"));
        System.out.println(RegularUtils.isUrl("https://hao.360.cn/"));
        System.out.println(RegularUtils.isUrl("https://hao.360.cn/?src=lm&ls=n2a27c3f091"));
        System.out.println(RegularUtils.isUrl("https://hao.360.cn#"));
        System.out.println(RegularUtils.isUrl("https://hao.360.cn"));
        System.out.println(RegularUtils.isUrl("https://hao.360.cn:8080/#home;status=3"));
        System.out.println(RegularUtils.isUrl("https://hao.360.cn/;staus=1"));
    }

    @Test
    public void testUrl() throws IOException {
        String html = OkHttpClientTools.getInstance().get()
                .url("https://hao.360.cn/")
                .addParam("src","lm")
                .addParam("ls","n2a27c3f091")
                .execute().body().string();
    }

    @Test
    public void testSpeedUrl(){
        String url = "http://www.baidu.com/index.html?m=uuid&";
        Map<String, String> params = new HashMap<>();
        params.put("abc","123");
        System.out.println(OkHttpClientTools.getInstance().head().appendParams(url,params));
    }
}
