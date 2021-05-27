package spring.config;

import org.springframework.web.client.RestTemplate;
import spring.HttpUtils;

/**
 * 获取restTemplate实例抽象类
 */
public abstract class AbstractHttpService {

    private RestTemplate restTemplate;

    public RestTemplate createRestTemplate(){
        if(restTemplate != null){
            return restTemplate;
        }
        this.restTemplate = HttpUtils.createRestTemplate();
        return restTemplate;
    }
}
