package sss.ancillary

import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config

trait Configure {

  implicit lazy val config: Config = ConfigFactory.load()
  def config(name: String): Config = config.getConfig(name)
}

object ConfigureFactory extends Configure