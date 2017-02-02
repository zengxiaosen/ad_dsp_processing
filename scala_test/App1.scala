/**
  * Created by Administrator on 2016/12/30.
  */

trait S1{
  def fun():Unit
}
class S2{
  self:S1 =>
}
//S2的子类必须混入S1这个特质
class S3 extends S2 with S1{
  override def fun() = println("Spark")
}
object App1 {
  def main(args: Array[String]): Unit = {
    //创建S2对象时必须混入S1这个特质
    val aa = new S2() with S1{
      override def fun() = println("Scala")
    }
    aa.fun()
  }
}
