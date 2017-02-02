package ds.test;

import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/1/13.
 */
public abstract class Model {
    public String modelName;
    public abstract Model parse(String rawModel);
    /*
    predict score, return 0.0 if data is wrong
     */
    public abstract double predict(Map<Integer, Double> point);

    public abstract Set<Integer> getAvailableFeatureIndex();
}
