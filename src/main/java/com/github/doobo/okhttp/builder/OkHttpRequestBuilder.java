package com.github.doobo.okhttp.builder;

import com.github.doobo.okhttp.OkHttpClientTools;
import com.github.doobo.okhttp.util.RegularUtils;
import okhttp3.Headers;
import okhttp3.Request;
import com.github.doobo.okhttp.response.IResponseHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@SuppressWarnings("unchecked")
public abstract class OkHttpRequestBuilder<T extends OkHttpRequestBuilder> {
    protected String mUrl;
    protected Object mTag;
    protected Map<String, String> mHeaders;

    protected OkHttpClientTools mOkHttpClientTools;

    /**
     * 异步执行
     * @param responseHandler 自定义回调
     */
    abstract void enqueue(final IResponseHandler responseHandler);

    public OkHttpRequestBuilder(OkHttpClientTools okHttpClientTools) {
        mOkHttpClientTools = okHttpClientTools;
    }

    /**
     * set url
     * @param url url
     * @return
     */
    public T url(String url)
    {
        this.mUrl = url;
        return (T) this;
    }

    /**
     * set tag
     * @param tag tag
     * @return
     */
    public T tag(Object tag)
    {
        this.mTag = tag;
        return (T) this;
    }

    /**
     * set headers
     * @param headers headers
     * @return
     */
    public T headers(Map<String, String> headers)
    {
        this.mHeaders = headers;
        return (T) this;
    }

    /**
     * set one header
     * @param key header key
     * @param val header val
     * @return
     */
    public T addHeader(String key, String val)
    {
        if (this.mHeaders == null)
        {
            mHeaders = new LinkedHashMap<>();
        }
        mHeaders.put(key, val);
        return (T) this;
    }

    /**
     * append headers into builder
     * @param builder
     * @param headers
     */
    protected void appendHeaders(Request.Builder builder, Map<String, String> headers) {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers == null || headers.isEmpty()) return;

        for (String key : headers.keySet()) {
            headerBuilder.add(key, headers.get(key));
        }
        builder.headers(headerBuilder.build());
    }

    /**
     * append params to url
     * @param url
     * @param params
     * @return
     */
    public String appendParams(String url, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        if(RegularUtils.hasWenHao(url)){
            sb.append(url+"&");
        }else{
            sb.append(url + "?");
        }
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                try {
                    sb.append(key).append("=").append(URLEncoder.encode(params.get(key),"UTF-8")).append("&");
                } catch (UnsupportedEncodingException e) {
                    sb.append(key).append("=").append(params.get(key)).append("&");
                }
            }
        }

        sb = sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * append params to url
     * @param url
     * @param params
     * @return
     */
    public String appendParamArr(String url, Map<String, String[]> params) {
        StringBuilder sb = new StringBuilder();
        if(RegularUtils.hasWenHao(url)){
            sb.append(url+"&");
        }else{
            sb.append(url + "?");
        }
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                try {
                    String[] ss = params.get(key);
                    if(ss == null || ss.length == 0){
                        continue;
                    }
                    for(String st : ss){
                        sb.append(key).append("=").append(URLEncoder.encode(st,UTF_8.name())).append("&");
                    }
                } catch (UnsupportedEncodingException e) {
                    sb.append(key).append("=").append(params.get(key)).append("&");
                }
            }
        }
        sb = sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
