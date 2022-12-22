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
```code
@RestController
public class IndexController {

   /**
    * 代理URL资源文件
    */
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
    String byClass = RestTemplateUtil.post("https://baidu.com", "{}"
            , new ParameterizedTypeReference<String>(){}, null);
    System.out.println(byClass);
}
```
### 配置链接池
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

## OkHttp调用示例
```code
//发送get请求
String result = OkHttpClientTools.getInstance()
                .get()
                .addHeader("auth","你好")
                .url("http://www.baidu.com")
                .addParam("a", "B")
                .execute().body().string();
//简单文件上传
String str = new OkHttpClientTools(client)
        .upload()
        .url("http://pic.sogou.com/pic/upload_pic.jsp")
        .addParam("type","utf-8;text/json")
        .addFile("files", new File("/Users/doobo/Downloads/myAirTicket.png"))
        .execute().body().string();
System.out.println(str);

//简单下载文件
new OkHttpClientTools(client)
    .upload()
    .url("http://pic.sogou.com/pic/upload_pic.jsp")
    .addParam("type","utf-8;text/json")
    .addFile("files", new File("/Users/doobo/Downloads/bbc.png"))
    .enqueue(new RawResponseHandler() {
        @Override
        public void onSuccess(int statusCode, String response) {
            System.out.println(response);
        }
        @Override
        public void onFailure(int statusCode, String error_msg) {
            System.out.println(error_msg);
        }
    });

//302转发兼容
Response res = OkHttpClientTools.getInstance()
                .get()
                .url("https://5fu8.com/m/userCenter.do?agentId=1&vt=5")
                .addHeader("Cookie","cookiesid=ce3ee9471-27e3-4411-ae94-d89f93dc4ce9-56911233|a3e962802165daacb807c298023a502e")
                .execute();
while (res.priorResponse() != null){
    System.out.println(res.priorResponse().code());
    res = res.priorResponse();
}

//webscoket请求实例
WsBuilder builder = OkHttpClientTools.getInstance().ws()
                .url(wsUrl)
                .build();
WsResponseHandler wsResponseHandler = new WsResponseHandler() {
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        mWebSocket = builder.getWebSocket();
        //开启消息定时发送
        startTask();
    }
    public void onMessage(WebSocket webSocket, String text) {
        //打印一些内容
    }
    public void onMessage(WebSocket webSocket, ByteString bytes) {
    }
    public void onClosing(WebSocket webSocket, int code, String reason) {
    }
    public void onClosed(WebSocket webSocket, int code, String reason) {
    }
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        //出现异常会进入此回调
    }
};
builder.enqueue(wsResponseHandler);
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
