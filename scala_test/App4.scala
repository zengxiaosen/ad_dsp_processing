import scala.io.{BufferedSource, Source}

/**
  * Created by Administrator on 2016/12/30.
  */
trait A1{
  type In
  type Out
  def read(args: In):Out
}
class A2 extends A1{
  type In = String
  type Out = BufferedSource
  override def read(a: String): BufferedSource = Source.fromFile(a)
}
object App4 {
  def main(args: Array[String]): Unit = {
    var a2= new A2()
    var fr: BufferedSource = a2.read("C;\\word.txt")
    for(line <- fr.getLines()){
      println(line)
    }
  }
}
