package com.github.doobo.okhttp.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


/**
 * okhttp默认配置
 */
@Configuration
@PropertySource(value = "classpath:okhttp-tools-default.yml"
	, encoding = "utf-8", factory = YamlPropertySourceFactory.class)
public class OkhttpToolsDefaultConfig {

	@Bean
	@ConfigurationProperties("ipfs.default")
	@ConditionalOnMissingBean(name = "defaultOkHttpToolsProperties")
	public OKHttpProperties defaultOkHttpToolsProperties() {
		return new OKHttpProperties();
	}

	/**
	 * 获取okhttp默认配置
	 */
	public static OKHttpProperties getDefaultOkHttpToolsProperties() {
		return (OKHttpProperties) SimpleSpringUtil.getBean("defaultOkHttpToolsProperties");
	}
}
