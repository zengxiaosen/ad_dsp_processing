package commons.framework.transform

/**
  * Created by Administrator on 2016/12/30.
  */
abstract class Transform extends Serializable{

  type IN
  type OUT

  val transformName: String
  def createTransformFunc(params: Any): IN => OUT

}
