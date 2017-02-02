package Learning.common;

/**
 * Created by Administrator on 2017/1/13.
 */
public interface IFeatureAnalyzer {
    public abstract void OnItemExtracted(DataItem dataItem);
    public abstract FeatureSetView GetFeatureSetView();
}
