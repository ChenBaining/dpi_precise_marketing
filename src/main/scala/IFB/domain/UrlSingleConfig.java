package IFB.domain;

import java.io.Serializable;

/**
 * 单独一条url的配置信息
 * Created by miluo on 2017/8/30.
 */
public class UrlSingleConfig implements Serializable {

    /**
     * URL链接
     */
    private String url;

    /**
     * 权重
     */
    private Double weights;

    /**
     * 是什么类型 0 app 1 网页端
     */
    private int urlType;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getWeights() {
        return weights;
    }

    public void setWeights(Double weights) {
        this.weights = weights;
    }

    public int getUrlType() {
        return urlType;
    }

    public void setUrlType(int urlType) {
        this.urlType = urlType;
    }
}
