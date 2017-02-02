package FeatureExtractor.Framework;

import Learning.common.Feature;
import Learning.common.FeatureSetInfo;

import java.util.Vector;

/**
 * Created by Administrator on 2017/1/13.
 */
public interface IFeatureHandler {
    public abstract Vector<Feature> ExtractFeature(DataInstance dataInst);
    public abstract FeatureSetInfo GetSupportedFeatureSet();
}
