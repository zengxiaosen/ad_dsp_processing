package Learning.common;

/**
 * Created by Administrator on 2017/1/13.
 */
public interface IPredictor {
    public abstract double Predict(EncodedItem encodedItem, LearningModel model);
}
