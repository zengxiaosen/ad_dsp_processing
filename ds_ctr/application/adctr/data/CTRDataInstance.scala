package application.adctr.data

import commons.framework.data.DataInstance

/**
  * Created by Administrator on 2016/12/30.
  */
class CTRDataInstance extends DataInstance{
  override var correctlyFilled: Boolean = false

  var channel: String = null //video channel
  var subChannel: String = null //video sub channel
  var date: String = null // request date
  var timeStamp: Long = -1 // request time
  var site: String = null // yuku or tudou
  var clientType: String = null // web or app
  var os: String = null
  var brower: String = null
  var avs: String = null // app version
  var deviceType: String = null
  var adSize: String = null // length * width
  var ideaType: String = null // 1 video 2 banner
  var adIndustry: String = null // ad(idea) industry
  var adSubIndustry: String = null // ad(idea) sub industry
  var adCategory: String = null // ad(idea) 品类

  var ip: String = null
  var provinceId: String = null
  var cityId: String = null
  var programId: String = null // 节目id
  var videoId: String = null // 视频id
  var albumId: String = null // 专辑id
  var positionId: String = null // 广告位id
  var castId: String = null // 投放id
  var customerId: String = null // 广告主id
  var ideaId: String = null //素材Id

  //user info
  var sex: String = null
  var age: String = null
  var channelsPref: String = null //频道偏好，看是否与上面的频道匹配上？no
  var adsPref: String = null //广告偏好，看是否与上面的广告行业匹配上？no

  //brand ad effect optimize 有关品牌广告的其他上面没有的字段
  var isLongVideo: String = null // 0:短视频 1：长视频
  var effect: String = null // 交互效果：1.常规，2.全屏，3.互动，4.crazy，5.adselector，6.TrueView
  var packageType: String = null // 0:非包断，1：tvb包断
  var orderType: String = null //销售0，市场1，测试2
  var dp: String = null // 清晰度
  var videoLength: Float = -1 // 视频时长，单位s
  var ideaLength : Float = -1 // 素材时长，单位s 暂停0s
  var orderId: String = null
  var copyright: String = null
  var productType: String = null //产品类型

  var targetValue: Double = Double.MinValue
  var costType: Int = -1 // cost type, 0位cpm类型，1位cpc类型，2为cpa，3位cpv
}
