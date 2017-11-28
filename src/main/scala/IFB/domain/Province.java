package IFB.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miluo on 2017/9/5.
 */
public class Province implements Serializable {

    /**
     * 名称
     */
    private String provinceName;

    /**
     * 省份简写
     */
    private String provinceNick;

    /**
     * 省份数据间隔天数
     */
    @Deprecated
    private int intervalDay;

    /**
     * 设置数据间隔小时
     */
    private int intervalHour;

    /**
     * 设置要几个小时的数据
     */
    private int period;

    /**
     * 用户业务推荐策略
     */
    private List<UserRecomBusiness> userRecomBusinessList;

    /**
     * 配置数据位置
     *
     * @return
     */
    private List<Position> positionList = new ArrayList<Position>();

    public int getIntervalHour() {
        return intervalHour;
    }

    public void setIntervalHour(int intervalHour) {
        this.intervalHour = intervalHour;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceNick() {
        return provinceNick;
    }

    public void setProvinceNick(String provinceNick) {
        this.provinceNick = provinceNick;
    }


    public int getIntervalDay() {
        return intervalDay;
    }

    public void setIntervalDay(int intervalDay) {
        this.intervalDay = intervalDay;
    }

    public List<UserRecomBusiness> getUserRecomBusinessList() {
        return userRecomBusinessList;
    }

    public void setUserRecomBusinessList(List<UserRecomBusiness> userRecomBusinessList) {
        this.userRecomBusinessList = userRecomBusinessList;
    }

    public List<Position> getPositionList() {
        return positionList;
    }

    public void setPositionList(List<Position> positionList) {
        this.positionList = positionList;
    }
}
