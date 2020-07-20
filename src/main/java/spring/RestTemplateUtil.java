package spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import vip.ipav.okhttp.OkHttpClientTools;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Map;

/**
 * restTemplate公共请求方法
 */
public class RestTemplateUtil {

    private static Logger log = LoggerFactory.getLogger(RestTemplateUtil.class);

    private static final class SignRestTemplate{
		static final RestTemplate INSTANCE = HttpUtils.createRestTemplate();
    }

    public static RestTemplate getInstance() {
		return SignRestTemplate.INSTANCE;
    }

    public static HttpEntity<Object> param(HttpServletRequest request) {
        try {
			byte[] body = StreamUtils.copyToByteArray(request.getInputStream());
            return new HttpEntity<>(body, getAuthorization(request));
        } catch (Exception e) {
            log.warn("RestTemplateUtilError", e);
            return new HttpEntity<>(null, getAuthorization(request));
        }
    }

    public static HttpEntity<Object> param(HttpServletRequest request, Object body) {
        return new HttpEntity<>(body, getAuthorization(request));
    }

    private static String param(HttpServletRequest request, String url) {
        Map<String, String[]> param = request.getParameterMap();
        return OkHttpClientTools.getInstance().head().appendParamArr(url,param);
    }

    private static HttpHeaders getAuthorization(HttpServletRequest request) {
        HttpHeaders requestHeaders = new HttpHeaders();
        Enumeration<String> em = request.getHeaderNames();
        if(em != null){
            String name;
            while (em.hasMoreElements()){
                name = em.nextElement();
                requestHeaders.add(name, request.getHeader(name));
            }
        }
        return requestHeaders;
    }

    /**
     * url里面不带参数
     * @param url
     * @param request
     * @param responseType
     */
    public static <T> T getExchange(String url, HttpServletRequest request, ParameterizedTypeReference<T> responseType) {
        return RestTemplateUtil.getInstance().exchange(param(request, url)
                , HttpMethod.GET, param(null, getAuthorization(request))
                , responseType).getBody();
    }

    /**
     * url里面有参数
     * @param url
     * @param request
     * @param responseType
     * @param uriVariables
     */
    public static <T> T getExchange(String url, HttpServletRequest request, ParameterizedTypeReference<T> responseType, Object ...uriVariables) {
        return RestTemplateUtil.getInstance().exchange(url
                , HttpMethod.GET, new HttpEntity<>(null, getAuthorization(request))
                , responseType, uriVariables).getBody();
    }

    /**
     * 原生http请求
     * @param url
     * @param responseType
     * @param uriVariables
     */
    public static <T> T getExchange(String url, ParameterizedTypeReference<T> responseType, Object ...uriVariables) {
        return RestTemplateUtil.getInstance().exchange(url
                , HttpMethod.GET, new HttpEntity<>(null)
                , responseType, uriVariables).getBody();
    }

    /**
     * 原生http请求
     * @param url
     * @param data
     * @param responseType
     * @param uriVariables
     */
    public static <T> T getExchange(String url, Object data, ParameterizedTypeReference<T> responseType, Object ...uriVariables) {
        return RestTemplateUtil.getInstance().exchange(url
                , HttpMethod.GET, new HttpEntity<>(data)
                , responseType, uriVariables).getBody();
    }

    /**
     * url里面不带参数
     * @param url
     * @param request
     * @param data
     * @param responseType
     */
    public static <T> T postExchange(String url, HttpServletRequest request, Object data, ParameterizedTypeReference<T> responseType) {
        return RestTemplateUtil.getInstance().exchange(param(request, url)
                , HttpMethod.POST, param(request, data)
                , responseType).getBody();
    }

    /**
     * url里面不带参数
     * @param url
     * @param request
     * @param responseType
     */
    public static <T> T postExchange(String url, HttpServletRequest request, ParameterizedTypeReference<T> responseType) {
        return RestTemplateUtil.getInstance().exchange(param(request, url)
                , HttpMethod.POST, param(request)
                , responseType).getBody();
    }

    /**
     * url里面有参数
     * @param url
     * @param request
     * @param responseType
     * @param uriVariables
     */
    public static <T> T postExchange(String url, HttpServletRequest request, Object data, ParameterizedTypeReference<T> responseType, Object ...uriVariables) {
        return RestTemplateUtil.getInstance().exchange(url
                , HttpMethod.POST, param(request, data)
                , responseType, uriVariables).getBody();
    }

    /**
     * 原生http请求
     * @param url
     * @param data
     * @param responseType
     * @param uriVariables
     */
    public static <T> T postExchange(String url, String data, ParameterizedTypeReference<T> responseType, Object ...uriVariables) {
        return RestTemplateUtil.getInstance().exchange(url
                , HttpMethod.GET, new HttpEntity<>(data)
                , responseType, uriVariables).getBody();
    }

