package commons.framework.sample

import commons.framework.data.DataInstance

import scala.collection.mutable.ArrayBuffer

/**
  * Created by Administrator on 2016/12/30.
  */
abstract class SampleMaker extends Serializable{
  val defaultArrayListSize: Int = 256
  var featuresHandlerList: ArrayBuffer[FeatureHandler] = new ArrayBuffer[FeatureHandler](defaultArrayListSize)

  def registerFeatureHandler(featureHandler: FeatureHandler){
    featuresHandlerList.+=(featureHandler)
  }

  def fillSample(sample: Sample, dataInstance: DataInstance){
    for(handler <- featuresHandlerList){
      sample.addFeatures(handler.extractFeature(dataInstance))
    }
  }
}
