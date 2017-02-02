/**
  * Created by Administrator on 2016/12/30.
  */

class SS1{
  def fun():this.type = this
}

class SS2 extends SS1{
  def g():this.type  = this
}

object App2{
  def main(args: Array[String]): Unit = {
    var s2 = new SS2()
    //scala之所以可以做到链式风格的编程，是因为使用了type机制
    s2.g().fun().g().fun()
  }
}
