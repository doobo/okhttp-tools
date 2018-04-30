package vip.ipav.okhttp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularUtils {
    /**
     * URL检查<br>
     * <br>
     * @param url     要检查的字符串<br>
     * @return boolean   返回检查结果<br>
     */
    public static boolean isUrl(String url) {
        if (url ==  null ){
            return   false ;
        }
        String regEx =  "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-"
                +  "Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{"
                +  "2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}"
                +  "[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|"
                +  "[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-"
                +  "4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0"
                +  "-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/"
                +  "[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$\\=~_\\-@]*)*$" ;
        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(url);
        return  matcher.matches();
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
