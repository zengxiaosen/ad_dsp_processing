package com.soledede.cf

import com.esotericsoftware.kryo.Kryo
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.serializer.{KryoRegistrator, KryoSerializer}
import scopt.OptionParser

/**
  * Created by Administrator on 2017/2/23.
  */
object FruitRecomendALS extends Serializable{
  class ALSRegistrator extends KryoRegistrator {
    override def registerClasses(kryo: Kryo) {
      kryo.register(classOf[Rating])
    }
  }

  case class Params(
                   input: String = null,
                   kryo: Boolean = false,
                   numIterations: Int = 20,
                   lambda: Double = 1.0,
                   rank: Int = 10,
                   implicitPrefs: Boolean = false,
                   userInput: String = null,
                   itemInput: String = null,
                   recommendNum: Int = 50,
                   separator: String = "\t",
                   userSeperator: String = "\t",
                   itemSeperator: String = "\t",
                   zookeeper_quorum: String = "spark2.soledede.com, spark3.soledede.com, spark1.soledede.com"
                   )

  def main(args: Array[String]) {
    val defaultParams = Params()
    val parser = new OptionParser[Params]("FruitRecomendALS") {
      head("FruitRecommendALS: ALS for fruit recommended.")
      opt[Int]("rank")
        .text(s"rank, defualt: ${defaultParams}")
        .action((x, c) => c.copy(numIterations =  x))
      opt[Int]("numIterations")
        .text(s"number of iterations, default: ${defaultParams.numIterations}")
        .action((x, c) => c.copy(numIterations = x))
      opt[Int]("recommendNum")
        .text(s"recommendNum, default: ${defaultParams.recommendNum}")
        .action((x, c) => c.copy(numIterations = x))
      opt[Double]("lambda")
        .text(s"lambda (smoothing constant), default: ${defaultParams.lambda}")
        .action((x, c) => c.copy(lambda = x))
      opt[Unit]("kryo")
        .text(s"use Kryo serialization")
        .action((_, c) => c.copy(kryo = true))
      opt[Unit]("implicitPrefs")
        .text("use implicit preference")
        .action((_, c) => c.copy(implicitPrefs = true))
      opt[String]("separator")
        .text(s"separator of ratings, default: ${defaultParams.separator}")
        .action((x, c) => c.copy(separator = x))
      opt[String]("userSeprator")
        .text(s"separator of users, default: ${defaultParams.userSeperator}")
        .action((x, c) => c.copy(userSeperator = x))
      opt[String]("itemSeprator")
        .text(s"separator of items, default: ${defaultParams.itemSeperator}")
        .action((x, c) => c.copy(itemSeperator = x))
      opt[String]("zookeeper_quorum")
        .text(s"zookeeper_quorum, default: ${defaultParams.zookeeper_quorum}")
        .action((x, c) => c.copy(zookeeper_quorum = x))
      arg[String]("<input>")
        .required()
        .text("input paths to a Fruit dataset of ratings")
        .action((x, c) => c.copy(input = x))
      arg[String]("userInput")
        .required()
        .text("userInput paths to a Fruit dataset of userids")
        .action((x, c) => c.copy(userInput = x))
      arg[String]("itemInput")
        .required()
        .text("itemInput paths to a Fruit dataset of itemids")
        .action((x, c) => c.copy(itemInput = x))
      note(
        """
          |For example, the following command runs this app on a synthetic dataset:
          |
          |/opt/cloudera/parcel//CDH/lib/spark/bin/spark-submit --class com.soledede.com.cf.FruitRecomendALS \
          |/home/hadoop/mllib/scala/spark_fruitrecomend-assembly-1.0.jar \
          |--rank 5 --numIteration 20 --lambda 1.0 --kryo --separator :: --userSeparator :: --itemSeparator :: \
          |/user/hadoop/mllib/movielen/ratings.dat /user/hadoop/mllib/movielen/users.dat /user/hadoop/mllib/movielen/movies.dat \
          |--recomendNum 10
        """.stripMargin
      )
    }

    parser.parse(args, defaultParams).map{
      params => run(params)
    } getOrElse{
      System.exit(1)
    }
  }

  def run(params: Params): Unit ={
    val conf = new SparkConf().setAppName(s"ALS with Fruit")
    if(params.kryo){
      conf.set("spark.serializer", classOf[KryoSerializer].getName)
        .set("spark.kyro.registrator", classOf[ALSRegistrator].getName)
        .set("spark.kryoserializer.buffer.mb", "8")
    }
    val sc = new SparkContext(conf)

    //load the config of Hbase, create Table recommend
    val confHbase = HBaseConfiguration.create()
    confHbase.set()
  }
}
