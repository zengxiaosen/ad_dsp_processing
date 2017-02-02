package commons.framework.transform

import org.apache.spark.rdd.RDD

/**
  * Created by Administrator on 2016/12/30.
  */
abstract class SparkTransform extends Transform{
  protected var transformParams: Any
  var inputRDD: RDD[IN]
  var outputRDD: RDD[OUT]
  implicit def tag: reflect.ClassTag[OUT]

  def rddTransform(){
    val transform = createTransformFunc(transformParams)
    outputRDD = inputRDD.map(transform).filter(dataFilter)
    if(isNeedCache()){
      outputRDD.cache()
    }
  }

  def dataFilter(input: OUT): Boolean = true

  def isNeedCache(): Boolean = false

  def unCache(): Unit ={
    if(isNeedCache()){
      outputRDD.unpersist()
    }
  }
}
