package Learning.common;

/**
 * Created by Administrator on 2017/1/13.
 */
public class LinearEncoder implements IFeatureEncoder {

    public EncodedFeature EncodeFeature(Feature feature)
    {
        EncodedFeature newFeature = new EncodedFeature();
        newFeature.FeatureId = feature.getFeatureSet().GetFeatureSetId() *1000 + feature.getLocalFeatureId();
        newFeature.FeatureValue = feature.getFeatureValue();
        return newFeature;
    }
}
