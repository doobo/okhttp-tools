package com.github.doobo.okhttp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URLEncoder;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * url相关工具
 */
public abstract class UrlUtils {

    private static final Logger log = LoggerFactory.getLogger(UrlUtils.class);
    
    /**
     * 对象转查询URL
     */
    public static String getBeanString(Object obj){
        if(Objects.isNull(obj)){
            return "";
        }
        return multiValueMapToUrl(beanToMap(obj), true);
    }

    /**
     * 对象转查询URL
     */
    public static String getBeanString(Object obj, boolean encode){
        if(Objects.isNull(obj)){
            return "";
        }
        return multiValueMapToUrl(beanToMap(obj), encode);
    }

    /**
     * 获取查询字符串
     */
    public static String getQueryString(String str, boolean encode) {
        if ( str == null ||  str.isEmpty()){
            return "";
        }
        log.debug(str);
        MultiValueMap<String, String> map = stringToMap(str);
        return multiValueMapToUrl(map, encode);
    }

    /**
     * 获取查询字符串
     */
    public static String getQueryString(String str) {
        return getQueryString(str, true);
    }

    /**
     * URL参数转Map
     */
    public static MultiValueMap<String, String> stringToMap(String str){
        //判断str是否有值
        if (null == str || "".equals(str)){
            return null;
        }
        //根据&截取
        String[] strings = str.split("&");
        MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
        //循环加入map
        for (String string : strings) {
            String[] strArray = string.split("=");
            map.add(strArray[0], strArray[1]);
        }
        return map;
    }

    /**
     * 对象转Map
     */
    public static MultiValueMap<String, String> beanToMap(Object obj){
        if(Objects.isNull(obj)){
            return null;
        }
        Map<Object, Object> pros = new HashMap<>();
        if(obj instanceof Map){
            //noinspection SingleStatementInBlock
            pros = (Map) obj;
        }else {
            //noinspection SingleStatementInBlock
            pros.putAll(BeanMap.create(obj));
        }
        //根据&截取
        MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
        pros.forEach((k , v) ->{
            if(v instanceof List){
                //noinspection SingleStatementInBlock
                ((List) v).forEach(m -> map.add(String.valueOf(k), String.valueOf(m)));
            }else if(v instanceof Set){
                //noinspection SingleStatementInBlock
                ((Set) v).forEach(m -> map.add(String.valueOf(k), String.valueOf(m)));
            } else{
                map.add(String.valueOf(k), String.valueOf(v));
            }
        });
        return map;
    }

    /**
     * 组装Get参数
     */
    public static String multiValueMapToUrl(MultiValueMap<String, String> map, boolean encode) {
        if(map == null || map.isEmpty()){
            return "";
        }
        Map<String,String[]> rMap = new HashMap<>();
        map.keySet().stream().filter(Objects::nonNull)
                .forEach(m->{
                    List<String> tmp = map.get(m);
                    if(tmp != null){
                        rMap.put(m, tmp.toArray(new String[0]));
                    }
                });
        return getMapArrToString(rMap, encode);
    }

    /**
     * 获取请求地址中的某个参数
     */
    public static String getParam(String url, String name) {
        return urlSplit(url).getFirst(name);
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     * @param url url地址
     * @return url请求参数部分
     */
    private static String truncateUrlPage(String url) {
        String strAllParam = null;
        String[] arrSplit;
        url = url.trim().toLowerCase();
        arrSplit = url.split("[?]");
        if (url.length() > 1) {
            if (arrSplit.length > 1) {
                for (int i = 1; i < arrSplit.length; i++) {
                    strAllParam = arrSplit[i];
                }
            }
        }
        return strAllParam;
    }

    /**
     * 将参数存入map集合
     * @param url  url地址
     * @return url请求参数部分存入map集合
     */
    public static MultiValueMap<String, String> urlSplit(String url) {
        MultiValueMap<String, String> mapRequest = new LinkedMultiValueMap<>();
        String[] arrSplit = null;
        String strUrlParam = truncateUrlPage(url);
        if (strUrlParam == null) {
            return mapRequest;
        }
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");
            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.add(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                if (!Objects.equals(arrSplitEqual[0],"")) {
                    //只有参数没有值，不加入
                    mapRequest.add(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    /**
     * 字段排序,并编码拼接字符串
     */
    public static String getMapArrToString(Map<String, String[]> map, boolean encode) {
        if(map == null || map.isEmpty()){
            return "";
        }
        Set<String> keySet = map.keySet();
        String[] keyArray = keySet.toArray(new String[0]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        String key;
        String[] ss;
        for (String s : keyArray) {
            key = s;
            ss = map.get(key);
            if (ss == null || ss.length == 0) {
                continue;
            }
            //同名参数,再对值排序
            Arrays.sort(ss);
            for (String st : ss) {
                if (st != null) {
                    try {
                        String dv = encode? URLEncoder.encode(st, UTF_8.name())
                                .replace("*","%2A")
                                .replace("+","%20")
                                .replace("%7E","~"): st;
                        sb.append(key).append("=").append(dv).append("&");
                    } catch (Exception e) {
                        log.error("encodeError:", e);
                        sb.append(key).append("=").append(st).append("&");
                    }
                }
            }
        }
        //去除最后的'&'符号
        if(sb.toString().endsWith("&")){
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
