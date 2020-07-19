package spring;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import spring.config.BasicParams;
import spring.config.HttpRequestInterceptor;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * restTemplate工具
 */
public class HttpUtils {

    private static Logger log = LoggerFactory.getLogger(HttpUtils.class);

    private static int HTTP_CLIENT_RETRY_COUNT = 1;
    private static int MAXIMUM_TOTAL_CONNECTION = 10;
    private static int MAXIMUM_CONNECTION_PER_ROUTE = 5;
    private static int CONNECTION_VALIDATE_AFTER_INACTIVITY_MS = 10 * 1000;
    private static int CONNECTION_TIMEOUT = 20 * 1000;
    private static int READ_TIMEOUT = 20 * 1000;
    private static boolean REST_DEBUG = false;

    public static RestTemplate createRestTemplate() {
        HttpClientBuilder clientBuilder = HttpClients.custom();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        // Set the maximum number of total open connections.
        connectionManager.setMaxTotal(MAXIMUM_TOTAL_CONNECTION);
        // Set the maximum number of concurrent connections per route, which is 2 by default.
        connectionManager.setDefaultMaxPerRoute(MAXIMUM_CONNECTION_PER_ROUTE);
        connectionManager.setValidateAfterInactivity(CONNECTION_VALIDATE_AFTER_INACTIVITY_MS);
        clientBuilder.setConnectionManager(connectionManager);
        clientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(HTTP_CLIENT_RETRY_COUNT, true, new ArrayList<>()) {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                return super.retryRequest(exception, executionCount, context);
            }
        });
        try {
            //设置SSL,信任所有证书
            TrustStrategy trustStrategy = (X509Certificate[] chain, String authType) -> true;
            SSLContext sslContexts = SSLContexts.custom().loadTrustMaterial(null,trustStrategy ).build();
            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContexts);
            clientBuilder.setSSLSocketFactory(csf);
        } catch (Exception e) {
            log.error("HttpUtilsError", e);
        }
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(clientBuilder.build());
        httpRequestFactory.setConnectTimeout(CONNECTION_TIMEOUT);
        httpRequestFactory.setConnectionRequestTimeout(READ_TIMEOUT);
        httpRequestFactory.setReadTimeout(READ_TIMEOUT);
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        //添加拦截器,调试输入和输出信息,目前测试文件上传不影响日志监听
		if(REST_DEBUG){
			restTemplate.setInterceptors(Collections.singletonList(new HttpRequestInterceptor()));
		}
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(httpRequestFactory));
        restTemplate.getMessageConverters().stream().filter(StringHttpMessageConverter.class::isInstance).map(StringHttpMessageConverter.class::cast).forEach(a -> {
            a.setWriteAcceptCharset(false);
            a.setDefaultCharset(StandardCharsets.UTF_8);
        });
		List<HttpMessageConverter<?>> converters = new ArrayList<>();
		//部分转化器默认匹配全部的MediaType 详见restTemplate的doWithRequest（）方法和转化器的 canRead（）/canWrite（）方法
		converters.add(new ByteArrayHttpMessageConverter());
		converters.add(new StringHttpMessageConverter(Charset.forName(StandardCharsets.UTF_8.name())));
		converters.add(new ResourceHttpMessageConverter());
		converters.add(new SourceHttpMessageConverter<>());
		converters.add(new AllEncompassingFormHttpMessageConverter());
		//converters.add(new MappingJackson2XmlHttpMessageConverter());
		converters.add(new MappingJackson2HttpMessageConverter());
		restTemplate.setMessageConverters(converters);
        return restTemplate;
    }

	/**
	 * 配置基础参数
	 * @param params
	 */
	public static void basicParams(BasicParams params){
		MAXIMUM_TOTAL_CONNECTION = params.getTotal();
		MAXIMUM_CONNECTION_PER_ROUTE = params.getRoute();
		CONNECTION_TIMEOUT = params.getConnectTimeout();
		READ_TIMEOUT = params.getReadTimeout();
		REST_DEBUG = params.isRestDebug();
	}


}
