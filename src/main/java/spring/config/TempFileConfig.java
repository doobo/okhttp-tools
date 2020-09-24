package spring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spring.HttpUtils;

import javax.annotation.Resource;
import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.io.IOException;

@Configuration
public class TempFileConfig {

    private static Logger log = LoggerFactory.getLogger(TempFileConfig.class);
    
    @Resource
    private BasicParams basicParams;
    
    /**
     * 文件上传临时路径
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
       
        try {
            File file = new File("logs");
            if(!file.exists() && file.mkdir()){
                log.info("create tmp dir success.");
            }
            factory.setLocation(file.getCanonicalPath());
        } catch (IOException e) {
            log.error("MultipartConfigElementError", e);
        }
        return factory.createMultipartConfig();
    }
    
    @Bean
    public HttpUtils httpUtils(){
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.basicParams(basicParams);
        return httpUtils;
    }
}
