package spring;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {
    
    @RequestMapping("")
    public ResponseEntity<String> proxyHttp(String url){
        return RestTemplateUtil.getByClass(url, String.class, null);
    }
}
