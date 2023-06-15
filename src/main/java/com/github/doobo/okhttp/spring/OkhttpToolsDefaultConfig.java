package com.github.doobo.okhttp.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Objects;


/**
 * okhttp默认配置
 */
@Configuration
@PropertySource(value = "classpath:okhttp-tools-default.yml"
	, encoding = "utf-8", factory = YamlPropertySourceFactory.class)
public class OkhttpToolsDefaultConfig {
	
	private static OKHttpProperties okHttpProperties;

	@Bean(name = "defaultOkHttpToolsProperties")
	@ConfigurationProperties("ipfs.default")
	@ConditionalOnMissingBean(name = "defaultOkHttpToolsProperties")
	public OKHttpProperties defaultOkHttpToolsProperties() {
		OkhttpToolsDefaultConfig.okHttpProperties = new OKHttpProperties();
		return OkhttpToolsDefaultConfig.okHttpProperties;
	}

	public static OKHttpProperties getOkHttpProperties() {
		if(Objects.isNull(okHttpProperties)){
			okHttpProperties = new OKHttpProperties();
		}
		return okHttpProperties;
	}
}
