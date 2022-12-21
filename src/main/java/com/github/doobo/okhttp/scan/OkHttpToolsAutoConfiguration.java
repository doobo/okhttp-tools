package com.github.doobo.okhttp.scan;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

/**
 * 基础包扫描
 */
@Configuration
@AutoConfigureOrder(Integer.SIZE)
@ComponentScans({@ComponentScan("com.github.doobo.okhttp.spring")})
public class OkHttpToolsAutoConfiguration {
}
