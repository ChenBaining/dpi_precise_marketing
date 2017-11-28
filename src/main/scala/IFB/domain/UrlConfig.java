package IFB.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * URL信息的配置
 * Created by miluo on 2017/8/30.
 */
public class UrlConfig implements Serializable {

    /**
     * tag 表明这一个批次是什么类型
     */
    private String tag;

    /**
     * 整个批次的全局权重
     */
    private double globalWeights;

    /**
     * 引入所有URL到同一个批次中
     */
    private List<UrlSingleConfig> urlSingleConfig = new ArrayList<UrlSingleConfig>();

    public double getGlobalWeights() {
        return globalWeights;
    }

    public void setGlobalWeights(double globalWeights) {
        this.globalWeights = globalWeights;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<UrlSingleConfig> getUrlSingleConfig() {
        return urlSingleConfig;
    }

    public void setUrlSingleConfig(List<UrlSingleConfig> urlSingleConfig) {
        this.urlSingleConfig = urlSingleConfig;
    }
}
