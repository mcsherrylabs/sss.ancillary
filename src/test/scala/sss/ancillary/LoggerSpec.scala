
package sss.ancillary

import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec

import scala.io.Source
import org.scalatest.matchers.should.Matchers

object LoggerSpec {

  def assertIsInLogFile(msg: String) = {
    val fileContents = {
      val buffer = Source.fromFile("ancillary-test-logging.log")
      val result = buffer.getLines().mkString
      buffer.close()
      result
    }
    assert(fileContents.contains(msg))
  }
}
class LoggerSpec extends AnyFlatSpec with Matchers with BeforeAndAfterAll with Logging {


  import LoggerSpec.assertIsInLogFile


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