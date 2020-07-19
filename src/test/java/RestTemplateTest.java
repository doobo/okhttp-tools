import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import spring.RestTemplateUtil;

public class RestTemplateTest {
    
    @Test
    public void testGet(){
        Object str = RestTemplateUtil.getExchange("https://hao.360.cn/?src={}&ls={}"
        , new ParameterizedTypeReference<String>(){}, "lm", "n2a27c3f091");
        System.out.println(str);
    }
}
