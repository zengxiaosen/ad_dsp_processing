package Applications.RTB;

import FeatureExtractor.Framework.DataInstance;
import FeatureExtractor.Framework.IFeatureHandler;
import FileUtil.HashSerializer;
import Learning.common.Feature;
import Learning.common.FeatureSetInfo;

import java.io.*;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Created by Administrator on 2017/1/13.
 */
public class DomainFeatureHandler implements IFeatureHandler {

    private static FeatureSetInfo _featureSet;
    private Hashtable<String, Integer> _featureMapping;
    private static String _dataFileName = "domain_mapping.txt";

    public DomainFeatureHandler(String dataDir, int featureSetId) throws IOException {
        _featureSet = new FeatureSetInfo(featureSetId, "DomainFeature");
        String dataPath = dataDir + _dataFileName;
        File dataFile = new File(dataPath);
        BufferedReader reader = new BufferedReader(new FileReader(dataFile));
        _featureMapping = HashSerializer.LoadStringHashtable(reader, "\t", 0, 1);
    }

    public Vector<Feature> ExtractFeature(DataInstance dataInst) {
        RTBInstance rtbInst = (RTBInstance)dataInst;
        Vector<Feature> features = new Vector<Feature>();
        String domainHash = rtbInst.GetDomainHash();
        if(_featureMapping.containsKey(domainHash));
        {
            int localId = _featureMapping.get(domainHash);
            features.add(new Feature(_featureSet,localId,1));
        }
        return features;
    }

    public FeatureSetInfo GetSupportedFeatureSet() {
        return _featureSet;
    }
}
