package sss.ancillary

import scala.reflect.runtime.universe._
import java.lang.reflect.{ Method, InvocationHandler, Proxy }
/**
 * Factory to allow an instance to be created via reflection from the constructor
 * Pass in the correct params for the constructor.
 *
 * WARN - Not good for multiple constructors, this will only
 * attempt to match the first constructor of a class.
 *
 */
object ReflectionUtils extends Logging {

  /**
   * WARN - Not good for multiple constructors, this will only
   * attempt to match the first constructor of a class.
   */
  def createInstance[T](className: String, params: Any*): T = {
    val c = Class.forName(className)
    val constructors = c.getConstructors
    if (constructors.size != 1) log.warn("There are multiple constructors reported here, this method operates under the assumption there's only one!")
    if (params.size > 0) constructors(0).newInstance((params.map(_.asInstanceOf[Object]): _*)).asInstanceOf[T]
    else constructors(0).newInstance().asInstanceOf[T]

  }

  def getInstance[T](objectName: String): T = {

    val runtimeMirrorer = runtimeMirror(getClass.getClassLoader)

    val module = runtimeMirrorer.staticModule(objectName)

    runtimeMirrorer.reflectModule(module).instance.asInstanceOf[T]

  }

  /**
   * Create an interface instance powered by the passed invoker.
   *
   * @param invoker
   * @tparam I
   * @return
   */
  def createProxy[I: TypeTag](invoker: (Object, Method, Array[Object]) => Object): I = {

    val m = runtimeMirror(getClass.getClassLoader)
    val clazz = m.runtimeClass(typeOf[I])
    Proxy.newProxyInstance(clazz.getClassLoader, Array(clazz), new InvocationHandler() {
      override def invoke(proxy: Object, method: Method, args: Array[Object]) = {
        invoker(proxy, method, args)
      }
    }).asInstanceOf[I]
  }

}