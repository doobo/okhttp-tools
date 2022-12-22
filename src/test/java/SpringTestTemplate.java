import com.github.doobo.okhttp.util.RestTemplateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

/**
 * spring的http工具测试
 */
public class SpringTestTemplate {

    @Test
    public void testGet(){
        int a = 1;
        ResponseEntity<String> byClass = RestTemplateUtil.getByClass("https://baidu.com?a={a}", String.class, null, a);
        System.out.println(byClass.getBody());
    }

    @Test
    public void testPost(){
        String byClass = RestTemplateUtil.post("https://baidu.com", "{}"
                , new ParameterizedTypeReference<String>(){}, null);
        System.out.println(byClass);
    }

    @Test
    public void testPut(){
        ResponseEntity<String> byClass = RestTemplateUtil.optionsByClass("https://baidu.com", String.class, null);
        System.out.println(byClass.getBody());
    }
}
