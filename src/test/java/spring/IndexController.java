package spring;

import com.github.doobo.okhttp.util.RestTemplateUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class IndexController {

    @GetMapping
    public ResponseEntity<byte[]> proxyHttp(String url){
        if(Objects.isNull(url) || !url.startsWith("http")){
            return ResponseEntity.ok("hello".getBytes());
        }
        return RestTemplateUtil.getByClass(url, byte[].class, null);
    }
}
