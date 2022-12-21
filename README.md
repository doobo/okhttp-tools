# OkHttpTools

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)

> 对Okhttp3和RestTemplate进行简单封装,对外提供了POST、GET、PATCH、PUT、DELETE、上传文件、下载文件、取消请求、Ws/Wss请求.
* 如果异步使用相关JSON解析器，请主动添加该包，默认不会添加，以免引起不必要的引
* 添加对spring的restTemplate支持,可自定义相关参数，参考文件:okhttp-tools-default.yml,对restTemplate的关系映射使用的是fastjson
## 如何添加
```
 <dependency>
   <groupId>com.github.doobo</groupId>
   <artifactId>okhttp-tools</artifactId>
   <version>1.3</version>
 </dependency>
```

### 在spring项目中使用
```yml
okhttp:
  tools:
    startConfig: true
    maxIdleConnections: 30
    keepAliveDuration: 300
    connectTimeout: 10
    readTimeout: 20
    writeTimeout: 10
```

## RestTemplate使用
```code
//代理URL资源文件
@RestController
public class IndexController {

    @GetMapping
    public ResponseEntity<byte[]> proxyHttp(String url){
        return RestTemplateUtil.getByClass(url, byte[].class, null);
    }
}

@Test
public void testGet(){
    int a = 1;
    ResponseEntity<String> byClass = RestTemplateUtil.getByClass("https://baidu.com?a={a}", String.class, null, a);
    System.out.println(byClass.getBody());
}

@Test
public void testPost(){
    ResponseEntity<String> byClass = RestTemplateUtil.postByClass("https://baidu.com", null, String.class, null);
    System.out.println(byClass.getBody());
}

@Test
public void testPut(){
    ResponseEntity<String> byClass = RestTemplateUtil.optionsByClass("https://baidu.com", String.class, null);
    System.out.println(byClass.getBody());
}
```

### 回调格式
现在回调格式有以下几种:
1. Raw原生数据 RawResponseHandler
2. Json JsonResponseHandler
3. Gson GsonResponseHandler
4. 下载 DownloadResponseHandler
5. 长连接 WsResponseHandler

## OkHttp调用示例
```code
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
        String wsUrl = "ws://5fu8.com/chatroomServer/1021";

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
                .url("http://5fu8.com/api/weibo/queryChargeInfo.json?agentId=100031&platform=wap&version=1.0.0")
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
                .url("http://5fu8.com/api/weibo/queryChargeInfo.json?agentId=100031&platform=wap&version=1.0.0")
                .execute().body().string();
        System.out.println(str);
    }

    //获取请求的CODE
    @Test
    public void testAutoLogin() throws IOException {
        Response res = OkHttpClientTools.getInstance()
                        .get()
                        .url("https://5fu8.com/m/userCenter.do?agentId=1&vt=5")
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
    
    //url拼接测试
    @Test
    public void testSpeedUrl(){
        String url = "http://www.5fu8.com/index.html?m=uuid&";
        Map<String, String> params = new HashMap<>();
        params.put("abc","123");
        System.out.println(OkHttpClientTools.getInstance().head().appendParams(url,params));
    }
```

## 添加springboot的restTemplate模板支持,GET/POST/PUT/DELETE/HEAD/TRACE/PATCH/OPTIONS

### restTemplate使用实例
```

    //文件下载
    @ApiOperation(value = "Pdf打印")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "affairInfoCode", dataType = "string", paramType = "query")
    })
    @PostMapping("/GetPdfFile")
    public void getPdfFilePost(HttpServletRequest request, HttpServletResponse response) {
        Resource resource = RestTemplateUtil.postExchange(PdfUrl.getPdfFilePost, request,ApiType.API_CENTRE, null
                ,new ParameterizedTypeReference<Resource>() {});
        RestTemplateUtil.downloadFile(response, resource);
    }
    
    //如果想要自定义httpUtil的参数,如日志监控等,BasicParams继承它，并暴露给Bean容器
    @Bean
    public HttpUtils httpUtils(){
        HttpUtils httpUtils = new HttpUtils();
        HttpUtils.basicParams(basicParams);
        return httpUtils;
    }
    
    //链路追踪配置
    /**
     * 配置 span 收集器
     * @return
     */
    @Bean
    public SpanCollector spanCollector() {
        Config config = Config.builder()
                .connectTimeout(connecTimeout)
                .compressionEnabled(compressionEnabled)
                .flushInterval(flushInterval)
                .readTimeout(readTimeout)
                .build();

        return HttpSpanCollector.create(url, config, new EmptySpanCollectorMetricsHandler());
    }

    /**
     * 配置采集率
     * @param spanCollector
     * @return
     */
    @Bean
    public Brave brave(SpanCollector spanCollector) {
        Builder builder = new Builder(serviceName);
        builder.spanCollector(spanCollector)
                .traceSampler(Sampler.create(samplerRate))
                .build();
        return builder.build();
    }
    
    @Bean
    public CloseableHttpClient httpClient(Brave brave) {
        CloseableHttpClient httpclient = HttpClients.custom()
                .addInterceptorFirst(new BraveHttpRequestInterceptor(brave.clientRequestInterceptor(),
                        new DefaultSpanNameProvider()))
                .addInterceptorFirst(new BraveHttpResponseInterceptor(brave.clientResponseInterceptor()))
                .setMaxConnTotal(ComProperties.getTotal()).setMaxConnPerRoute(ComProperties.getRoute())
                .build();
        return httpclient;
    }

    /**
     * 配置zk拦截器
     * @param httpClient
     * @return
     */
    @Bean
    public RestTemplateUtil restTemplateUtil(CloseableHttpClient httpClient){
        RestTemplateUtil restTemplateUtil = new RestTemplateUtil();
        restTemplateUtil.setHttpClient(httpClient);
        return restTemplateUtil;
    }
    
    /**
     * 设置server的（服务端收到请求和服务端完成处理，并将结果发送给客户端）过滤器
     * @Param:
     * @return: 过滤器
     * 不知道有用没,也许可以去掉
     */
    @Bean
    public BraveServletFilter braveServletFilter(Brave brave) {
        BraveServletFilter filter = new BraveServletFilter(brave.serverRequestInterceptor(),
                brave.serverResponseInterceptor(), new DefaultSpanNameProvider());
        return filter;
    }
    
    //配置restTemplate
    private static RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        clientHttpRequestFactory.setConnectionRequestTimeout(ComProperties.getRequestTimeout());
        clientHttpRequestFactory.setConnectTimeout(ComProperties.getConnectTimeout());
        clientHttpRequestFactory.setReadTimeout(ComProperties.getReadTimeout());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(clientHttpRequestFactory);
        return restTemplate;
    }
```

## 参考文献
对于Okhttp3的封装参考了:
* [https://github.com/hongyangAndroid/okhttputils](https://github.com/hongyangAndroid/okhttputils)
* [https://github.com/jeasonlzy/okhttp-OkGo](https://github.com/jeasonlzy/okhttp-OkGo)
* [https://github.com/ZhaoKaiQiang/OkHttpPlus](https://github.com/ZhaoKaiQiang/OkHttpPlus)

cookie本地持久化使用了PersistentCookieJar：
* [https://github.com/franmontiel/PersistentCookieJar](https://github.com/franmontiel/PersistentCookieJar)

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
