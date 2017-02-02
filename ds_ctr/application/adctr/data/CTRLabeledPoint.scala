package application.adctr.data

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vector
/**
  * Created by Administrator on 2016/12/30.
  */
case class CTRLabeledPoint() {

  type KEY_TYPE = (String,String,String,String,String,String)

  var ideaId: String = _
  var cityId: String = _
  var adCategory: String = _
  var adSubIndustry: String = _
  var customerId: String = _

  var positionCTR: Double = 0d
  var lrScore: Double = 0d
  var timestamp: Long = 0L
  var costType: Int = 0
  var posId: String = _
  var doubleArray: Vector = _
  var labeledPoint: LabeledPoint = _
  var label: Double = 0d
  var gbtDoubleArray: Vector = _

  def getKey(): KEY_TYPE = (ideaId,cityId, adCategory, adSubIndustry, customerId, posId)
  def getIdeaPosKey(): (String, String) = (ideaId, posId)

}