    /**
     * url里面不带参数
     * @param url
     * @param request
     * @param data
     * @param responseType
     */
    public static <T> T putExchange(String url, HttpServletRequest request, Object data, ParameterizedTypeReference<T> responseType) {
        return RestTemplateUtil.getInstance().exchange(param(request, url)
                , HttpMethod.PUT, new HttpEntity<>(data, getAuthorization(request))
                , responseType).getBody();
    }

    /**
     * url里面有参数
     * @param url
     * @param request
     * @param responseType
     * @param uriVariables
     */
    public static <T> T putExchange(String url, HttpServletRequest request, Object data, ParameterizedTypeReference<T> responseType, Object ...uriVariables) {
        return RestTemplateUtil.getInstance().exchange(url
                , HttpMethod.PUT, param(request, data)
                , responseType, uriVariables).getBody();
    }

    /**
     * 原生http请求
     * @param url
     * @param data
     * @param responseType
     * @param uriVariables
     */
    public static <T> T putExchange(String url, String data, ParameterizedTypeReference<T> responseType, Object ...uriVariables) {
        return RestTemplateUtil.getInstance().exchange(url
                , HttpMethod.PUT, new HttpEntity<>(data)
                , responseType, uriVariables).getBody();
    }

    /**
     * url里面不带参数
     * @param url
     * @param request
     * @param data
     * @param responseType
     * @return
     */
    public static <T> T delExchange(String url, HttpServletRequest request, Object data, ParameterizedTypeReference<T> responseType) {
        return RestTemplateUtil.getInstance().exchange(param(request, url)
                , HttpMethod.DELETE, param(request, data)
                , responseType).getBody();
    }

    /**
     * url里面有参数
     * @param url
     * @param request
     * @param responseType
     * @param uriVariables
     */
    public static <T> T delExchange(String url, HttpServletRequest request, Object data, ParameterizedTypeReference<T> responseType, Object ...uriVariables) {
        return RestTemplateUtil.getInstance().exchange(url
                , HttpMethod.DELETE, param(request, data)
                , responseType, uriVariables).getBody();
    }

    /**
     * 原生http请求
     * @param url
     * @param data
     * @param responseType
     * @param uriVariables
     */
    public static <T> T delExchange(String url, String data, ParameterizedTypeReference<T> responseType, Object ...uriVariables) {
        return RestTemplateUtil.getInstance().exchange(url
                , HttpMethod.DELETE, new HttpEntity<>(data)
                , responseType, uriVariables).getBody();
    }

    /**
     * 原生http请求
     * @param url
     * @param data
     * @param responseType
     * @param uriVariables
     */
    public static <T> T headExchange(String url, String data, ParameterizedTypeReference<T> responseType, Object ...uriVariables) {
        return RestTemplateUtil.getInstance().exchange(url
                , HttpMethod.HEAD, new HttpEntity<>(data)
                , responseType, uriVariables).getBody();
    }

    /**
     * 原生http请求
     * @param url
     * @param data
     * @param responseType
     * @param uriVariables
     */
    public static <T> T traceExchange(String url, String data, ParameterizedTypeReference<T> responseType, Object ...uriVariables) {
        return RestTemplateUtil.getInstance().exchange(url
                , HttpMethod.TRACE, new HttpEntity<>(data)
                , responseType, uriVariables).getBody();
    }

    /**
     * 原生http请求
     * @param url
     * @param data
     * @param responseType
     * @param uriVariables
     */
    public static <T> T patchExchange(String url, String data, ParameterizedTypeReference<T> responseType, Object ...uriVariables) {
        return RestTemplateUtil.getInstance().exchange(url
                , HttpMethod.PATCH, new HttpEntity<>(data)
                , responseType, uriVariables).getBody();
    }

    /**
     * 原生http请求
     * @param url
     * @param data
     * @param responseType
     * @param uriVariables
     */
    public static <T> T optionsExchange(String url, String data, ParameterizedTypeReference<T> responseType, Object ...uriVariables) {
        return RestTemplateUtil.getInstance().exchange(url
                , HttpMethod.OPTIONS, new HttpEntity<>(data)
                , responseType, uriVariables).getBody();
    }
   
	public static void downloadFile(HttpServletResponse response, Resource resource) {
		if (resource != null) {
			try(InputStream is = resource.getInputStream();OutputStream os = response.getOutputStream()
				;BufferedInputStream bis = new BufferedInputStream(is)) {
				response.reset();
				response.setContentType("*");
				response.setHeader("Access-Control-Allow-Origin", "*");

				int len;
				byte[] buff = new byte[1024];
				while ((len = bis.read(buff)) != -1) {
					os.write(buff, 0, len);
					os.flush();
				}
			}catch (Exception e) {
				log.info("download file error：", e);
				throw new RuntimeException("文件下载失败：" + e.getMessage());
			}
		}
	}
}
