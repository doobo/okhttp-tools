import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.junit.Test;
import vip.ipav.okhttp.OkHttpClientTools;

import java.io.File;
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
        new Thread(() -> {
            System.out.printf("%s\t%d\n",myLocalString.get(),0);
            countDownLatch.countDown();
        }).start();
        System.out.printf("%s\t%d\n",myLocalString.get(),1);
        countDownLatch.await();
    }

    @Test
    public void testUrl() throws IOException {
        String html = OkHttpClientTools.getInstance().get()
                .url("https://hao.360.cn/")
                .addParam("src","lm")
                .addParam("ls","n2a27c3f091")
                .execute().body().string();
        System.out.println(html);
    }

    @Test
    public void testSpeedUrl(){
        String url = "http://www.baidu.com/index.html?m=uuid&";
        Map<String, String> params = new HashMap<>();
        params.put("abc","123");
        System.out.println(OkHttpClientTools.getInstance().head().appendParams(url,params));
    }

    @Test
    public void testAutoLogin() throws IOException {
        Response res = OkHttpClientTools.getInstance()
                        .get()
                        .url("https://m.aicai.com/m/userCenter.do?agentId=1&vt=5")
                        .addHeader("Cookie","cookiesid=ce3ee9471-27e3-4411-ae94-d89f93dc4ce9-56911233|a3e962802165daacb807c298023a502e")
                        .execute();

        //System.out.println(res.body().string());
        System.out.println(res.isRedirect());
        System.out.println(res.code());
        System.out.println(res.message());

        //如果有跳转，获取跳转前的Response
        while (res.priorResponse() != null){
            System.out.println(res.priorResponse().code());
            res = res.priorResponse();
        }
    }
    
    @Test
    public void testChineseHeader() throws IOException {
        Response ret = OkHttpClientTools.getInstance()
                .upload()
                .url("https://sm.ms/api/v2/upload")
                .addHeader("Authorization","333sss")
                .addHeader("Accept-Language", "zh-cn")
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Accept-Charset", "UTF-8,utf-8;q=0.7,*;q=0.7")
                .addHeader("User-Agent", "Mozilla/5.0 (SymbianOS/9.1; U; en-us) AppleWebKit/413 (KHTML, like Gecko) Safari/413")
                .addFile("smfile",new File("/Users/doobo/Pictures/粘福字.jpeg"))
                .execute();
        System.out.println(ret.message());
        System.out.println(ret.body().string());
    }
}
