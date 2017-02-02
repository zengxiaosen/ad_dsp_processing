package utils.model.xgboost;

import utils.sample.Sample;

/**
 * Created by Administrator on 2017/1/17.
 */
public abstract interface Model {
    public abstract void loadModel(String rawModel);

    /*
    predict score, return 0.0 if data is wrong
    参数:sample
    return
     */
    public abstract double predict(Sample sample);
}
