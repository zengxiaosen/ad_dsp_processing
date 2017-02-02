package commons.framework.sample

import scala.collection.mutable.ArrayBuffer

/**
  * Created by Administrator on 2016/12/30.
  */
class Sample (val sampleId: Long, var targetValue: Double, var posId: String, var costType: Int, var timestamp: Long, var predictValue: Double = Double.MinValue){
  var lrScore: Double = 0
  var positionCTR: Double = 0
  var ideaId: String = _
  var cityId: String = _
  var adCategory: String = _
  var adSubIndustry: String = _
  var customerId: String = _

  var featureArrayList: ArrayBuffer[Feature] = new ArrayBuffer[Feature](1024)
  var doubleFeatureArray: ArrayBuffer[Double] = new ArrayBuffer[Double](128)

  def addFeatures(features: ArrayBuffer[Feature]){
    featureArrayList.++=(features)
  }

  def addDoubleFeature(value: Double, index: Int){
    while(doubleFeatureArray.length <= index){
      doubleFeatureArray.+=(0d)
    }
    doubleFeatureArray(index) = value
  }

  def pushDoubleFeature(value:Double): Unit ={
    doubleFeatureArray.+=(value)
  }

  def getFeaturesTupleSeq(): Seq[(Int, Double)] = {
    //featureIndex需要 -1，因为sparse的数组是从0开始的
    val featuresArray = featureArrayList.map(feature => (feature.featureIndex - 1, feature.featureValue))
    featuresArray.toSeq
  }

  def getDoubleFeatureTupleSeq(): Seq[(Int, Double)] = {
    val featuresArray = new Array[(Int,Double)](doubleFeatureArray.size)
    for(i <- 0 to featuresArray.length - 1){
      featuresArray(i) = (i, doubleFeatureArray(i))
    }
    featuresArray.toSeq
  }

  override def toString: String = {
    val sortedFeatures = featureArrayList.map(feature => (feature.featureIndex, feature)).sortBy(_._1).map(_._2)
    val result = targetValue + " " + sortedFeatures.mkString(" ")

    result
  }
}
