/**
  * Created by Administrator on 2016/12/30.
  */
object PrimaryConstructorDemo {
    //Personl is to show: primary constructor consists of not only class args list but also all runnable part in class body
  class Personl(var firstName: String, var lastName: String){
      /*
      首先，我们必须要非常清晰明确的认识到：主构造函数不是你看到的class后面跟的参数列表，
      那怎么可能是主构造函数呢？那只是主构造函数的函数列表！那主构造函数的函数体在那里呢？答案是：
      class body里所有除去字段和方法声明的语句，剩下的一切都是主构造函数的，它们在class实例化时一定会被执行。
      本文原文出处: http://blog.csdn.net/bluishglc/article/details/50899077 严禁任何形式的转载，否则将委托CSDN官方维护权益！

      所以说，Scala的主构造函数包含这些部分：

      The constructor parameters
      Methods that are called in the body of the class
      Statements and expressions that are executed in the body of the class
      请看实例代码中的class Person1以及它的输出。从这个例子上我们可以看出：
      主构造函数看上去和类的定义已经完全融合在了一起！它的参数列表放到了类名的后面（我们也可以直接叫它类参数列表），
      它的方法体就是整个类体，实例化一个类时，类体（主构造函数）中所有可行的部分都会被执行，不管是函数调用还是表达式等等，
      只是对于类的字段和方法声明而言是没有什么可执行的，它们只是声明而已。
       */
      println("the constructor begins")
      //some class fields
      private val HOME = "/root"
      var age = 30

      // some methods
      override def toString = s"$firstName $lastName is $age years old"
      def printHome {
        println(s"HOME= $HOME")
      }
      def printFullName{
        println(this) // uses toString
      }
      printHome
      printFullName
      println("still in the constructor")
    }

  // Person2 is to show: the visibility of class fields
  class Person2{
    var age = 30
    val gender = "male"
    private val healthy = false
  }

  // Person3 is to show: the visibility of primary ccnstructor args
  class Person3(var age: Int, val gender: String, private val healthy: Boolean)

  //Person4 is to show: change visibility for primary constructor args
  class Person4(private var _age:Int){
    def age = _age // this is getter
    def age_=(newAge: Int) = _age = newAge //this is setter
  }

  def main(args: Array[String]) {
    val p1 = new Personl("Tome","White")
    println("---------------------------------")
    var p2 = new Person2
    println(p2.age)
    println(p2.gender)
    println("---------------------------------")
    val p3 = new Person3(30, "male", false)
    println(p3.age)
    p3.age = 40;
    println(p3.age)
    println(p3.gender)
    println("---------------------------------")
    val p4 = new Person4(30)
    println(p4.age)
    p4.age = 40
    println(p4.age)

  }
}
