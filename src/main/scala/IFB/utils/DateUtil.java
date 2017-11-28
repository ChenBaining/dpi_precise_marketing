package IFB.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by CainGao on 2017/8/29.
 */
public class DateUtil {
    /**
     * 按天范围检索
     **/
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    /**
     * 计算时间往前推N天的日期
     */
    public static String dayConvertByInterval(int intervalDay) {
        //获取当天日期
        Calendar calendar = Calendar.getInstance(); //得到一个Calendar的实例
        calendar.setTime(new Date()); //设置时间为当前时间
        calendar.add(Calendar.DATE, (-intervalDay)); //当前日期减掉配置文件的天数
        //转成yyyyMMdd格式数据
        return sdf.format(calendar.getTime());
    }

    /**
     * 时间推算，往前推N小时
     *
     * @param intervalHour
     * @param period
     * @return
     */
    public static List<String> hourConverByInterval(int intervalHour, int period) {
        List<String> dateList = new ArrayList<String>();
        for (int i = intervalHour + 1; i <= intervalHour + period; i++) {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd,HH");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date()); //设置时间为当前时间
            calendar.add(Calendar.HOUR, (-i)); //当前日期减掉配置文件的天数 18
            dateList.add(fmt.format(calendar.getTime()));
        }
        return dateList;
    }

    /**
     * 获取当前小时
     */
    public static String getCurrentHour() {
        SimpleDateFormat fmt = new SimpleDateFormat("HH");
        Calendar calendar = Calendar.getInstance();
        return fmt.format(calendar.getTime());
    }

    /**
     * 获取给定日期的前n天的日期
     *
     * @param oneDate：给定日期
     * @param interval：间隔，1代表给定日期的昨天，-1代表给定日期的明天
     * @return
     */
    public static String getBeforeDateFormOneDate(String oneDate, int interval) {
        try {
            //将String日期转换为Date
            Date dt = sdf.parse(oneDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dt);
            calendar.add(Calendar.DATE, -interval);
            //转成格式化的日期
            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            System.out.println("日期转换错误。日期：" + oneDate + ",间隔：" + interval);
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 计算两个时间相差多少分钟
     * @param startTime
     * @param endTime
     * @return
     */
    public static long intervalMin(Date startTime,Date endTime){
        long diff = endTime.getTime() - startTime.getTime();
        long minute = diff / (1000*60);
        return minute;
    }
    /**
     * 计算时间相差天数的时间
     */
    public static Date dayTimeStamp(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + day);
        return calendar.getTime();
    }

    /**
     * 获取某天的0点时间
     */
    public static Date dayZero(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
        return zero;
    }

    /**
     * 获取两个时间之间的所有时间(Day)
     */
    public static List<Date> rangeDate(Date startDate, Date endDate) {
        List<Date> result = new ArrayList<Date>();
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        while (endCal.after(startCal)) {
            startCal.add(Calendar.DAY_OF_MONTH, 1);
            result.add(startCal.getTime());
        }
        return result;
    }

    /**
     * 获取今天的时间戳
     */
    public static String dateFormat(Date date) {
        return sdf.format(date);
    }

    /**
     * 获取当前时间几天内的时间范围  rangeDay 仅支持负数.
     */
    public static List<Date> rangeDate(Date startDate, int rangeDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_MONTH, rangeDay);
        return rangeDate(calendar.getTime(), startDate);
    }


}
