package sss.ancillary

import com.typesafe.config.Config
import java.lang.reflect.Method
import scala.reflect.runtime.universe._

/**
 * @author alan
 */
object DynConfig {
  def apply[T: TypeTag](configName: String): T = {
    apply(ConfigureFactory.config(configName))
  }

  def apply[T: TypeTag](config: Config): T = {

    val ignore = Set("equals", "hashCode", "clone", "wait", "finalize", "getClass", "==")

    def invoker(proxy: Object, method: Method, args: Array[Object]): Object = {
      val name = method.getName
      if (ignore.contains(name)) method.invoke(this, args: _*)
      else {
        if (name.endsWith("Opt")) {
          val nameMinusOpt = name.substring(0, name.length - 3)
          if (config.hasPath(nameMinusOpt)) Option(config.getAnyRef(nameMinusOpt))
          else None
        } else config.getAnyRef(name)
      }
    }

    ReflectionUtils.createProxy[T](invoker)
  }

}

