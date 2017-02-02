import scala.io.{BufferedSource, Source}

/**
  * Created by Administrator on 2016/12/30.
  */
trait b1[F,S]{
  def read(arg: F): S
}
class b2 extends b1[String, BufferedSource]{
  override def read(args: String): BufferedSource = Source.fromFile(args)
}
object App5 {
  def main(args: Array[String]): Unit = {
    val b = new b2()
    var fr = b.read("C:\\word.txt")
    for(line <- fr.getLines()){
      println(line)
    }
  }

}
