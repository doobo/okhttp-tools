package com.github.doobo.okhttp.spring;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * okhttp配置
 */
@Configuration
public class OkHttpToolsConfig {

	private static final Logger log = LoggerFactory.getLogger(OkHttpToolsConfig.class);
	
	private static RestTemplate restTemplate;
	
	private static OKHttpProperties okHttpProperties;
	
	@Primary
	@Bean(name = "oKHttpProperties")
	@ConfigurationProperties("okhttp.tools")
	@ConditionalOnProperty(name = "okhttp.tools.startConfig", havingValue = "true")
	@ConditionalOnMissingBean(name = "oKHttpProperties")
	public OKHttpProperties oKHttpPropertiesByConfig() {
		OkHttpToolsConfig.okHttpProperties = new OKHttpProperties();
		return OkHttpToolsConfig.okHttpProperties;
	}

	@Bean(name ="fastRestTemplate")
	@ConditionalOnMissingBean(name = "fastRestTemplate")
	public RestTemplate fastRestTemplate(OKHttpProperties okHttpProperties){
		OkHttpToolsConfig.restTemplate = getInstanceRestTemplate(okHttpProperties);
		return OkHttpToolsConfig.restTemplate;
	}

	public static OKHttpProperties getOkHttpProperties() {
		if(Objects.isNull(okHttpProperties)){
			return OkhttpToolsDefaultConfig.getOkHttpProperties();
		}
		return okHttpProperties;
	}

	/**
	 * 获取http模板
	 */
	public static RestTemplate getRestTemplate() {
		return restTemplate;
	}

	/**
	 * 初始化restTemplate
	 */
	public static RestTemplate getInstanceRestTemplate(OKHttpProperties okHttpProperties){
		okHttpProperties = Optional.ofNullable(okHttpProperties).orElseGet(OkHttpToolsConfig::getOkHttpProperties);
		ClientHttpRequestFactory factory = httpRequestFactory(okHttpProperties);
		RestTemplate restTemplate = new RestTemplate(factory);
		restTemplate.setErrorHandler(new AcceptResponseErrorHandler());
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
	private static ClientHttpRequestFactory httpRequestFactory(OKHttpProperties okHttpProperties) {
		return new OkHttp3ClientHttpRequestFactory(okHttpConfigClient(okHttpProperties));
	}

	/**
	 * 客户端
	 */
	private static OkHttpClient okHttpConfigClient(OKHttpProperties okHttpProperties) {
		return new OkHttpClient().newBuilder()
				.connectionPool(pool(okHttpProperties))
				.connectTimeout(okHttpProperties.getConnectTimeout(), okHttpProperties.getConnectTimeoutTimeUnit())
				.readTimeout(okHttpProperties.getReadTimeout(), okHttpProperties.getReadTimeoutTimeUnit())
				.writeTimeout(okHttpProperties.getWriteTimeout(), okHttpProperties.getWriteTimeoutTimeUnit())
				.hostnameVerifier((hostname, session) -> true)
				.build();
	}

	/**
	 * 连接池
	 */
	private static ConnectionPool pool(OKHttpProperties okHttpProperties) {
		return new ConnectionPool(okHttpProperties.getMaxIdleConnections()
				, okHttpProperties.getKeepAliveDuration()
				, okHttpProperties.getKeepAliveDurationTimeUnit());
	}

	/**
	 * 使RestTemplate能够对响应的错误消息不进行处理
	 * 如：当响应码为400、500等错误时，能够不进行处理，最终用户可以获取到body数据
	 */
	private static class AcceptResponseErrorHandler implements ResponseErrorHandler {

		@Override
		public boolean hasError(ClientHttpResponse response) throws IOException {
			return false;
		}

		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
		}
	}

}
