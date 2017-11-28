package IFB.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liam on 2017/10/17.
 */
public class LogUtil {

    public static void printLog(String className ,String msg){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String logTime = sdf.format(new Date());
        String logStr = "[IFB] " + logTime + " " + className.replace("$","") + ": " + msg;
        System.out.println(logStr);
    }


    public static void main(String[] args){
        printLog("LogUtil","test log");
    }
}
