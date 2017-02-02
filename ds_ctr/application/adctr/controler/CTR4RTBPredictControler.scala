package application.adctr.controler

import application.adctr.sample.{CTRSampleConfig, CTRSampleMaker}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Administrator on 2016/12/13.
  */
class CTR4RTBPredictControler {

}

object CTR4RTBPredictControler{
  def main(args: Array[String]){
    if(args.length < 9){
      System.out.println("program need 9 args,$programName,$inputPath,formatDataOutPath,$modelPath,$testResultPath,$runType")
      System.out.println("set param equal 'none' if no need it")
      System.exit(-1)
    }

    val programName = args(0)
    val inputPath = args(1)
    val testDataPath = args(2)
    val confPath = if (args(3).equals("none")) "" else args(3)
    val modelPath = if (args(4).equals("none")) "" else args(4)
    val controlModelPath = args(5)
    val testResultPath = if (args(6).equals("none")) "" else args(6)
    val runType = args(7)
    val modelDate = args(8)
    val modelType = args(9).toInt
    val iterationCount = args(10).toInt
    println(s"programName:$programName")
    println(s"inputPath:$inputPath")
    println(s"testDataPath:$testDataPath")
    println(s"confPath:$confPath")
    println(s"modelPath:$modelPath")
    println(s"controlModelPath:$controlModelPath")
    println(s"testResultPath:$testResultPath")
    println(s"runType:$runType")
    println(s"date:$modelDate")
    println(s"modelType:${modelType}")
    println(s"iterationCount:${iterationCount}")

    val conf = new SparkConf().setAppName(programName)
    val sc = new SparkContext(conf)

    System.setProperty("spark.serializer","org.apache.spark.serializer.KryoSerializer")
    System.setProperty("spark.kryo.referenceTracking","false")
    System.setProperty("spark.kryo.registrator","application.adctr.controler,CTR4RTBPredictControler.Register")
    System.setProperty("spark.kryoserializer.buffer.mb","16")
    System.setProperty("spark.storage.memoryFraction","0.7")

    runType match{
      case "etl-train-sgd" => runETLAndTrainByLogisticSGD(sc, inputPath, testDataPath, confPath, modelPath, controlModelPath, testResultPath, modelDate, modelType, iterationCount)

    }

    def runETLAndTrainByLogisticSGD(sc: SparkContext, inputPath: String, testDataPath: String, confPath: String, modelPath: String,
                                    controlModelPath: String, testResultPath: String, modelDate: String, modelType: Int, iterationCount:Int){
      val training = sc.textFile(inputPath)
      var testData : RDD[String] = null
      if(testDataPath != "")
        testData = sc.textFile(testDataPath)

      //feature handler register
      //val featureIndex = init(confPath)

    }

    /*def init(confPath: String): Int = {
      CTRSampleConfig.setParentPath(confPath)
      CTRSampleConfig.loadConf()

      var featureIndex = 0
      val sampleMaker = CTRSampleMaker.instance

    }*/
  }
}
