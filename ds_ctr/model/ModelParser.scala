package model

import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.LogisticRegressionModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.GeneralizedLinearModel
import org.apache.spark.mllib.tree.configuration.{Algo, FeatureType}

import org.apache.spark.mllib.tree.model._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
/**
  * Created by Administrator on 2016/12/13.
  */
class ModelParser {

}

object ModelParser{
  private  val SUFFIX = "Handler"

  def writeGBTModelToStringArray(model: GradientBoostedTreesModel): ArrayBuffer[String] ={
    var buffer = new ArrayBuffer[String]();
    buffer.+=(s"TreeCount=${model.numTrees}")
    buffer.+=(s"NodeCount=${model.totalNumNodes}")
    buffer.+=(s"")
    for(i <- 0 until model.trees.length){
      buffer.+=(s"TreeWeight${i}=${model.treeWeights(i)}")
      buffer.+=(model.trees(i).toDebugString)
      buffer.+=(s"")
    }
    buffer
  }

  def writeGBTSingleNode(node: Node, buffer: ArrayBuffer[String], indentCount: Int): Unit = {
    val prefix: String = "\t" * indentCount
    if(node.isLeaf){
      buffer.+=(prefix + s"${node.id}:leaf=${node.predict.predict}")
    }else{
      buffer.+=(prefix + s"${node.id}:[f${node.split.get.feature} <= ${node.split.get.threshold}] yes=${node.leftNode.get.id}, no=${node.rightNode.get.id},gain=${node.stats.get.gain}")
      writeGBTSingleNode(node.leftNode.get, buffer, indentCount+1)
      writeGBTSingleNode(node.rightNode.get, buffer, indentCount+1)
    }
  }

  def writeGBTModelTrees(model: GradientBoostedTreesModel, features: Array[String]): ArrayBuffer[String] = {
    val buffer = new ArrayBuffer[String]()
    buffer.+=(s"TreeCount=${model.trees.length}")
    buffer.+=(s"FeatureCount=${features.length}")
    for(i <- 0 until model.trees.length){
      buffer.+=(s"")
      buffer.+=(s"booster[${i}]:weight=${model.treeWeights(i)}")

    }
    buffer.+=(s"")
    buffer
  }

  private def constructNode(
                           id: Int,
                           dataMap: Map[Int, NodeData],
                           nodes: mutable.Map[Int, Node]
                           ): Node = {
    if(nodes.contains(id)){
      return nodes(id)
    }
    val data = dataMap(id)
    val node =
      if (data.isLeaf){
        new Node(data.nodeId, data.predict.toPredict, data.impurity, data.isLeaf, null, null, null, null)
      }else{
        val leftNode = constructNode(data.leftNodeId.get, dataMap, nodes)
        val rightNode = constructNode(data.rightNodeId.get, dataMap, nodes)
        val stats = new InformationGainStats(data.infoGain.get, data.impurity, leftNode.impurity, rightNode.impurity, leftNode.predict, rightNode.predict)
        new Node(data.nodeId, data.predict.toPredict, data.impurity, data.isLeaf, data.split.map(_.toSplit), Some(leftNode), Some(rightNode), Some(stats))
      }
    nodes += node.id -> node
    node
  }

  def readGBTModelFromFile(GBTModelPath: String, sc: SparkContext) : GradientBoostedTreesModel = {
    val model = sc.textFile(GBTModelPath, 1).collect()
    readGBTModel(model)
  }

