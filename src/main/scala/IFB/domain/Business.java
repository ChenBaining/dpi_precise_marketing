package IFB.domain;


import java.io.Serializable;

/**
 * 筛选用户所需的配置
 */
public class Business implements Serializable {

    /**
     * 业务名称
     */
    private String businessName;

    /**
     * 权重
     */
    private double weight;

    /**
     * 业务标签
     */
    private String businessTag;

    /**
     * 阈值
     */
    private int threshold;


    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getBusinessTag() {
        return businessTag;
    }

    public void setBusinessTag(String businessTag) {
        this.businessTag = businessTag;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
