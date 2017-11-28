package IFB.domain;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miluo on 2017/9/5.
 */
public class DpiConfigTest {

    public static void main(String[] args) {
        Gson gson = new Gson();

        DpiConfig dpiConfig = new DpiConfig();
        dpiConfig.setGlobalIntervalDay(1);
        dpiConfig.setFilterUrlSuffix("css|js|gif|bmp|jpeg|tiff|psd|png|svg|pcx|wmf|emf|lic|tga|rle|doc");
        dpiConfig.setScoreStoragePath("scoreStoredPath");
        dpiConfig.setRangeDay(1);  //综合评分天数
        dpiConfig.setgWeight(11);
        dpiConfig.setAccessDataType("txt");
        dpiConfig.setUserProcessLogic("处理逻辑");
        dpiConfig.setUserStoragePath("用户存储目录");

        List<Position> positionList = new ArrayList<Position>();
        Position psition = new Position();
        psition.setUrlPosition(27);
        psition.setUserPhonePosition(1);
        psition.setNetworkType("3G");
        psition.setDpiPath("E:/data/beijing");
        positionList.add(psition);

        psition = new Position();
        psition.setUrlPosition(27);
        psition.setUserPhonePosition(1);
        psition.setNetworkType("4G");
        psition.setDpiPath("E:/data/beijing_4G");
        positionList.add(psition);


        //业务
        List<Business> businessList = new ArrayList<Business>();
        Business business = new Business();
        business.setBusinessName("业务名称A");
        business.setBusinessTag("业务tag");
        business.setThreshold(10);
        business.setWeight(2);
        businessList.add(business);

        business = new Business();
        business.setBusinessName("业务名称B");
        business.setBusinessTag("业务tag");
        business.setThreshold(10);
        business.setWeight(2);
        businessList.add(business);
        dpiConfig.setBusinessList(businessList);

        //省份
        List<Province> provinceList = new ArrayList<Province>();
        Province province = new Province();
        province.setProvinceName("北京");
        province.setIntervalDay(2);
        province.setProvinceNick("bj");
        province.setIntervalHour(1);
        province.setPeriod(4);
        province.setPositionList(positionList);

        List<UserRecomBusiness> userRecomBusinessList = new ArrayList<UserRecomBusiness>();
        UserRecomBusiness userRecomBusiness = new UserRecomBusiness();
        userRecomBusiness.setBusinessTag("业务tag");
        userRecomBusiness.setMsgRecomUser("短信发送人数");
        userRecomBusiness.setRangeDay("间隔天数");
        userRecomBusiness.setTelRecomUser("电话发送人数");
        userRecomBusinessList.add(userRecomBusiness);
        province.setUserRecomBusinessList(userRecomBusinessList);
        provinceList.add(province);
        dpiConfig.setProvinceList(provinceList);

        //通用
        List<BaseVal> baseValList = new ArrayList<BaseVal>();
        BaseVal baseVal = new BaseVal();
        baseVal.setVarName("参数一");
        baseVal.setWeight(10);
        baseValList.add(baseVal);
        dpiConfig.setBaseValList(baseValList);
        dpiConfig.setUserBaseRecomBusiness(userRecomBusiness);


        TelcomGainCustomer t = new TelcomGainCustomer();
        t.setApiKey("setApiKey");
        t.setHost("host");
        t.setIntervalTime(1L);
        t.setPort("808");
        t.setSecretkey("setSecretkey");

        dpiConfig.setTelcomGainCustomer(t);
        System.out.println(gson.toJson(dpiConfig));
    }


}
