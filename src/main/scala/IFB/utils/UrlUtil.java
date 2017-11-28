package IFB.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * url工具类
 * Created by miluo on 2017/8/31.
 */
public class UrlUtil implements Serializable {

    /**
     * 获取url的host地址
     *
     * @param url
     * @return
     */
    public static String getHost(String url) {
        if (!(StringUtils.startsWithIgnoreCase(url, "http://") || StringUtils.startsWithIgnoreCase(url, "https://"))) {
            if (url.startsWith("//")) {
                url = url.replaceFirst("//", "");
            }
            url = "http://" + url;
        }
        String returnVal = StringUtils.EMPTY;
        try {
            URI uri = new URI(url);
            returnVal = uri.getHost();
        } catch (Exception e) {
        }
        if ((StringUtils.endsWithIgnoreCase(returnVal, ".html") || StringUtils.endsWithIgnoreCase(returnVal, ".htm"))) {
            returnVal = StringUtils.EMPTY;
        }
        return returnVal;
    }

    /**
     * suffixes String suffixes="avi|mpeg|3gp|mp3|mp4|wav|jpeg|gif|jpg|png|apk|exe|pdf|rar|zip|docx|doc";
     * 匹配文件后缀名
     */
    public static boolean getUrlSuffix(String url, String suffixes) {
        boolean suffixCheck = false;
        Pattern pat = Pattern.compile("[\\w]+[\\.](" + suffixes + ")");//正则判断
        Matcher mc = pat.matcher(url);//条件匹配
        while (mc.find()) {
            //substring = mc.group();//截取文件名后缀名
            suffixCheck = true;
        }
        return suffixCheck;
    }

    /**
     * suffixes String suffixes="avi|mpeg|3gp|mp3|mp4|wav|jpeg|gif|jpg|png|apk|exe|pdf|rar|zip|docx|doc";
     * 匹配文件后缀名
     */
    public static boolean getUserAgent(String dpiUserAgentStr, String configUserAgent) {
        boolean suffixCheck = false;
        Pattern pat = Pattern.compile("(" + configUserAgent + ")[\\S]*");//正则判断
        Matcher mc = pat.matcher(dpiUserAgentStr);//条件匹配
        while (mc.find()) {
            //substring = mc.group();//截取文件名后缀名
            suffixCheck = true;
        }
        return suffixCheck;
    }
}
