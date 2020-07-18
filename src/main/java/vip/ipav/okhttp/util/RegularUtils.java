package vip.ipav.okhttp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularUtils {
    /**
     * URL检查
     * @param url 要检查的字符串
     * @return boolean 返回检查结果
     */
    @Deprecated
    public static boolean isUrl(String url) {
        return true;
    }

    /**
     * 是否有'?'字符
     * @param str
     * @return
     */
    public static boolean hasWenHao(String str){
        String regEx = ".*\\?.*";
        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(str);
        return  matcher.matches();
    }


}
