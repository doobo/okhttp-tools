# OkHttpTools

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)

> 对Okhttp3进行二次封装,对外提供了POST请求、GET请求、PATCH请求、PUT请求、DELETE请求、上传文件、下载文件、取消请求、Ws/Wss请求、Raw/Json/Gson返回、后台下载管理等功能.
## 如何添加
```
   <repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
    
    <dependency>
	    <groupId>com.github.doobo</groupId>
	    <artifactId>OkHttpTools</artifactId>
	    <version>1.3</version>
	</dependency>
```

## 1 总体简介

### 1.1 调用方式

整个调用采用链式调用的方式. 方便以后扩展.

### 1.2 请求类型

现在有Get, Post, Patch, Put, Delete, Upload, Download, Ws/Wss这些请求方式

### 1.3 添加参数方式

添加参数可以使用addParam一个个添加, 也可以使用params一次性添加

### 1.4 添加Header方式

添加参数可以使用addHeader一个个添加, 也可以使用headers一次性添加

### 1.5 回调格式

现在回调格式有以下几种:

1. Raw原生数据 RawResponseHandler
2. Json JsonResponseHandler
3. Gson GsonResponseHandler
4. 下载 DownloadResponseHandler
5. 长连接 WsResponseHandler

## 2 调用示例
```
   //GET请求
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
    
    //文件上传
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
    
    //文件下载
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
    
    //Ws/Wss请求
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

    //cookie设置
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
    
    //获取请求的CODE
    @Test
    public void testAutoLogin() throws IOException {
        Response res = OkHttpClientTools.getInstance()
                        .get()
                        .url("https://m.aicai.com/m/userCenter.do?agentId=1&vt=5")
                        .addHeader("Cookie","cookiesid=ce3ee9471-27e3-4411-ae94-d89f93dc4ce9-56911233|a3e962802165daacb807c298023a502e")
                        .execute();

        System.out.println(res.isRedirect());
        System.out.println(res.code());
        System.out.println(res.message());

        //如果有跳转，获取跳转前的Response
        while (res.priorResponse() != null){
            System.out.println(res.priorResponse().code());
            res = res.priorResponse();
        }
    }
```

## 参考文献
对于Okhttp3的封装参考了:
1. [https://github.com/hongyangAndroid/okhttputils](https://github.com/hongyangAndroid/okhttputils)
1. [https://github.com/jeasonlzy/okhttp-OkGo](https://github.com/jeasonlzy/okhttp-OkGo)
1. [https://github.com/ZhaoKaiQiang/OkHttpPlus](https://github.com/ZhaoKaiQiang/OkHttpPlus)

cookie本地持久化使用了PersistentCookieJar：
1. [https://github.com/franmontiel/PersistentCookieJar](https://github.com/franmontiel/PersistentCookieJar)

License
-------
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
