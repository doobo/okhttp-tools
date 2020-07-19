package spring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * com常用参数
 * @author qpc
 */
@ConfigurationProperties(prefix = "vip.ipav")
public class BasicParams {

	private Integer total;

	private Integer route;

	private Integer requestTimeout;

	private Integer connectTimeout;

	private Integer readTimeout;

	boolean restDebug;

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getRoute() {
		return route;
	}

	public void setRoute(Integer route) {
		this.route = route;
	}

	public Integer getRequestTimeout() {
		return requestTimeout;
	}

	public void setRequestTimeout(Integer requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	public Integer getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public Integer getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}

	public boolean isRestDebug() {
		return restDebug;
	}

	public void setRestDebug(boolean restDebug) {
		this.restDebug = restDebug;
	}
}
