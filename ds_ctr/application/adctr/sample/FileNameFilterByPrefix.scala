package application.adctr.sample

import java.io.{File, FilenameFilter}

/**
  * Created by Administrator on 2016/12/30.
  */
class FileNameFilterByPrefix extends FilenameFilter {

  var prefix: String = ""
  def this(pathPrefix: String){
    this()
    prefix = pathPrefix
  }

  override def accept(dir: File, name: String): Boolean = {
    name.startsWith(prefix)
  }
}
