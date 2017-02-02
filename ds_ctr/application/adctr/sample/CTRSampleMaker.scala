package application.adctr.sample

import commons.framework.sample.SampleMaker

import scala.collection.mutable.ArrayBuffer

/**
  * Created by Administrator on 2016/12/13.
  */
class CTRSampleMaker private extends SampleMaker{
  var linearFeatureIndex: Map[String, Int] = Map()
  var isLinearEnable: Boolean = false

  def this(linearEnable: Boolean){
    this()
    isLinearEnable = linearEnable
    for(index <- 0 until CTRSampleMaker.linearFeatureArray.length){
      linearFeatureIndex.+=(CTRSampleMaker.linearFeatureArray(index) -> index)
    }
  }
}


object CTRSampleMaker{

  val linearFeatureArray: Array[String] = Array(
    "PositionChannelCTRHandler",
    "PositionCityCTRHandler",
    "PositionTimeCTRHandler",
    "PositionSubChannelCTRHandler",
    "PositionDeviceCTRHandler",
    "CityCTRHandler",
    "IdeaCTRHandler",
    "CastCTRHandler",
    "CustomerCTRHandler",
    "DeviceTypeHandler",
    "SexHandler",
    "AgeHandler",
    "LRScore"
  )

  val GBTFeatureArray: Array[String] = Array(
    //"PositionId",
    //"IdeaId",
    //"CityId",
    //"CustomerId",
    "MinutesOfDay"
  )

  def getGBTArray(): Array[String] = {
    val buffer = new ArrayBuffer[String]()
    buffer.++=(linearFeatureArray)
    buffer.++=(GBTFeatureArray)
    buffer.toArray

  }

  val SOURCE : Long = 10000000
  val cTRSampleMaker = new CTRSampleMaker(true)
  def instance = cTRSampleMaker

}