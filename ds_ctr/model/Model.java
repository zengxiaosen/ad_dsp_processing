package model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/11.
 */
public abstract class Model implements Serializable{
    public abstract Model parse(String rawModel);
    public abstract double predict(double[] point, double label);
}
