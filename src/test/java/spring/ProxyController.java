package spring;

import com.github.doobo.okhttp.util.RestTemplateUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

@RestController
@RequestMapping("/api")
public class ProxyController {

    //mapping的规则需要根据项目情况定义
    private final String uriRegex = "/api/proxy";

    /**
     * 向PC端转发get请求
     */
    @GetMapping(value = "/proxy/**")
    public ResponseEntity<?> getPCWithParams(HttpServletRequest request, HttpMethod httpMethod) throws URISyntaxException {
        return request2PC(request, httpMethod, null);
    }

    /**
     * 向PC端转发post请求  JSON数据请求
     */
    @PostMapping(value = "/proxy/**", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postPCWithJson(HttpServletRequest request, HttpMethod httpMethod) throws URISyntaxException {
        return request2PC(request, httpMethod, null);
    }

    /**
     * 向PC端转发post请求  formUrlEncode数据请求
     * @param formUrlEncode formUrlEncode数据
     */
    @PostMapping(value = "/proxy/**", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> postPCWithFormUrlEncode(@RequestBody MultiValueMap<String, String> formUrlEncode, HttpServletRequest request, HttpServletResponse response, HttpMethod httpMethod) throws URISyntaxException {
        return request2PC(request, httpMethod, formUrlEncode);
    }

    /**
     * 向PC端转发post请求formData数据请求 文件上传请求
     */
    @PostMapping(value = "/proxy/**", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postPCWithFormData(HttpServletRequest request) throws URISyntaxException {
        return null;
    }

    /**
     * 向PC端转发请求
     */
    private ResponseEntity<?> request2PC(HttpServletRequest request, HttpMethod httpMethod, Object body) throws URISyntaxException {
        String[] requestURIArr = request.getRequestURI().split(uriRegex);
        if (requestURIArr.length < 1) {
            throw new RuntimeException("请求路径异常!");
        }
        String requestURI = requestURIArr[1];
        URI thirdPartyApi = new URI("https", null, "5fu8.com"
                , 443, requestURI, request.getQueryString(), null);
        RestTemplate restTemplate = RestTemplateUtil.getInstance();
        HttpHeaders httpHeaders = RestTemplateUtil.getRequestHeader(request);
        httpHeaders.put("host", Collections.singletonList(thirdPartyApi.getHost()));
        HttpEntity<Object> httpEntity = RestTemplateUtil.getHttpEntity(request, httpHeaders, body);
        return restTemplate.exchange(thirdPartyApi, httpMethod, httpEntity, byte[].class);
    }
}
