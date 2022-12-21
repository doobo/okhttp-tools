package com.github.doobo.okhttp.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


/**
 * okhttp配置
 */
@Configuration
public class OkHttpToolsConfig {

	@Bean
	@Primary
	@ConfigurationProperties("okhttp.tools")
	@ConditionalOnProperty(name = "okhttp.tools.startConfig", havingValue = "true")
	@ConditionalOnMissingBean(name = "oKHttpProperties")
	public OKHttpProperties oKHttpProperties() {
		return new OKHttpProperties();
	}

	/**
	 * 获取okhttp配置
	 */
	public static OKHttpProperties getOKHttpProperties() {
		return SimpleSpringUtil.getBean(OKHttpProperties.class);
	}
}
