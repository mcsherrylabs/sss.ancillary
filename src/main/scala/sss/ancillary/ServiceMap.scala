package sss.ancillary

import scala.collection.concurrent.TrieMap
import scala.reflect.ClassTag

trait ServiceMap {

  protected val serviceMap: TrieMap[String, Any] = TrieMap.empty

  protected def classTypeToString[T: ClassTag]: String = {
    implicitly[ClassTag[T]].runtimeClass.getName
  }

  def addService[T: ClassTag](service: T): Unit = {
    addService(classTypeToString[T], service)
  }

  def removeService(serviceName: String): Option[Any] = {
    serviceMap.remove(serviceName)
  }

  def removeService[T: ClassTag](): Option[T] = {
    serviceMap.remove(classTypeToString[T]).map(_.asInstanceOf[T])
  }

  def addService(key: String, value: Any): Unit = {
    this.serviceMap.put(key, value)
  }

  def addAllServices(context: Iterable[(String, Any)]): Unit = {
    this.serviceMap.addAll(context)
  }

  def findService[T: ClassTag](): Option[T] = {
    findService(classTypeToString[T])
  }

  def service[T: ClassTag](): T = {
    service[T](classTypeToString[T])
  }

  def findService[T: ClassTag](key: String): Option[T] = {

    this.serviceMap.get(key).map {
      case x: T => x
      case notX => {
        val debug = classTypeToString[T]
        val wrong = notX.getClass
        throw new IllegalArgumentException(s"$key is of type $wrong, not instance of expected type $debug")
      }
    }
  }

  def service[T: ClassTag](key: String): T = {
    findService[T](key).getOrElse(
      throw new IllegalStateException(s"ServiceMap does not contain key $key")
    )
  }
}

object ServiceMap extends ServiceMap