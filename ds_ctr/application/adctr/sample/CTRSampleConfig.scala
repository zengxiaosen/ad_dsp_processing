package application.adctr.sample

import java.io.{FileInputStream, IOException}
import java.util.Properties

/**
  * Created by Administrator on 2016/12/13.
  */
class CTRSampleConfig {

}

object CTRSampleConfig{
  var parentPath = "rtb_conf/"

  //update ctr conf directory
  private val updateConfPath = "updateConf/"
  private val adsSlotPath = "dim_ads_slot_exchange.csv"
  private val confFile = "LR4DspCTR.properties"

  def getUpdateConfPath(): String = {
    parentPath + updateConfPath
  }

  def getAdsSlotPath(): String = {
    parentPath + adsSlotPath
  }
  def getConfFile(): String = {
    parentPath + confFile
  }

  def setParentPath(path: String): Unit = {
    parentPath = path
  }

  def loadConf(){
    try{
      println(s"ParentPath ${parentPath} updateConf ${getUpdateConfPath()}")

      val props = new Properties()
      val inputStream = new FileInputStream(getConfFile)
      props.load(inputStream)
      setParameters(props)
      inputStream.close()

    }catch{
      case ex: IOException => ex.printStackTrace()
    }
  }

  /**
    * label,site,time,provinceId,cityId,castId,cat,subcat,vid,effect, (10)
    * ideaLength,isLong(1,0),videoLength,adPosition,copyright,orderId,orderType, (7)
    * packageType,productType,avs,clearDegree,deviceType,osType,clientType,(7)
    * ideaId, ideaCat, ideaSubIndustry, ideaIndustry,cookie,adsPref,channelPref,sex,age (8)
    */

  var timestampHandlerSwitch = false
  var timeChannelHandlerSwitch = false
  var timeClientTypeHandlerSwitch = false
  var timeDeviceTypeHandlerSwitch = false
  var timeOSTypeHandlerSwitch = false
  var adxPIdTimeStampCTRHandlerSwitch = false
  var cityCTRHandlerSwitch = false
  var adxPIdCityCTRHandlerSwitch = false
  var retargetingHandlerSwitch = false
  var channelHandlerSwitch = false
  var adxPIdChannelCTRHandlerSwitch = false
  var adxPIdSubChannelCTRHandlerSwitch = false
  var clientTypeHandlerSwitch = false
  var osTypeHandlerSwitch = false
  var deviceTypeHandlerSwitch = false
  var adxPIdDeviceTypeCTRHandlerSwitch = false
  var browserHandlerSwitch = false
  var avsHandlerSwitch = false
  var siteHandlerSwitch = false
  var castCTRHandlerSwitch = false
  var advertiserCTRHandlerSwitch = false
  var ideaCTRHandlerSwitch = false
  var ideaIndustryAndSubHandlerSwitch = false
  var ideaLengthHandlerSwitch = false
  var ideaSizeHandlerSwitch = false
  var ideaTypeHandlerSwitch = false
  var prefAdIndustryMatchedHandlerSwitch = false
  var userAdPrefHandlerSwitch = false
  var prefChannelMatchedHandlerSwitch = false
  var userChannelPrefHandlerSwitch = false
  var sexHandlerSwitch = false
  var sexIdeaIndustryAndSubHandlerSwitch = false
  var ageHandlerSwitch = false
  var ageIdeaIndustryAndSubHandlerSwitch = false

