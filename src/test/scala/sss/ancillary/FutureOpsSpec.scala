
package sss.ancillary

import org.scalatest._

import scala.concurrent.ExecutionContext.Implicits.global
import LoggingFutureSupport.LoggingFuture
import FutureOps._
import LoggerSpec.assertIsInLogFile

import scala.util.{Failure, Success}

class FutureOpsSpec extends FlatSpec with Matchers with BeforeAndAfterAll with Logging {

  val errMsg = "LoggingFuture_FutureOpsSpec"

  "LoggingFuture" should "not distort the result" in {

    val futureInt = LoggingFuture[Int] {
      5
    } map { i =>
      i * i
    }

    assert(futureInt.await() == 25, "The result of LoggingFuture was not 5 as expected")
  }

  it should "write future error result" in {

    val futureInt = LoggingFuture[Int] {
      throw new RuntimeException(errMsg)
    } map { i =>
      i * i
    }

    futureInt.andThen {
      case Success(_) => assert(false, "Can't succeed")
      case Failure(_) => {
        assertIsInLogFile(errMsg)
      }
    }

    assert(futureInt.toTry().isFailure, "Should have thrown RuntimeEx")

  }
}