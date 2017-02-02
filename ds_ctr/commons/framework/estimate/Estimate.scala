package commons.framework.estimate

/**
  * Created by Administrator on 2016/12/14.
  */
abstract class Estimate extends Serializable{
  val estimatorName: String
  val isPredict: Boolean//从已有的模型里进行predict
  def doEstimate()
  def analyzeResult(){}
  def doPredict(){}

}
