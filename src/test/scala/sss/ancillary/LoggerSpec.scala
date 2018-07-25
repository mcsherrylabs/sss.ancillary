
package sss.ancillary

import org.scalatest._

import scala.io.Source

class LoggerSpec extends FlatSpec with Matchers with BeforeAndAfterAll with Logging {



  private def assertIsInLogFile(msg: String) = {
    val fileContents = Source.fromFile("ancillary-test-logging.log").getLines.mkString
    assert(fileContents.contains(msg))
  }

  "LogFactory " should " write to file " in {
    val m = LogFactory.getLogger("tag1")
    val msg = s"Write warn log"
    m.warn(msg)
    assertIsInLogFile(msg)

  }

  it should " write log through the trait " in {
    val msg = s"Write log through trait"
    log.info(msg)
    assertIsInLogFile(msg)
  }

  it should " not trigger substitution " in {

    lazy val msg = {
      throw new RuntimeException("Should never see this if scala logging works")
      "SUBSTUTION"
    }
    log.debug(s"Don't Write DEBUG log $msg")
  }

}