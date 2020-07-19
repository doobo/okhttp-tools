package spring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 监听restTemplate的请求和返回
 *
 */
public class HttpRequestInterceptor implements ClientHttpRequestInterceptor {
    
    Logger log = LoggerFactory.getLogger(HttpRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        traceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        traceResponse(response);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
        log.info("===========================request begin================================================");
        log.info("URI         : {}", request.getURI());
        log.info("Method      : {}", request.getMethod());
        log.info("Headers     : {}", request.getHeaders());
        log.info("Request body: {}", new String(body, StandardCharsets.UTF_8.name()));
        log.info("==========================request end================================================");
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        StringBuilder inputStringBuilder = new StringBuilder();
		if(response != null) {
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8.name()))) {
				String line = bufferedReader.readLine();
				while (line != null) {
					inputStringBuilder.append(line);
					inputStringBuilder.append('\n');
					line = bufferedReader.readLine();
				}
			}
			log.info("============================response begin==========================================");
			log.info("Status code  : {}", response.getStatusCode());
			log.info("Status text  : {}", response.getStatusText());
			log.info("Headers      : {}", response.getHeaders());
			log.info("Response body: {}", inputStringBuilder.toString());
			log.info("=======================response end=================================================");
		}
    }
}
