package IFB.utils;

import IFB.domain.TelcomGainCustomer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * UserRecommender抽取工具类.
 * Created by CainGao on 2017/8/29.
 */
public class UserRecommenderUtil {


    private static TelcomGainCustomer telcomGainCustomer;


    /**
     * 解析需发短信用户的停止范围
     */
    public static Integer parseMsgMaxDay(String str) {
        String[] size = str.split("-");
        return Integer.parseInt(size[1]);
    }

    /**
     * 解析需发短信用户的开启范围
     */
    public static Integer parseMsgMinDay(String str) {
        String[] size = str.split("-");
        return Integer.parseInt(size[0]);
    }

    /**
     * 根据配置的基础目录与当前的时间格式,业务,后缀等生成文件路径
     */
    public static String pathByDayFormat(String basePath, String dateformat,String hour, String bussName, String suffix) {
        StringBuffer sb = new StringBuffer();
        sb.append(basePath + "/" + dateformat + "/" + hour + "/"+ bussName + "/" + suffix);
        return sb.toString();
    }

    /**
     * 根据业务获取到文件数组字符串
     * 限制,只有当文件夹存在时才会返回数据
     */
    public static String paths(String path, String rangeDateStr,String hour, String bussName, String suffix) {
        // 获取到当前时间
        Date now = new Date();
        int rangeDay = Integer.parseInt(rangeDateStr);
        List<Date> dates = DateUtil.rangeDate(now, rangeDay);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        StringBuffer sb = new StringBuffer();
        for (Date date : dates) {
            String hdfspath = pathByDayFormat(path, sdf.format(date),hour, bussName, suffix);
            if (HdfsUtil.exists(pathByDayFormat(path, sdf.format(date),hour, bussName, suffix))) {
                sb.append(hdfspath + "/*/*,");
            }
        }
        if (sb.toString().length() < 1)
            return "";
        return sb.toString().substring(0, sb.toString().length() - 1);
    }
}
