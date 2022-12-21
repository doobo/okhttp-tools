package com.github.doobo.okhttp.util;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.github.doobo.okhttp.spring.OKHttpProperties;
import com.github.doobo.okhttp.spring.OkHttpToolsConfig;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

/**
 * okhttp属性获取
 */
public abstract class OkHttpPropertyUtil {

    private static final Logger log = LoggerFactory.getLogger(OkHttpPropertyUtil.class);

    private static OKHttpProperties okHttpProperties;

    /**
     * 初始化restTemplate
     */
    public static RestTemplate getInstance(){
        try {
            //兼容不启用springboot相关依赖
            okHttpProperties = OkHttpToolsConfig.getOKHttpProperties();
        }catch (NullPointerException e){
            okHttpProperties = new OKHttpProperties();
        }
        ClientHttpRequestFactory factory = httpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(factory);
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        Iterator<HttpMessageConverter<?>> iterator = messageConverters.iterator();
        while(iterator.hasNext()){
            HttpMessageConverter<?> converter=iterator.next();
            //原有的String是ISO-8859-1编码 去掉
            if(converter instanceof StringHttpMessageConverter){
                iterator.remove();
            }
            //由于系统中默认有jackson 在转换json时自动会启用  但是我们不想使用它 可以直接移除
            if(converter instanceof GsonHttpMessageConverter || converter instanceof MappingJackson2HttpMessageConverter){
                iterator.remove();
            }
        }
        messageConverters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        messageConverters.add(fastJsonHttpMessageConverter);
        log.info("restTemplate build complete");
        return restTemplate;
    }

    /**
     * 工厂
     */
    private static ClientHttpRequestFactory httpRequestFactory() {
        return new OkHttp3ClientHttpRequestFactory(okHttpConfigClient());
    }

    /**
     * 客户端
     */
    private static OkHttpClient okHttpConfigClient() {
        return new OkHttpClient().newBuilder()
                .connectionPool(pool())
                .connectTimeout(okHttpProperties.getConnectTimeout(), okHttpProperties.getConnectTimeoutTimeUnit())
                .readTimeout(okHttpProperties.getReadTimeout(), okHttpProperties.getReadTimeoutTimeUnit())
                .writeTimeout(okHttpProperties.getWriteTimeout(), okHttpProperties.getWriteTimeoutTimeUnit())
                .hostnameVerifier((hostname, session) -> true)
                .build();
    }

    /**
     * 连接池
     */
    private static ConnectionPool pool() {
        return new ConnectionPool(okHttpProperties.getMaxIdleConnections()
                , okHttpProperties.getKeepAliveDuration()
                , okHttpProperties.getKeepAliveDurationTimeUnit());
    }
}
