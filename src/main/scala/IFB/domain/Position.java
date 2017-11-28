package IFB.domain;

import java.io.Serializable;

/**
 * Created by miluo on 2017/9/5.
 */
public class Position implements Serializable {


    /**
     * 网络类型 3G 4G
     */
    private String networkType;

    /**
     * dpi路径
     */
    private String dpiPath;

    /**
     * dpi新路径
     */
    private String newDpiPath;

    /**
     * URL地址的位置
     */
    private int urlPosition;

    /**
     * user-agent的位置
     */
    private int userAgentPosition;

    /**
     * 用户手机号
     */
    private int userPhonePosition;

    public int getUserAgentPosition() {
        return userAgentPosition;
    }

    public void setUserAgentPosition(int userAgentPosition) {
        this.userAgentPosition = userAgentPosition;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getDpiPath() {
        return dpiPath;
    }

    public void setDpiPath(String dpiPath) {
        this.dpiPath = dpiPath;
    }

    public int getUrlPosition() {
        return urlPosition;
    }

    public void setUrlPosition(int urlPosition) {
        this.urlPosition = urlPosition;
    }

    public int getUserPhonePosition() {
        return userPhonePosition;
    }

    public void setUserPhonePosition(int userPhonePosition) {
        this.userPhonePosition = userPhonePosition;
    }

    public String getNewDpiPath() {
        return newDpiPath;
    }

    public void setNewDpiPath(String newDpiPath) {
        this.newDpiPath = newDpiPath;
    }
}
