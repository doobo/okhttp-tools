package com.github.doobo.okhttp.spring;

import java.util.concurrent.TimeUnit;

/**
 * 相关配置属性
 */
public class OKHttpProperties implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Max Idle Connections
     */
    private Integer maxIdleConnections = 30;

    /**
     * 连接持续时间
     */
    private Long keepAliveDuration = 300L;

    /**
     * 复用时间单位
     */
    private TimeUnit keepAliveDurationTimeUnit = TimeUnit.MINUTES;

    /**
     * 连接超时时间
     */
    private Long connectTimeout = 10L;

    /**
     * 连接超时时间单位
     */
    private TimeUnit connectTimeoutTimeUnit = TimeUnit.SECONDS;

    /**
     * 写超时时间
     */
    private Long writeTimeout = 10L;

    /**
     * 写超时时间单位
     */
    private TimeUnit writeTimeoutTimeUnit = TimeUnit.SECONDS;

    /**
     * 读超时时间
     */
    private Long readTimeout = 20L;

    /**
     * 读超时时间单位
     */
    private TimeUnit readTimeoutTimeUnit = TimeUnit.SECONDS;

    /**
     * 线程配置
     */
    private int corePoolSize = Runtime.getRuntime().availableProcessors();

    /**
     * 最大线程数
     */
    private int maxPoolSize = Runtime.getRuntime().availableProcessors() * 4;

    public Integer getMaxIdleConnections() {
        return maxIdleConnections;
    }

    public void setMaxIdleConnections(Integer maxIdleConnections) {
        this.maxIdleConnections = maxIdleConnections;
    }

    public Long getKeepAliveDuration() {
        return keepAliveDuration;
    }

    public void setKeepAliveDuration(Long keepAliveDuration) {
        this.keepAliveDuration = keepAliveDuration;
    }

    public TimeUnit getKeepAliveDurationTimeUnit() {
        return keepAliveDurationTimeUnit;
    }

    public void setKeepAliveDurationTimeUnit(TimeUnit keepAliveDurationTimeUnit) {
        this.keepAliveDurationTimeUnit = keepAliveDurationTimeUnit;
    }

    public Long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public TimeUnit getConnectTimeoutTimeUnit() {
        return connectTimeoutTimeUnit;
    }

    public void setConnectTimeoutTimeUnit(TimeUnit connectTimeoutTimeUnit) {
        this.connectTimeoutTimeUnit = connectTimeoutTimeUnit;
    }

    public Long getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(Long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public TimeUnit getWriteTimeoutTimeUnit() {
        return writeTimeoutTimeUnit;
    }

    public void setWriteTimeoutTimeUnit(TimeUnit writeTimeoutTimeUnit) {
        this.writeTimeoutTimeUnit = writeTimeoutTimeUnit;
    }

    public Long getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public TimeUnit getReadTimeoutTimeUnit() {
        return readTimeoutTimeUnit;
    }

    public void setReadTimeoutTimeUnit(TimeUnit readTimeoutTimeUnit) {
        this.readTimeoutTimeUnit = readTimeoutTimeUnit;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }
}
