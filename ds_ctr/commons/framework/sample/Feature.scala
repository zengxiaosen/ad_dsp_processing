package commons.framework.sample

/**
  * Created by Administrator on 2016/12/13.
  */
case class Feature (featureIndex: Int, featureName: String = "UndefinedName", featureValue: Double){
  override def toString: String = {
    featureIndex + " " + featureValue
  }

}
