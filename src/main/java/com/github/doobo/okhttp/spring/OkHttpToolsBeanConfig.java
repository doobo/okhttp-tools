package com.github.doobo.okhttp.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 获取REST模板
 *
 * @Description: okhttp-tools
 * @User: diding
 * @Time: 2023-06-16 09:49
 */
@Component
public class OkHttpToolsBeanConfig {

    private static RestTemplate restTemplate;

    public static RestTemplate getRestTemplate() {
        return restTemplate;
    }

    @Autowired(required = false)
    public void setRestTemplate(@Qualifier("fastRestTemplate") RestTemplate restTemplate) {
        OkHttpToolsBeanConfig.restTemplate = restTemplate;
    }
}
