package spring;

import com.github.doobo.okhttp.util.RestTemplateUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping
    public ResponseEntity<byte[]> proxyHttp(String url){
        return RestTemplateUtil.getByClass(url, byte[].class, null);
    }
}
