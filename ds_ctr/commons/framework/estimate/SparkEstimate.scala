package commons.framework.estimate

import java.io.IOException

import application.adctr.sample.CTRSampleConfig
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

import scala.io.{BufferedSource, Source}
import scala.util.Random

/**
  * Created by Administrator on 2016/12/30.
  */
abstract class SparkEstimate extends Estimate{
  type SOURCE
  var originRDD: RDD[SOURCE]
  var testRDD: RDD[SOURCE]
  type AUC_COUNT = (Long, Double) // count, AUC under ROC
  var posIdMap: Map[String, Int] = Map()

  val IS_TUNING: Boolean = false
  val IS_DEBUG: Boolean = false

  def readPositionMap(): Unit = {
    try{
      var source: BufferedSource = null
      source = Source.fromFile(CTRSampleConfig.getAdsSlotPath())
      val lineItr = source.getLines()
      for(line <- lineItr){
        val contents = line.split(",")
        if(contents.length >= 3){
          posIdMap.+=(contents(0).trim -> contents(2).trim.toInt)
        }
      }
    }catch {
      case ex: IOException => ex.printStackTrace()
    }
    finally{
      println(s"PositionId map size ${posIdMap.size}")
    }
  }

  def AUCPerPosition(scoreAndLabels: RDD[(String, Double, Double)]) : (AUC_COUNT, AUC_COUNT, AUC_COUNT, AUC_COUNT, AUC_COUNT, AUC_COUNT, AUC_COUNT,AUC_COUNT,AUC_COUNT) = {
    scoreAndLabels.persist(StorageLevel.MEMORY_AND_DISK_SER)
    val tiepianAUC = calculateAUC(scoreAndLabels.filter(u => isTiepian(u._1)))
    val frontAUC = calculateAUC(scoreAndLabels.filter(u => (adPosition(u._1) == 1)))
    val medianAUC = calculateAUC(scoreAndLabels.filter(u => (adPosition(u._1) == 2)))
    val backAUC = calculateAUC(scoreAndLabels.filter(u => (adPosition(u._1) == 3)))
    val stopAUC = calculateAUC(scoreAndLabels.filter(u => (adPosition(u._1) == 8)))
    val bannerAUC = calculateAUC(scoreAndLabels.filter(u => (adPosition(u._1) == 5)))
    val cornerAUC = calculateAUC(scoreAndLabels.filter(u => (adPosition(u._1) == 6)))
    val mobileAUC = calculateAUC(scoreAndLabels.filter(u=> (adPosition(u._1) == 7)))
    val otherAUC = calculateAUC(scoreAndLabels.filter(u => isCornerStopMobile(u._1)))
    scoreAndLabels.unpersist()
    (tiepianAUC, frontAUC, medianAUC, backAUC, stopAUC, bannerAUC, cornerAUC, mobileAUC, otherAUC)
  }

  def AUCPerPosition(scoreAndLabels : RDD[(String, Double, Double)], mType: Int) : (AUC_COUNT, AUC_COUNT, AUC_COUNT, AUC_COUNT) = {
    if (mType == 1) {
      scoreAndLabels.persist(StorageLevel.MEMORY_AND_DISK_SER)
      val tiepianAUC = calculateAUC(scoreAndLabels.filter(u => isTiepian(u._1)))
      val frontAUC = calculateAUC(scoreAndLabels.filter(u => (adPosition(u._1) == 1)))
      val medianAUC = calculateAUC(scoreAndLabels.filter(u => (adPosition(u._1) == 2)))
      val backAUC = calculateAUC(scoreAndLabels.filter(u => (adPosition(u._1) == 3)))
      scoreAndLabels.unpersist()

      (tiepianAUC,frontAUC,medianAUC,backAUC)
    }
    else if(mType == 2){
      scoreAndLabels.persist(StorageLevel.MEMORY_AND_DISK_SER)
      val bannerAUC = calculateAUC(scoreAndLabels.filter(u => isBanner(u._1)))
      scoreAndLabels.unpersist()

      (bannerAUC,bannerAUC,bannerAUC,bannerAUC)
    }
    else{
      scoreAndLabels.persist(StorageLevel.MEMORY_AND_DISK_SER)
      val allOtherAUC = calculateAUC(scoreAndLabels.filter(u => isCornerStopMobile(u._1)))
      val stopAUC = calculateAUC(scoreAndLabels.filter(u => (adPosition(u._1) == 8)))
      val cornerAUC = calculateAUC(scoreAndLabels.filter(u => (adPosition(u._1) == 6)))
      val mobileAUC = calculateAUC(scoreAndLabels.filter(u => (adPosition(u._1) == 7)))
      scoreAndLabels.unpersist()
      (allOtherAUC,stopAUC,cornerAUC,mobileAUC)
    }
  }

  def calculateAUC(labelPair: RDD[(String, Double, Double)]) : AUC_COUNT = {
    val metrics = new BinaryClassificationMetrics(labelPair.map(u => (u._2, u._3)))
    val count = labelPair.count()
    (count, metrics.areaUnderROC()) // metrics.areaUnderPR9())

  }



  def isBanner(posId: String) : Boolean = {
    (adPosition(posId) == 5)
  }

  def isTiepian(posId: String) : Boolean = {
    (adPosition(posId) >= 1 && adPosition(posId) <= 3)
  }

  def isCornerStopMobile(posId: String) : Boolean = {
    (adPosition(posId) >= 6 && adPosition(posId) <= 8)
  }

  def adPosition(posId : String) : Int = {
    var posIntId = 0
    if(posIdMap.contains(posId)){
      posIntId = posIdMap(posId)
    }

    posIntId match {
      case 2 => 1 // front
      case 40 => 2 // median
      case 5 => 3 // back
      case 11 => 5 // banner
      case 12 => 5 // banner
      case 10 => 6 // corner
      case 310 => 7 // mobile
      case 4 => 8 // stop
      case _ => 0

    }

  }

  def isBannerType(mType: Int): Boolean = {
    (mType == 2)
  }

  def isTiepian(mType: Int): Boolean = {
    (mType == 1)
  }

  def randomSample(percent: Double): Boolean = {
    var isSampled = false
    if(Random.nextDouble() < percent){
      isSampled = true
    }
    isSampled
  }

}
