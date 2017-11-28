package IFB.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.List;

/**
 * 功能介绍:简易Http请求 复杂度:
 *
 * @author gaoweigong
 * @date 2016年9月20日
 * @modify POST 2016年9月20日
 */
public class HTTPUtils {

    private HTTPUtils() {
    }

    public static String postContent(String url, List<NameValuePair> pairs, String charset) throws Exception{
        PostMethod post = new PostMethod(url);
        NameValuePair[] data = {new BasicNameValuePair(pairs.get(0).getName(), pairs.get(0).getValue())};
        HttpClient client = new HttpClient();
        String response = "";
        try {
            int status = client.executeMethod(post);     //执行，模拟POST方法提交到服务器
            response = post.getResponseBodyAsString();
        } catch (HttpException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            post.releaseConnection();
        }
        return response;
    }

    /**
     * @return
     * @功能描述:简易get请求
     * @复杂度:
     * @创建人:gaoweigong
     * @创建时间:2016年11月22日 下午3:01:50
     */
    public static String get(String url) throws Exception{
        HttpClient httpClient = new HttpClient();                    //构造HttpClient的实例
        GetMethod getMethod = new GetMethod(url);                    //创建GET方法的实
        getMethod.setRequestHeader("Connection", "close");
        String response = "";
        try {
            int statusCode = httpClient.executeMethod(getMethod);      //执行getMethod
            response = getMethod.getResponseBodyAsString();            //读取服务器返回的页面代码，这里用的是字符读法
        } finally {                                                          //释放连接
            getMethod.releaseConnection();
            httpClient.getHttpConnectionManager().closeIdleConnections(0);
        }
        return response;
    }

}
