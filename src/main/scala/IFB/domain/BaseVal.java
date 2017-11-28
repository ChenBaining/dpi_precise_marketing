package IFB.domain;

import java.io.Serializable;

/**
 * Created by miluo on 2017/9/5.
 */
public class BaseVal implements Serializable {
    /**
     * 名称
     */
    private String varName;

    /**
     * 权重
     */
    private double weight;


    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