  def readGBTModel(model: Array[String]): GradientBoostedTreesModel = {
    val trees: ArrayBuffer[DecisionTreeModel] = new ArrayBuffer[DecisionTreeModel]()
    val treeWeights: ArrayBuffer[Double] = new ArrayBuffer[Double]()
    var dataMap: Map[Int, NodeData] = Map()
    var isTreeSection: Boolean = false
    var treeId: Int = 0

    for(line <- model){

      if(line.startsWith("booster")){
        val weightStart = line.indexOf("=");
        treeWeights.+=(line.substring(weightStart+1).toDouble)
        isTreeSection = true
      }else if(isTreeSection){
        if(line.trim.size == 0){
          //end of trees and construct nodes
          val topNodes = constructNode(1, dataMap, mutable.Map.empty)
          trees.+=(new DecisionTreeModel(topNodes,Algo.Regression))
          dataMap = Map()
          isTreeSection = false
          treeId += 1
        }else{
          //30:[f0 <= 53739.0] yes=60,no=61,gain=7.390214731607449E-7,
          val splitBySpace = line.trim().split(" ")
          val id = splitBySpace(0).split(":")(0).toInt//30

          //40:leaf=0.06390231028061505
          if(line.contains("leaf+")){
            val predict = splitBySpace(0).split("=")(1).toDouble
            dataMap.+=(id->(new NodeData(treeId,id,PredictData(predict, 1),0,true,null,null,null,null)))

          }
          else{
            val featureId = splitBySpace(0).split("f")(1).toInt //0
            val threshold = splitBySpace(2).split("]")(0).toDouble
            val splitByComma = splitBySpace(3).split(",")//yes=60,no=61,gain=7.390214731607449E-7,
            val yes = splitByComma(0).split("=")(1).toInt// yes=60
            val no = splitByComma(1).split("=")(1).toInt// no=61
            val gain = splitByComma(2).split("=")(1).toDouble//gain=7.390
            val emptyArray: Array[Double] = new Array[Double](0)
            dataMap.+=(id->(new NodeData(treeId,id,PredictData(0,1),0,false,
              Some(SplitData(featureId,threshold,FeatureType.Continuous.id,emptyArray.toSeq)),Some(yes),Some(no),Some(gain))))
          }
        }
      }
    }

    if(treeWeights.size > trees.size){
      // end of trees and construct nodes
      val topNodes = constructNode(1,dataMap,mutable.Map.empty)
      trees.+=(new DecisionTreeModel(topNodes,Algo.Regression))
    }

    println(s"Tree Count: ${trees.size}")
    new GradientBoostedTreesModel(Algo.Regression, trees.toArray, treeWeights.toArray)

  }

  def readLinearRegressionModelWeight(linearModelPath: String, sparkContext: SparkContext): org.apache.spark.mllib.linalg.Vector ={
    val model = sparkContext.textFile(linearModelPath, 1)
    val array = model.filter(u => u.startsWith(s"FeatureWeight")).map(u =>{
      val startPoint = u.indexOf("=")
      u.substring(startPoint+1).toDouble
    }).collect()

    for(weight : Double <- array){
      println(weight.toString)
    }
    println(s"weight size ${array.size} ${linearModelPath}")
    Vectors.dense(array)
  }

  def writeLinearRegressionModel(linearModel: GeneralizedLinearModel, features: Array[String]): ArrayBuffer[String] = {
    val buffer = new ArrayBuffer[String]()
    //Feature count
    buffer.+=(s"FeatureCount=${features.length}")

    for(index <- 0 until features.length){
      buffer.+=(s"")
      buffer.+=(s"FeatureName${index}=${removeSuffix(features(index))}")
      buffer.+=(s"FeatureWeight${index}=${linearModel.weights(index)}")
    }
    buffer
  }

  def printLinearRegressionModel(linearModel: GeneralizedLinearModel, features: Array[String]){
    //Feature count
    println(s"FeatureCount=${features.length}")

    for(index <- 0 until features.length){
      println(s"FeatureWeight${index}=${linearModel.weights(index)} FeatureName${index}=${removeSuffix(features(index))}")
    }
  }

  def removeSuffix(featureName: String): String = {
    var trimedName = featureName
    if(featureName.endsWith(SUFFIX)){
      trimedName = featureName.substring(0,featureName.length - SUFFIX.length)
    }
    trimedName
  }

  def savaModels(sc:SparkContext, model: LogisticRegressionModel, modelPath: String, suffix: String): Unit={
    if(model != null){
      val weightStr = "weight:" +  model.weights.toArray.mkString(",")
      val thresholdStr = "threshold:" + model.getThreshold
      val interceptStr = "intercept:" + model.intercept
      val saveModelPath = modelPath + "_" + suffix
      sc.parallelize(Array(weightStr,thresholdStr,interceptStr),1).saveAsTextFile(saveModelPath)
      println(s"*********************Save model path ${saveModelPath}")
    }
  }








  case class NodeData(
                     treeId: Int,
                     nodeId: Int,
                     predict: PredictData,
                     impurity: Double,
                     isLeaf: Boolean,
                     split: Option[SplitData],
                     leftNodeId: Option[Int],
                     rightNodeId: Option[Int],
                     infoGain: Option[Double]
                     )

  case class PredictData(predict: Double, prob: Double){
    def toPredict: Predict = new Predict(predict, prob)
  }
  case class SplitData(
                      feature: Int,
                      threshold: Double,
                      featureType: Int,
                      categories: Seq[Double]
                      ){
    def toSplit: Split = {
      new Split(feature, threshold, FeatureType(featureType), categories.toList)
    }
  }

}
