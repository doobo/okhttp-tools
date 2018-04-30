package vip.ipav.okhttp.builder;

import vip.ipav.okhttp.OkHttpClientTools;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class OkHttpRequestBuilderHasParam<T extends OkHttpRequestBuilderHasParam> extends OkHttpRequestBuilder<T> {

    protected Map<String, String> mParams;

    public OkHttpRequestBuilderHasParam(OkHttpClientTools okHttpClientTools) {
        super(okHttpClientTools);
    }

    /**
     * set Map params
     * @param params
     * @return
     */
    public T params(Map<String, String> params) {
        this.mParams = params;
        return (T) this;
    }

    /**
     * add param
     * @param key param key
     * @param val param val
     * @return
     */
    public T addParam(String key, String val) {
        if (this.mParams == null)
        {
            mParams = new LinkedHashMap<>();
        }
        mParams.put(key, val);
        return (T) this;
    }
}
