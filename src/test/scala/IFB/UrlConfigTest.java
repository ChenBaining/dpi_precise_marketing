package IFB;

import IFB.domain.UrlConfig;
import IFB.domain.UrlSingleConfig;
import IFB.utils.IOUtil;
import IFB.utils.UrlUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miluo on 2017/8/30.
 */
public class UrlConfigTest {

    public static void listUrlData() {
        //app信用贷款
        String config = IOUtil.reader("E:\\极兆数据\\数据\\app信用贷款.txt", "utf-8");
        System.out.println(config);
        Gson gson = new Gson();
        List<UrlConfig> urlConfigList = new ArrayList<UrlConfig>();
        UrlConfig urlConfig = new UrlConfig();
        urlConfig.setTag("A");
        urlConfig.setGlobalWeights(2);  //总体权重
        for (int i = 0; i <= config.split(",").length; i++) {
            try {
                UrlSingleConfig urlSingleConfig = new UrlSingleConfig();
                urlSingleConfig.setUrlType(1);
                urlSingleConfig.setUrl(config.split(",")[i]);
                urlSingleConfig.setWeights(1.5);  //单独权重
                urlConfig.getUrlSingleConfig().add(urlSingleConfig);
            } catch (Exception e) {
            }
        }

        //信用贷-低
        config = IOUtil.reader("E:\\极兆数据\\数据\\信用贷-201700921低.txt", "utf-8");
        System.out.println(config);
        for (int i = 0; i <= config.split(",").length; i++) {
            try {
                UrlSingleConfig urlSingleConfig = new UrlSingleConfig();
                urlSingleConfig.setUrlType(1);
                urlSingleConfig.setUrl(config.split(",")[i]);
                urlSingleConfig.setWeights(10.0);  //单独权重
                urlConfig.getUrlSingleConfig().add(urlSingleConfig);
            } catch (Exception e) {
            }
        }

        //信用贷-高
        config = IOUtil.reader("E:\\极兆数据\\数据\\信用贷-201700921高.txt", "utf-8");
        System.out.println(config);
        for (int i = 0; i <= config.split(",").length; i++) {
            try {
                UrlSingleConfig urlSingleConfig = new UrlSingleConfig();
                urlSingleConfig.setUrlType(1);
                urlSingleConfig.setUrl(config.split(",")[i]);
                urlSingleConfig.setWeights(12.0);  //单独权重
                urlConfig.getUrlSingleConfig().add(urlSingleConfig);
            } catch (Exception e) {
            }
        }
        urlConfigList.add(urlConfig);

        //现金贷
        config = IOUtil.reader("E:\\极兆数据\\数据\\现金贷-201700921.txt", "utf-8");
        System.out.println(config);
        urlConfig = new UrlConfig();
        urlConfig.setTag("B");
        urlConfig.setGlobalWeights(2);  //总体权重
        for (int i = 0; i <= config.split(",").length; i++) {
            try {
                UrlSingleConfig urlSingleConfig = new UrlSingleConfig();
                urlSingleConfig.setUrlType(1);
                urlSingleConfig.setUrl(config.split(",")[i]);
                urlSingleConfig.setWeights(1.0);  //单独权重
                urlConfig.getUrlSingleConfig().add(urlSingleConfig);
            } catch (Exception e) {
            }
        }

        //app现金贷
        config = IOUtil.reader("E:\\极兆数据\\数据\\app现金贷.txt", "utf-8");
        System.out.println(config);
        for (int i = 0; i <= config.split(",").length; i++) {
            try {
                UrlSingleConfig urlSingleConfig = new UrlSingleConfig();
                urlSingleConfig.setUrlType(1);
                urlSingleConfig.setUrl(config.split(",")[i]);
                urlSingleConfig.setWeights(1.0);  //单独权重
                urlConfig.getUrlSingleConfig().add(urlSingleConfig);
            } catch (Exception e) {
            }
        }
        urlConfigList.add(urlConfig);

        System.out.println(gson.toJson(urlConfigList));
    }

    public static void main(String[] args) {
        listUrlData();
        String dpiUserAgent = "苏宁易购 5.0.3 rv:5.0.3.2 (iPhone; iPhone OS 7.1.2; zh_CN) SNCLIENT";
        String configUserAgent = "京东金融|天猫旗舰店|苏宁易购";

        //boolean p = UrlUtil.getUrlSuffix("http://shmmsns.qpic.cn/mmsns/xxXsPVBKeQVopsGBt319NSXJ2kibPSlwTTPGoJnmsBl7RC7lNibJRYWJydJTofNIAibPuu6ib1DjvBI.jpg", "png|jpd");
        //System.out.println(p);

        //boolean p = UrlUtil.getUserAgent(dpiUserAgent, configUserAgent);
        //System.out.println(p);

    }

}
