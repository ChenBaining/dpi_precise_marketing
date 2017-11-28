package IFB.domain;

import java.io.Serializable;

/**
 * 用户业务推荐策略
 */
public class UserRecomBusiness implements Serializable {
    /**
     * 业务标签
     */
    private String businessTag;

    /**
     * 间隔时间
     */
    private String rangeDay;

    /**
     * 电话推荐用户数量
     */
    private String telRecomUser;

    /**
     * 短信推荐用户数量
     */
    private String msgRecomUser;
    /**
     * key 多次执行按小时提升倍率
     */
    private long keyHourRate;

    public String getBusinessTag() {
        return businessTag;
    }

    public void setBusinessTag(String businessTag) {
        this.businessTag = businessTag;
    }

    public String getRangeDay() {
        return rangeDay;
    }

    public void setRangeDay(String rangeDay) {
        this.rangeDay = rangeDay;
    }

    public String getTelRecomUser() {
        return telRecomUser;
    }

    public void setTelRecomUser(String telRecomUser) {
        this.telRecomUser = telRecomUser;
    }

    public String getMsgRecomUser() {
        return msgRecomUser;
    }

    public void setMsgRecomUser(String msgRecomUser) {
        this.msgRecomUser = msgRecomUser;
    }

    public long getKeyHourRate() {
        return keyHourRate;
    }

    public void setKeyHourRate(long keyHourRate) {
        this.keyHourRate = keyHourRate;
    }
}
