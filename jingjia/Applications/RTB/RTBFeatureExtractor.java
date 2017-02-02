package Applications.RTB;

import FeatureExtractor.Framework.Extractor;
import Learning.common.DataItem;

import java.io.IOException;

/**
 * Created by Administrator on 2017/1/13.
 */
public class RTBFeatureExtractor {

    private Extractor _extractor = new Extractor();
    public RTBFeatureExtractor(String dataDir) throws IOException{
        DomainFeatureHandler domainHandler = new DomainFeatureHandler(dataDir, 1);
        _extractor.AddFeatureHandler(domainHandler);
    }

    public DataItem ExtractFeature(RTBInstance rtbInst){
        return _extractor.ExtractFeatures(rtbInst);
    }

}
