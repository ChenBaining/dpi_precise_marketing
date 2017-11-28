/**
 * Copyright (C), 2015-2017, XXX有限公司
 * FileName: ConfigUtils
 * Author:   zc
 * Date:     2017/9/5 17:28
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package IFB.utils;

import IFB.domain.DpiConfig;
import IFB.domain.UrlConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author zc
 * @create 2017/9/5
 * @company infinibase
 */
public class ConfigUtils {

    //系统配置文件
    private volatile static String configPath;
    private volatile static DpiConfig dpiConfig;

    //url配置文件
    private volatile static String urlConfigPath;
    private volatile static List<UrlConfig> urlConfig;

    //增加全局日期变量  在这个项目中作废
    @Deprecated
    private static String dateStr;

    public static String getDateStr() {
        return dateStr;
    }

    public static void setDateStr(String dateStr) {
        ConfigUtils.dateStr = dateStr;
    }

    /**
     * 给 配置文件外部路劲 赋值
     */
    public static void setConfigPath(String path) {
        try {
            configPath = path;
            if (dpiConfig == null) {
                Gson gson = new Gson();
                String config = IOUtil.reader(configPath, "utf-8");
                dpiConfig = gson.fromJson(config, DpiConfig.class);
            }
        } catch (Exception e) {
            System.out.println("给 配置文件外部路劲 赋值时出错！");
            e.printStackTrace();
        }
    }

    /**
     * 给 url配置文件外部路劲 赋值
     */
    public static void setUrlConfigPath(String path) {
        try {
            urlConfigPath = path;
            if (urlConfig == null) {
                Gson gson = new Gson();
                String config = IOUtil.reader(urlConfigPath, "utf-8");
                urlConfig = gson.fromJson(config, new TypeToken<List<UrlConfig>>() {
                }.getType());
            }
        } catch (Exception e) {
            System.out.println("给 url配置文件外部路劲 赋值时出错！");
            e.printStackTrace();
        }
    }

    /**
     * 获取url配置文件集合
     */
    public static List<UrlConfig> getUrlConfig() {
        try {
            if (urlConfig == null) {
                synchronized (ConfigUtils.class) {
                    if (urlConfig == null) {
                        String config = IOUtil.reader(urlConfigPath, "utf-8");
                        Gson gson = new Gson();
                        urlConfig = gson.fromJson(config, new TypeToken<List<UrlConfig>>() {
                        }.getType());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("获取url配置文件集合时出错！");
            e.printStackTrace();
        }
        return urlConfig;
    }

    /**
     * 获取系统配置文件
     *
     * @return
     */
    public static DpiConfig getDpiConfig() {
        try {
            if (dpiConfig == null) {
                synchronized (ConfigUtils.class) {
                    if (dpiConfig == null) {
                        String config = IOUtil.reader(configPath, "utf-8");
                        Gson gson = new Gson();
                        dpiConfig = gson.fromJson(config, DpiConfig.class);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("获取系统配置文件时出错！");
            e.printStackTrace();
        }
        return dpiConfig;
    }
}