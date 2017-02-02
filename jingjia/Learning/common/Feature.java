package Learning.common;

/**
 * Created by Administrator on 2017/1/13.
 */
public final class Feature {
    private FeatureSetInfo _featureSet;
    private int _localFeatureId;
    private int _featureValue;
    public Feature(FeatureSetInfo featureSet,
                   int localFeatureId,
                   int featureValue)
    {
        _featureSet = featureSet;
        _localFeatureId = localFeatureId;
        _featureValue = featureValue;
    }
    public FeatureSetInfo getFeatureSet() {
        return _featureSet;
    }
    public int getLocalFeatureId() {
        return _localFeatureId;
    }
    public int getFeatureValue() {
        return _featureValue;
    }




}
