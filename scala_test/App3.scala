/**
  * Created by Administrator on 2016/12/30.
  */

trait SSS1{
  def fun():Unit
}
trait SSS2{
  def g():Unit
}
//SSS3看做是一个复合的数据类型
class SSS3 extends SSS1 with SSS2{
  override def fun() = println("Spark")
  override def g() = println("hadoop")
}
object App3 {
  def main(args: Array[String]): Unit = {
    //m类型是一个复合的函数类型，即具有SSS1，SSS2的功能，还必须拥有f()方法
    type m = SSS1 with SSS2{
      def f():Unit
    }
    def fun2(arg:m) = println("复合的数据类型！ ")
    fun2(new SSS1 with SSS2{
      //匿名对象符合了复合类型的要求
      override def fun() = println("1111")
      override def g() = println("2222")
      def f() = println("3333")
    })
  }
}
