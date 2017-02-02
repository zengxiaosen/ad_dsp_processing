package application.adctr.estimator

import application.adctr.data.CTRLabeledPoint
import application.adctr.sample.CTRSampleMaker
import commons.framework.estimate.SparkEstimate
import model.ModelParser
import org.apache.spark.ml.classification.LogisticRegressionModel
import org.apache.spark.mllib.optimization.LeastSquaresGradient
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.GradientBoostedTrees
import org.apache.spark.mllib.tree.configuration.BoostingStrategy
import org.apache.spark.mllib.tree.model.GradientBoostedTreesModel
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

/**
  * Created by Administrator on 2017/1/2.
  */
class GBTEstimator (val estimatorName: String = "GBTEstimator", val isPredict: Boolean = false)  extends SparkEstimate{
  override type SOURCE = CTRLabeledPoint
  override var originRDD: RDD[SOURCE] = _
  override var testRDD: RDD[SOURCE] = _
  var modelA: GradientBoostedTreesModel = _
  var modelB: LogisticRegressionModel = _
  var modelType: Int = 1
  var testResultPath: String = _
  var iterationCount: Int = 60
  var bestTestResultRDD: RDD[(Double, Double)] = _

  def this(mType: Int, predict: Boolean){
    this(isPredict = predict)
    modelType = mType
    readPositionMap()
  }

  def sampleType(posId: String): Boolean = {
    if(isTiepian(posId)){
      randomSample(0.2)
    }
    else if(isBanner(posId)){
      randomSample(0.1)
    }
    else{
      true
    }
  }
  //从已有的模型里进行predict
  override def doEstimate(): Unit = {
    if(originRDD != null){
      val trainingData = modelType match {
        case 1 => originRDD.filter(u => isTiepian(u.posId)).filter(u => randomSample(0.3))
        case 2 => originRDD.filter(u => isBanner(u.posId)).filter(u => randomSample(0.1))
        case _ => originRDD.filter(u => isCornerStopMobile(u.posId))
      }

      trainingData.coalesce(936, true)

      val featureData = trainingData.map(u => {
        LabeledPoint(u.label, u.gbtDoubleArray)
      }).persist(StorageLevel.MEMORY_AND_DISK_SER)

      var firstLineFeature: Array[LabeledPoint] = null
      if (IS_DEBUG) {
        println("*********First line feature data:*********")
        firstLineFeature  = featureData.take(15)
        println(firstLineFeature(0).features.toString)
      }

      val boostingStrategy = BoostingStrategy.defaultParams("Regression")
      boostingStrategy.setNumIterations(iterationCount)
      boostingStrategy.treeStrategy.setSubsamplingRate(0.1)
      if(modelType == 2 || modelType == 3){
        boostingStrategy.treeStrategy.setMaxDepth(7) // banner has deeper tree depth 7
      }else{
        boostingStrategy.treeStrategy.setMaxDepth(5) // others have tree depth 5
      }
      boostingStrategy.setLearningRate(0.01)

      modelA = GradientBoostedTrees.train(featureData, boostingStrategy)
      featureData.unpersist()

      if(IS_DEBUG){
        println("*********Decision Tree************")
        val modelString = ModelParser.writeGBTModelTrees(modelA, CTRSampleMaker.getGBTArray())
        println(modelA.toDebugString)
        println("********Parsed Tree**********")
        val parsedModelA = ModelParser.readGBTModel(modelString.toArray)
        println(parsedModelA.toDebugString)

        firstLineFeature.map(u => {
          val scoreA = modelA.predict(u.features)
          val scoreParsedA = parsedModelA.predict(u.features)
          println(s"**** Evaluate score ${scoreA}, features ${u.features}*****")
        })

        doPredict()

      }

    }
  }

  def doDebug(ideaId: String, savePath: String): Unit = {
    if(testRDD != null && modelA != null){
      testRDD.coalesce(936, false).persist(StorageLevel.MEMORY_AND_DISK_SER)
      val scoreAndLabelsA = testRDD.filter(u => u.ideaId.equals(ideaId)).map(point => {
        val score = modelA.predict(point.gbtDoubleArray)
        (point.posId, point.ideaId, score, point.labeledPoint.label, point.lrScore)
      }).persist()

      val avgScore = scoreAndLabelsA.map(u => u._3.toDouble).mean()
      val avgCTR = scoreAndLabelsA.map(u => u._4.toDouble).mean()
      val avgLRScore = scoreAndLabelsA.map(u => u._5.toDouble).mean()
      println(s"*********ideaId ${ideaId} avgScore ${avgScore} avgLRScore ${avgLRScore} avgCTR ${avgCTR} ******")

      val sampledData = scoreAndLabelsA.take(15)
      for(line <- sampledData){
        println(s"${line}")
      }

      testRDD.unpersist()
      scoreAndLabelsA.saveAsTextFile(savePath)
      scoreAndLabelsA.unpersist()
    }
  }

  override def doPredict(): Unit = {
    if(testRDD != null && modelA != null && modelB != null){
      testRDD = modelType match {
        case 1 => testRDD.filter(u => isTiepian(u.posId))
        case 2 => testRDD.filter(u=>isBanner(u.posId))
        case _ =>testRDD.filter(u=>isCornerStopMobile(u.posId))
      }

      testRDD.coalesce(936, false).persist(StorageLevel.MEMORY_AND_DISK_SER)

      val gradient = new LeastSquaresGradient()
      val scoreAndLabelsOld = testRDD.map(point => {
        (point.posId, point.lrScore, point.labeledPoint.label)
      })

      val auc01d = AUCPerPosition(scoreAndLabelsOld, modelType)
      val scoreAndLabelsA = testRDD.map(point => {
        val score = modelA.predict(point.gbtDoubleArray)
        (point.posId, score, point.labeledPoint.label)
      })

      val aucA = AUCPerPosition(scoreAndLabelsA, modelType)

      val ScoreAndLabelsB = testRDD.map(point => {
        val score = null
        //val score = modelB.predict(point.labeledPoint.features)
        (point.posId, score, point.labeledPoint.label)
      })
      //val aucB = AUCPerPosition(ScoreAndLabelsB,modelType)
    }
  }

}
