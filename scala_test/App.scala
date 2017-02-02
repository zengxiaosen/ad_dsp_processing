/**
  * Created by Administrator on 2016/12/30.
  */
object App {
  def main(args: Array[String]): Unit = {
    val outer1: Outer = new Outer()
    val outer2: Outer = new Outer()
    //inner1与inner2这两个对象路径依赖不同
    val inner1: outer1.Inner = new outer1.Inner()
    val inner2: outer2.Inner = new outer2.Inner()
    inner1.fun()
    inner2.fun()
  }
}

class Outer{
  outer =>
    val str = "Spark"
    class Inner{
      def fun() = println(outer.str)
    }
}
