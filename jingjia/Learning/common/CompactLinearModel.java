package Learning.common;

/**
 * Created by Administrator on 2017/1/13.
 */
public class CompactLinearModel implements LearningModel {
    private double[] _weights;
    public double[] GetWeights(){
        return _weights;
    }
    public static CompactLinearModel CreateModelFromFile(String filePath){
        CompactLinearModel model = new CompactLinearModel();
        return model;
    }
}
