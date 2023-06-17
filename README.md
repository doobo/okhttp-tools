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
   <version>1.3.1</version>
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
OkHttpClientTools.getInstance()
    .download()
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
        }
        @Override
        public void onProgress(long currentBytes, long totalBytes) {
            System.out.println("doDownload onProgress:" + currentBytes + "/" + totalBytes);
        }
        @Override
        public void onFailure(String error_msg) {
            countDownLatch.countDown();
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
## 参考文献
对于Okhttp3的封装参考了:
* [https://github.com/hongyangAndroid/okhttputils](https://github.com/hongyangAndroid/okhttputils)
* [https://github.com/jeasonlzy/okhttp-OkGo](https://github.com/jeasonlzy/okhttp-OkGo)
* [https://github.com/ZhaoKaiQiang/OkHttpPlus](https://github.com/ZhaoKaiQiang/OkHttpPlus)

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
