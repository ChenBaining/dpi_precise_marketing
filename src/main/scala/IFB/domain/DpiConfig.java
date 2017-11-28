package IFB.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by miluo on 2017/9/5.
 */
public class DpiConfig implements Serializable {

    /**
     * 间隔天数
     */
    private int globalIntervalDay;

    /**
     * RDD 联合分区设置个数
     */
    private int coalesceNumber;

    /**
     * 过滤URL后缀
     */
    private String filterUrlSuffix;

    /**
     * 需要匹配的user-agent
     */
    private String filterUserAgent;

    /**
     * 备份目录
     */
    private String scoreStoragePath;

    /**
     * 综合评分天数
     */
    private int rangeDay;

    /**
     * 日期衰减权重
     */
    private double gWeight;

    /**
     * 用户推荐目录
     */
    private String userStoragePath;

    /**
     * 处理逻辑
     */
    private String userProcessLogic;

    /**
     * 用户提取基本处理逻辑
     */
    private UserRecomBusiness userBaseRecomBusiness;

    /**
     * 业务逻辑
     */
    private List<Business> businessList;

    /**
     * 省份
     */
    private List<Province> provinceList;


    /**
     * 通用变量
     */
    private List<BaseVal> baseValList;

    /**
     * 用户业务推荐策略
     */
    private UserRecomBusiness userRecomBusiness;

    private TelcomGainCustomer telcomGainCustomer;
    /**
     * 访问数据形式 txt byte
     */
    private String accessDataType;

    /**
     * repartition的个数
     */
    private int repartitionSize;

    /**
     * 持久化级别
     */
    private String storageLevel;

    public int getGlobalIntervalDay() {
        return globalIntervalDay;
    }

    public void setGlobalIntervalDay(int globalIntervalDay) {
        this.globalIntervalDay = globalIntervalDay;
    }

    public int getCoalesceNumber() {
        return coalesceNumber;
    }

    public void setCoalesceNumber(int coalesceNumber) {
        this.coalesceNumber = coalesceNumber;
    }

    public String getFilterUrlSuffix() {
        return filterUrlSuffix;
    }

    public void setFilterUrlSuffix(String filterUrlSuffix) {
        this.filterUrlSuffix = filterUrlSuffix;
    }

    public String getFilterUserAgent() {
        return filterUserAgent;
    }

    public void setFilterUserAgent(String filterUserAgent) {
        this.filterUserAgent = filterUserAgent;
    }

    public String getScoreStoragePath() {
        return scoreStoragePath;
    }

    public void setScoreStoragePath(String scoreStoragePath) {
        this.scoreStoragePath = scoreStoragePath;
    }

    public int getRangeDay() {
        return rangeDay;
    }

    public void setRangeDay(int rangeDay) {
        this.rangeDay = rangeDay;
    }

    public double getgWeight() {
        return gWeight;
    }

    public void setgWeight(double gWeight) {
        this.gWeight = gWeight;
    }

    public String getUserStoragePath() {
        return userStoragePath;
    }

    public void setUserStoragePath(String userStoragePath) {
        this.userStoragePath = userStoragePath;
    }

    public String getUserProcessLogic() {
        return userProcessLogic;
    }

    public void setUserProcessLogic(String userProcessLogic) {
        this.userProcessLogic = userProcessLogic;
    }

    public UserRecomBusiness getUserBaseRecomBusiness() {
        return userBaseRecomBusiness;
    }

    public void setUserBaseRecomBusiness(UserRecomBusiness userBaseRecomBusiness) {
        this.userBaseRecomBusiness  = userBaseRecomBusiness;
    }

    public List<Business> getBusinessList() {
        return businessList;
    }

    public void setBusinessList(List<Business> businessList) {
        this.businessList = businessList;
    }

    public List<Province> getProvinceList() {
        return provinceList;
    }

    public void setProvinceList(List<Province> provinceList) {
        this.provinceList = provinceList;
    }

    public List<BaseVal> getBaseValList() {
        return baseValList;
    }

    public void setBaseValList(List<BaseVal> baseValList) {
        this.baseValList = baseValList;
    }

    public UserRecomBusiness getUserRecomBusiness() {
        return userRecomBusiness;
    }

    public void setUserRecomBusiness(UserRecomBusiness userRecomBusiness) {
        this.userRecomBusiness = userRecomBusiness;
    }

    public TelcomGainCustomer getTelcomGainCustomer() {
        return telcomGainCustomer;
    }

    public void setTelcomGainCustomer(TelcomGainCustomer telcomGainCustomer) {
        this.telcomGainCustomer = telcomGainCustomer;
    }

    public String getAccessDataType() {
        return accessDataType;
    }

    public void setAccessDataType(String accessDataType) {
        this.accessDataType = accessDataType;
    }

    public int getRepartitionSize() {
        return repartitionSize;
    }

    public void setRepartitionSize(int repartitionSize) {
        this.repartitionSize = repartitionSize;
    }

    public String getStorageLevel() {
        return storageLevel;
    }

    public void setStorageLevel(String storageLevel) {
        this.storageLevel = storageLevel;
    }
}
