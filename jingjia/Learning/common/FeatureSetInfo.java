package Learning.common;

/**
 * Created by Administrator on 2017/1/13.
 */
public final class FeatureSetInfo {
    private int _featureSetId;
    private String _description;
    public int GetFeatureSetId(){
        return _featureSetId;
    }
    /*public int GetOffset()
	{
		return _offset;
	}*/
	/*public int MaxFeatureCount()
	{
		return _maxFeatureCount;
	}*/
    public String GetDescription()
    {
        return _description;
    }

    public FeatureSetInfo(int featureSetId,
                          //int offset,
                          //int maxFeatureCount,
                          String description)
    {
        _featureSetId = featureSetId;
        //_offset = offset;
        //_maxFeatureCount = maxFeatureCount;
        _description = description;


    }
}
