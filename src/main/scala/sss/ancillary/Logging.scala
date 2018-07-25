package sss.ancillary

import com.typesafe.scalalogging.Logger

trait Logging {
  lazy val log: Logger = Logger(this.getClass())
}

object LogFactory extends Logging {

  def getLogger(category: String): Logger = {
    Logger(category)
  }

}
