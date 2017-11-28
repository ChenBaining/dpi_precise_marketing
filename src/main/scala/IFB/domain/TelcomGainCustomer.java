package IFB.domain;

import java.io.Serializable;

/**
 * 电信获客平台
 * Created by CainGao on 2017/9/6.
 */
public class TelcomGainCustomer  implements Serializable {
    /**
     * appkey
     */
    private String apiKey;
    /**
     * secretkey
     */
    private String secretkey;
    /**
     * host
     */
    private String host;
    /**
     * port
     */
    private String port;

    /**
     * token的有效间隔时间
     */
    private long intervalTime;
    /**
     * 一次推送用户数量
     */
    private int pushUserSize;
    /**
     * 每批用户推送的个数
     */
    private long pushBatchSize;
    /**
     * 推荐用户的URL
     */
    private String gainUserSendUrl;
    /**
     * sleep间隔时间.
     */
    private long apiRequestDelay;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretkey() {
        return secretkey;
    }

    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public long getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(long intervalTime) {
        this.intervalTime = intervalTime;
    }

    public String getGainUserSendUrl() {
        return gainUserSendUrl;
    }

    public void setGainUserSendUrl(String gainUserSendUrl) {
        this.gainUserSendUrl = gainUserSendUrl;
    }

    public int getPushUserSize() {
        return pushUserSize;
    }

    public void setPushUserSize(int pushUserSize) {
        this.pushUserSize = pushUserSize;
    }

    public long getApiRequestDelay() {
        return apiRequestDelay;
    }

    public void setApiRequestDelay(long apiRequestDelay) {
        this.apiRequestDelay = apiRequestDelay;
    }

    public long getPushBatchSize() {
        return pushBatchSize;
    }

    public void setPushBatchSize(long pushBatchSize) {
        this.pushBatchSize = pushBatchSize;
    }
}
