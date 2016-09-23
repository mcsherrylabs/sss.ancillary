package sss.ancillary

import org.slf4j.Logger
import org.slf4j.LoggerFactory

trait Logging {
  lazy val log: Logger = LoggerFactory.getLogger(this.getClass())

}

object LogFactory extends Logging {

  def getLogger(category: String): Logger = {
    LoggerFactory.getLogger(category)
  }

}
