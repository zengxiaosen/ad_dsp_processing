package Learning.common;



import java.util.Vector;

/**
 * Created by Administrator on 2017/1/13.
 */
public  final class DataItem {
    private static int _defaultSize =1000;
    public int ItemId;
    public int TargetValue;
    public int PredictValue;
    public Vector<Feature> Features = new Vector<Feature>(_defaultSize);

    public void AddFeatures(Vector<Feature> newFeatures )
    {
        Features.addAll(newFeatures);
    }
}
