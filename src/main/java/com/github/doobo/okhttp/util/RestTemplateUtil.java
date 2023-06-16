package com.github.doobo.okhttp.util;

import com.github.doobo.okhttp.spring.OkHttpToolsBeanConfig;
import com.github.doobo.okhttp.spring.OkHttpToolsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


/**
 * http简单方法
 */
public abstract class RestTemplateUtil {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateUtil.class);

    protected static HttpHeaders headers;
    
    protected static RestTemplate instance;
    
    protected static final Set<String> removeHeaderSet = new HashSet<>();
    
    static {
        instance = OkHttpToolsBeanConfig.getRestTemplate();
        if(Objects.isNull(instance)){
            instance = OkHttpToolsConfig.getInstanceRestTemplate(null);
        }
        headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    }

    public static void setInstance(RestTemplate instance) {
        RestTemplateUtil.instance = instance;
    }

    public static RestTemplate getInstance() {
        return instance;
    }

    /**
     * 添加排除的header
     */
    public static void addRemoveHeaderList(List<String> rls){
        removeHeaderSet.addAll(rls);
    }

    public static HttpEntity<Object> paramBody(HttpServletRequest request) {
        try {
			byte[] body = StreamUtils.copyToByteArray(request.getInputStream());
            return new HttpEntity<>(body, getRequestHeader(request));
        } catch (Exception e) {
            log.error("RestTemplateUtilError", e);
            return new HttpEntity<>(getRequestHeader(request));
        }
    }

    public static HttpEntity<Object> paramBody(HttpServletRequest request, Object body) {
        return new HttpEntity<>(body, getRequestHeader(request));
    }

    private static String param(HttpServletRequest request, String url) {
        return paramGet(request, url);
    }

    /**
     * 获取request里面的头部信息
     */
    public static HttpHeaders getRequestHeader(HttpServletRequest request) {
        HttpHeaders requestHeaders = new HttpHeaders();
        if(request == null){
            return requestHeaders;
        }
        Enumeration<String> em = request.getHeaderNames();
        if(em != null){
            String name;
            while (em.hasMoreElements()){
                name = em.nextElement();
                if(removeHeaderSet.contains(name)){
                    continue;
                }
                requestHeaders.add(name, request.getHeader(name));
            }
        }
        return requestHeaders;
    }

    /**
     * url里面不带参数
     */
    public static <T> T getExchange(String url, HttpServletRequest request
            , ParameterizedTypeReference<T> responseType) {
        return instance.exchange(param(request, url)
                , HttpMethod.GET, paramBody(null, getRequestHeader(request))
                , responseType).getBody();
    }

    /**
     * url里面有参数
     */
    public static <T> T getByProxy(String url, HttpServletRequest request
            , Class<T> responseType, Object ...uriVariables) {
        return instance.exchange(param(request, url)
                , HttpMethod.GET, new HttpEntity<>(getRequestHeader(request))
                , responseType, uriVariables).getBody();
    }

    /**
     * 自定义头部信息
     */
    public static <T> ResponseEntity<T> getByHeader(String url, HttpServletRequest request
            , Class<T> cls, HttpHeaders headers, Object... uriVariables) {
        return instance.exchange(paramGet(request, url), HttpMethod.GET
                , new HttpEntity<>(headers), cls, uriVariables);
    }

    /**
     * 原生http请求
     */
    public static <T> T get(String url, ParameterizedTypeReference<T> responseType
            , HttpHeaders headers, Object ...uriVariables) {
        return instance.exchange(url
                , HttpMethod.GET, new HttpEntity<>(headers)
                , responseType, uriVariables).getBody();
    }

    /**
     * 自定义头部信息
     */
    public static <T> ResponseEntity<T> getByClass(String url, Class<T> cls
            , HttpHeaders headers, Object... uriVariables) {
        headers = Optional.ofNullable(headers).orElse(RestTemplateUtil.headers);
        return instance.exchange(url, HttpMethod.GET
                , new HttpEntity<>( headers), cls, uriVariables);
    }

    /**
     * url里面不带参数
     */
    public static <T> T postExchange(String url, HttpServletRequest request
            , Object data, ParameterizedTypeReference<T> responseType) {
        return instance.exchange(param(request, url)
                , HttpMethod.POST, paramBody(request, data)
                , responseType).getBody();
    }

    /**
     * url里面有参数
     */
    public static <T> T postByParams(String url, HttpServletRequest request
            , Object data, ParameterizedTypeReference<T> responseType, Object ...uriVariables) {
        return instance.exchange(param(request, url)
                , HttpMethod.POST, paramBody(request, data)
                , responseType, uriVariables).getBody();
    }

    /**
     * 原生http请求
     */
    public static <T> T post(String url, String data, ParameterizedTypeReference<T> responseType
            , HttpHeaders headers, Object ...uriVariables) {
        headers = Optional.ofNullable(headers).orElse(RestTemplateUtil.headers);
        return instance.exchange(url
                , HttpMethod.POST, new HttpEntity<>(data, headers)
                , responseType, uriVariables).getBody();
    }

    /**
     * 原生http请求
     */
    public static <T> ResponseEntity<T> postByClass(String url, String data
            , Class<T> cls, HttpHeaders headers, Object ...uriVariables) {
        headers = Optional.ofNullable(headers).orElse(RestTemplateUtil.headers);
        return instance.exchange(url
                , HttpMethod.POST, new HttpEntity<>(data, headers)
                , cls, uriVariables);
    }

    /**
     * url里面不带参数
     */
    public static <T> T putExchange(String url, HttpServletRequest request, Object data
            , ParameterizedTypeReference<T> responseType) {
        return instance.exchange(param(request, url)
                , HttpMethod.PUT, new HttpEntity<>(data, getRequestHeader(request))
                , responseType).getBody();
    }

    /**
     * url里面有参数
     */
    public static <T> T putByParams(String url, HttpServletRequest request
            , Object data, Class<T> responseType, Object ...uriVariables) {
        return instance.exchange(param(request, url)
                , HttpMethod.PUT, paramBody(request, data)
                , responseType, uriVariables).getBody();
    }

    /**
     * 原生http请求
     */
    public static <T> T put(String url, String data, ParameterizedTypeReference<T> responseType
            , HttpHeaders headers, Object ...uriVariables) {
        headers = Optional.ofNullable(headers).orElse(RestTemplateUtil.headers);
        return instance.exchange(url
                , HttpMethod.PUT, new HttpEntity<>(data, headers)
                , responseType, uriVariables).getBody();
    }

    /**
     * 原生http请求
     */
    public static <T> ResponseEntity<T> putByClass(String url, String data, Class<T> responseType
            , HttpHeaders headers, Object ...uriVariables) {
        headers = Optional.ofNullable(headers).orElse(RestTemplateUtil.headers);
        return instance.exchange(url
                , HttpMethod.PUT, new HttpEntity<>(data, headers)
                , responseType, uriVariables);
    }

    /**
     * url里面不带参数
     */
    public static <T> T delExchange(String url, HttpServletRequest request, Object data
            , ParameterizedTypeReference<T> responseType) {
        return instance.exchange(param(request, url)
                , HttpMethod.DELETE, paramBody(request, data)
                , responseType).getBody();
    }

    /**
     * url里面有参数
     */
    public static <T> T delExchange(String url, HttpServletRequest request
            , Object data, ParameterizedTypeReference<T> responseType, Object ...uriVariables) {
        return instance.exchange(param(request, url)
                , HttpMethod.DELETE, paramBody(request, data)
                , responseType, uriVariables).getBody();
    }

    /**
     * 原生http请求
     */
    public static <T> T del(String url, String data, ParameterizedTypeReference<T> responseType
            , HttpHeaders headers, Object ...uriVariables) {
        headers = Optional.ofNullable(headers).orElse(RestTemplateUtil.headers);
        return instance.exchange(url
                , HttpMethod.DELETE, new HttpEntity<>(data, headers)
                , responseType, uriVariables).getBody();
    }

    /**
     * 原生http请求
     */
    public static <T> ResponseEntity<T> delByClass(String url, String data
            , Class<T> responseType, HttpHeaders headers, Object ...uriVariables) {
        headers = Optional.ofNullable(headers).orElse(RestTemplateUtil.headers);
        return instance.exchange(url
                , HttpMethod.DELETE, new HttpEntity<>(data, headers)
                , responseType, uriVariables);
    }

    /**
     * 原生http请求
     */
    public static <T> T head(String url, String data, ParameterizedTypeReference<T> responseType
            , HttpHeaders headers, Object ...uriVariables) {
        headers = Optional.ofNullable(headers).orElse(RestTemplateUtil.headers);
        return instance.exchange(url
                , HttpMethod.HEAD, new HttpEntity<>(data, headers)
                , responseType, uriVariables).getBody();
    }

    /**
     * 原生http请求
     */
    public static <T> ResponseEntity<T> headByClass(String url, String data
            , Class<T> responseType, HttpHeaders headers, Object ...uriVariables) {
        headers = Optional.ofNullable(headers).orElse(RestTemplateUtil.headers);
        return instance.exchange(url
                , HttpMethod.HEAD, new HttpEntity<>(data, headers)
                , responseType, uriVariables);
    }

    /**
     * 原生http请求
     */
    public static <T> T trace(String url, String data, ParameterizedTypeReference<T> responseType
            , HttpHeaders headers, Object ...uriVariables) {
        headers = Optional.ofNullable(headers).orElse(RestTemplateUtil.headers);
        return instance.exchange(url
                , HttpMethod.TRACE, new HttpEntity<>(data, headers)
                , responseType, uriVariables).getBody();
    }

    /**
     * 原生http请求
     */
    public static <T> ResponseEntity<T> traceByClass(String url, String data, Class<T> responseType
            , HttpHeaders headers, Object ...uriVariables) {
        headers = Optional.ofNullable(headers).orElse(RestTemplateUtil.headers);
        return instance.exchange(url
                , HttpMethod.TRACE, new HttpEntity<>(data, headers)
                , responseType, uriVariables);
    }

    /**
     * 原生http请求
     */
    public static <T> T patch(String url, String data, ParameterizedTypeReference<T> responseType
            , HttpHeaders headers, Object ...uriVariables) {
        headers = Optional.ofNullable(headers).orElse(RestTemplateUtil.headers);
        return instance.exchange(url
                , HttpMethod.PATCH, new HttpEntity<>(data, headers)
                , responseType, uriVariables).getBody();
    }

    /**
     * 原生http请求
     */
    public static <T> ResponseEntity<T> patchByClass(String url, String data, Class<T> responseType
            , HttpHeaders headers, Object ...uriVariables) {
        headers = Optional.ofNullable(headers).orElse(RestTemplateUtil.headers);
        return instance.exchange(url
                , HttpMethod.PATCH, new HttpEntity<>(data, headers)
                , responseType, uriVariables);
    }

    /**
     * 原生http请求
     */
    public static <T> T options(String url, String data, ParameterizedTypeReference<T> responseType
            , HttpHeaders headers, Object ...uriVariables) {
        headers = Optional.ofNullable(headers).orElse(RestTemplateUtil.headers);
        return instance.exchange(url
                , HttpMethod.OPTIONS, new HttpEntity<>(data, headers)
                , responseType, uriVariables).getBody();
    }

    /**
     * 原生http请求
     */
    public static <T> ResponseEntity<T> optionsByClass(String url, Class<T> responseType
            , HttpHeaders headers, Object ...uriVariables) {
        headers = Optional.ofNullable(headers).orElse(RestTemplateUtil.headers);
        return instance.exchange(url
                , HttpMethod.OPTIONS, new HttpEntity<>(headers)
                , responseType, uriVariables);
    }

    /**
     * 文件上传转发
     */
    public static <T> T postMultipartFile(String url, HttpServletRequest request ,Map<String, Object> body
            , ParameterizedTypeReference<T> reference, Object... uriVariables) {
        HttpHeaders headers = getRequestHeader(request);
        headers.remove("Content-Type");
        headers.remove("ContentType");
        headers.setContentType(MediaType.parseMediaType("multipart/form-data;charset=UTF-8"));
        if(body == null || body.isEmpty()){
            return instance.exchange(param(request,url)
                    , HttpMethod.POST, new HttpEntity<>(body, headers), reference, uriVariables).getBody();
        }
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        String tempPath = "logs";
        // 保存临时文件
        List<String> tempList = new ArrayList<>();
        Object obj;
        for(String key : body.keySet()){
            obj = body.get(key);
            try {
                tempPath = createDir(tempPath);
                if(obj instanceof MultipartFile){
                    MultipartFile file = (MultipartFile) obj;
                    String tempFilePath = tempPath + File.separator + file.getOriginalFilename();
                    FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(tempFilePath));
                    tempList.add(tempFilePath);
                    FileSystemResource resource = new FileSystemResource(tempFilePath);
                    param.add(key, resource);
                    headers = covertFileHeaders(headers, param, request);
                }else if(obj instanceof MultipartFile[]){
                    MultipartFile[] files = (MultipartFile[]) obj;
                    for(MultipartFile file : files){
                        String tempFilePath = tempPath + File.separator + file.getOriginalFilename();
                        FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(tempFilePath));
                        tempList.add(tempFilePath);
                        FileSystemResource resource = new FileSystemResource(tempFilePath);
                        param.add(key, resource);
                    }
                    headers = covertFileHeaders(headers, param, request);
                }else{
                    param.add(key, obj);
                }
            } catch (Exception e) {
                log.error("postMultipartFileError", e);
            }
        }
        try {
            return instance.exchange(paramGet(request,url),
                    HttpMethod.POST, new HttpEntity<>(param, headers), reference, uriVariables).getBody();
        } finally {
            deleteLocalTempFiles(tempList);
        }
    }

    /**
     * 组装文件里面自带的file属性
     */
    public static HttpHeaders covertFileHeaders(HttpHeaders headers, MultiValueMap<String, Object> map
            , HttpServletRequest request){
        headers = headers == null?new HttpHeaders():headers;
        if(map == null || map.isEmpty()){
            return headers;
        }
        String tmp;
        for(String item : map.keySet()){
            tmp = request.getHeader(item);
            if(tmp != null && tmp.isEmpty()){
                headers.add(item, tmp);
            }
        }
        return headers;
    }

    /**
     * 创建目录
     */
    private static String createDir(String tempPath) throws IOException {
        File file = new File(tempPath);
        if (!file.exists() && file.mkdirs()) {
            log.info("create {} dir success.", tempPath);
        }
        return file.getCanonicalPath();
    }

    /**
     * 删除临时文件
     */
    private static void deleteLocalTempFiles(List<String> tempList) {
        if (!CollectionUtils.isEmpty(tempList)) {
            for (String fileName : tempList) {
                File file = new File(fileName);
                Path path = file.toPath();
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    log.info("deleteLocalTempFilesError", e);
                }
            }
        }
    }

    /**
     * 组装Get里面的参数
     */
    private static String paramGet(HttpServletRequest request, String url) {
        if(url == null || url.isEmpty()){
            return "";
        }
        if(request.getQueryString() == null || request.getQueryString().isEmpty()){
            return url;
        }
        url = url.contains("?")?(url + "&"):(url+"?");
        return url + UrlUtils.getQueryString(request.getQueryString(), false);
    }
    
    /**
     * 获取文件后转发给前端
     */
	public static void downloadFile(HttpServletResponse response, Resource resource) {
		if (resource != null) {
			try(InputStream is = resource.getInputStream();OutputStream os = response.getOutputStream()
				;BufferedInputStream bis = new BufferedInputStream(is)) {
				int len;
				byte[] buff = new byte[1024];
				while ((len = bis.read(buff)) != -1) {
					os.write(buff, 0, len);
					os.flush();
				}
			} catch(Exception e) {
				log.info("download file error：", e);
				throw new RuntimeException("文件下载失败：" + e.getMessage());
			}
		}
	}

    /**
     * 获取请求体
     */
    public static HttpEntity<Object> getHttpEntity(HttpServletRequest request, HttpHeaders headers, Object body) {
        try {
            if(Objects.nonNull(body)){
                return new HttpEntity<>(body, headers);
            }
            byte[] bytes = StreamUtils.copyToByteArray(request.getInputStream());
            return new HttpEntity<>(bytes, headers);
        } catch (Exception e) {
            log.error("getHttpEntityError:", e);
            return new HttpEntity<>(headers);
        }
    }
}
