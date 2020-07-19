package spring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 记住Request/Response 过滤器
 * 解决Request不能重复使用问题
 * byte[] body = StreamUtils.copyToByteArray(request.getInputStream());
 * return new HttpEntity<>(body, getAuthorization(request));
 * @author qpc
 */
@Component
@WebFilter(filterName = "crownFilter", urlPatterns = "/*")
public class MemoryReqResFilter implements Filter {

	Logger log = LoggerFactory.getLogger(MemoryReqResFilter.class);
	
	/**
	 * 项目停止执行一次
	 */
	@Override
	@SuppressWarnings("EmptyMethod")
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse res,
						 FilterChain chain) throws ServletException, IOException {
		RequestWrapper req = null;
		if(request instanceof HttpServletRequest) {
			req = new RequestWrapper((HttpServletRequest) request);
		}
		if(req == null) {
			chain.doFilter(request, res);
		} else {
			try {
				chain.doFilter(req, res);
			} catch (Exception e) {
				chain.doFilter(request, res);
				log.error("MemoryReqRestFilterError",e);
			}
		}
	}

	/**
	 * 项目启动时执行一次
	 * @param config
	 */
	@Override
	@SuppressWarnings("EmptyMethod")
	public void init(FilterConfig config) {
	}
}
