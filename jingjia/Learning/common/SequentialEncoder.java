package Learning.common;

/**
 * Created by Administrator on 2017/1/13.
 */
public class SequentialEncoder implements IFeatureEncoder {
    public EncodedFeature EncodeFeature(Feature feature) {
        EncodedFeature encodedFeature = new EncodedFeature();
        encodedFeature.FeatureId = feature.getLocalFeatureId();
        return encodedFeature;
    }
}
