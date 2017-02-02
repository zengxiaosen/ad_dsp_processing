package application.adctr.data

import org.apache.spark.mllib.regression.LabeledPoint

/**
  * Created by Administrator on 2016/12/30.
  */
case class MyLabeledPoint(val posId: String, var labeledPoint: LabeledPoint, val costType: Int, val timestamp: Long, val ideaId: String) {
  type KEY_TYPE = (String, String)

  def getIdeaPosKey(): KEY_TYPE = (ideaId, posId)

}
