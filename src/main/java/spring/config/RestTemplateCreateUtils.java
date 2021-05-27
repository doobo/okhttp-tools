package spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestTemplateCreateUtils {
    
    private static AbstractHttpService abstractHttpService;

    @Autowired(required = false)
    public void setAbstractHttpService(AbstractHttpService abstractHttpService) {
        RestTemplateCreateUtils.abstractHttpService = abstractHttpService;
    }

    /**
     * 获取RestTemplate
     */
    public static AbstractHttpService getAbstractHttpService() {
        if(RestTemplateCreateUtils.abstractHttpService == null){
            RestTemplateCreateUtils.abstractHttpService = new AbstractHttpService() {};
        }
        return RestTemplateCreateUtils.abstractHttpService;
    }
}
