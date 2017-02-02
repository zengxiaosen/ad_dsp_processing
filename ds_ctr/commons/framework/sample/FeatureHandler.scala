package commons.framework.sample

import java.io.{File, IOException}
import java.text.SimpleDateFormat
import java.util.Calendar

import application.adctr.sample.{CTRSampleConfig, FileNameFilterByPrefix}
import commons.framework.data.DataInstance

import scala.collection.immutable.{HashMap, Map}
import scala.collection.mutable.ArrayBuffer
import scala.io.{BufferedSource, Source}

/**
  * Created by Administrator on 2016/12/13.
  */
trait FeatureHandler extends Serializable{
  val featureHandlerName: String
  val defaultDoubleValue: Double = -1d
  var isFeatureAdded: Boolean = true
  var dailyCTRMap: Map[String,Double] = new HashMap[String,Double]
  def initFeatureHandler(initFeatureIndex: Int): Int
  def extractFeature(dataInstance: DataInstance): ArrayBuffer[Feature]
  def extractValue(dataInstance: DataInstance): Double = {
    0
  }

  // function to write daily map
  def parseDailyMap(namePrefix: String, numOfFields : Int): Unit = {
    var source: BufferedSource = null
    val updateList = (new File(CTRSampleConfig.getUpdateConfPath())).list(new FileNameFilterByPrefix(namePrefix))
    var fileCount = 0
    val calendar = Calendar.getInstance()
    val sdf= new SimpleDateFormat("yyyyMMdd")

    for(matchFile  <- updateList){
      try {
        source = Source.fromFile(CTRSampleConfig.getUpdateConfPath() + matchFile)
        val date = matchFile.substring(matchFile.indexOf('_') + 1)
        calendar.setTime(sdf.parse(date))
        // Actually yesterday data
        calendar.set(Calendar.DATE,calendar.get(Calendar.DATE) -1 )
        val dateString = sdf.format(calendar.getTime())

        for (line <- source.getLines()) {
          val contents = line.split("\\t")
          if (contents.length == numOfFields) {
            val key = dateString + "_" + contents(0).trim
            if (!dailyCTRMap.contains(key)) {
              dailyCTRMap.+=(key -> contents(1).trim.toDouble)
            }
          }
        }
        fileCount = fileCount + 1
      }
      catch {
        case ex: IOException => ex.printStackTrace()
      }
      finally{
        if (source != null)
        {
          source.close()
          source = null
        }
      }
    }
    System.out.println(s"${namePrefix} files count ${fileCount}")
  }
}