  var ctrDiscreteDis = 0.001
  var ctrDiscreteLen = 10 //线性模型时，品牌广告里的ctr一般值为11，RTB广告另取
  var continuousDiscrete = 1//除ctr外的连续型值是否离散化，暂时不用
  def setParameters(props: Properties){
    timestampHandlerSwitch = props.getProperty("timestampHandlerSwitch","0").toInt > 0
    timeChannelHandlerSwitch = props.getProperty("timeChannelHandlerSwitch","0").toInt > 0
    timeClientTypeHandlerSwitch = props.getProperty("timeClientTypeHandlerSwitch","0").toInt > 0
    timeDeviceTypeHandlerSwitch = props.getProperty("timeDeviceTypeHandlerSwitch","0").toInt > 0
    timeOSTypeHandlerSwitch = props.getProperty("timeOSTypeHandlerSwitch","0").toInt > 0
    adxPIdTimeStampCTRHandlerSwitch = props.getProperty("adxPIdTimeStampCTRHandlerSwitch","0").toInt > 0
    cityCTRHandlerSwitch = props.getProperty("cityCTRHandlerSwitch","0").toInt > 0
    adxPIdCityCTRHandlerSwitch = props.getProperty("adxPIdCityCTRHandlerSwitch","0").toInt > 0
    retargetingHandlerSwitch = props.getProperty("retargetingHandlerSwitch","0").toInt > 0
    channelHandlerSwitch = props.getProperty("channelHandlerSwitch","0").toInt > 0
    adxPIdChannelCTRHandlerSwitch = props.getProperty("adxPIdChannelCTRHandlerSwitch","0").toInt > 0
    adxPIdSubChannelCTRHandlerSwitch = props.getProperty("adxPIdSubChannelCTRHandlerSwitch","0").toInt > 0
    clientTypeHandlerSwitch = props.getProperty("clientTypeHandlerSwitch","0").toInt > 0
    osTypeHandlerSwitch = props.getProperty("osTypeHandlerSwitch","0").toInt > 0
    deviceTypeHandlerSwitch = props.getProperty("deviceTypeHandlerSwitch","0").toInt > 0
    adxPIdDeviceTypeCTRHandlerSwitch = props.getProperty("adxPIdDeviceTypeCTRHandlerSwitch","0").toInt > 0
    browserHandlerSwitch = props.getProperty("browserHandlerSwitch","0").toInt > 0
    avsHandlerSwitch = props.getProperty("avsHandlerSwitch","0").toInt > 0
    siteHandlerSwitch = props.getProperty("siteHandlerSwitch","0").toInt > 0
    castCTRHandlerSwitch = props.getProperty("castCTRHandlerSwitch","0").toInt > 0
    advertiserCTRHandlerSwitch = props.getProperty("advertiserCTRHandlerSwitch","0").toInt > 0
    ideaCTRHandlerSwitch = props.getProperty("ideaCTRHandlerSwitch","0").toInt > 0
    ideaIndustryAndSubHandlerSwitch = props.getProperty("ideaIndustryAndSubHandlerSwitch","0").toInt > 0
    ideaLengthHandlerSwitch = props.getProperty("ideaLengthHandlerSwitch","0").toInt > 0
    ideaSizeHandlerSwitch = props.getProperty("ideaSizeHandlerSwitch","0").toInt > 0
    ideaTypeHandlerSwitch = props.getProperty("ideaTypeHandlerSwitch","0").toInt > 0
    prefAdIndustryMatchedHandlerSwitch = props.getProperty("prefAdIndustryMatchedHandlerSwitch","0").toInt > 0
    userAdPrefHandlerSwitch = props.getProperty("userAdPrefHandlerSwitch","0").toInt > 0
    prefChannelMatchedHandlerSwitch = props.getProperty("prefChannelMatchedHandlerSwitch","0").toInt > 0
    userChannelPrefHandlerSwitch = props.getProperty("userChannelPrefHandlerSwitch","0").toInt > 0
    sexHandlerSwitch = props.getProperty("sexHandlerSwitch","0").toInt > 0
    sexIdeaIndustryAndSubHandlerSwitch = props.getProperty("sexIdeaIndustryAndSubHandlerSwitch","0").toInt > 0
    ageHandlerSwitch = props.getProperty("ageHandlerSwitch","0").toInt > 0
    ageIdeaIndustryAndSubHandlerSwitch = props.getProperty("ageIdeaIndustryAndSubHandlerSwitch","0").toInt > 0

    ctrDiscreteDis = props.getProperty("ctrDiscreteDis","0.001").toFloat
    ctrDiscreteLen = props.getProperty("ctrDiscreteLen","10").toInt
    continuousDiscrete = props.getProperty("ctrDiscreteLen","1").toInt
  }



}
