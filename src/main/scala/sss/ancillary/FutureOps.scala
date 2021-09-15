package sss.ancillary


import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}


trait LoggingFutureSupport {

  self : Logging =>

  def LoggingFuture[T](f: => T)(implicit ec: ExecutionContext): Future[T] = Future {
    f
  } andThen {
    case Failure(e) => log.error(s"LoggingFuture ${e.toString}")
    case Success(s) => log.trace(s.toString.take(100))
  }

}

object LoggingFutureSupport extends LoggingFutureSupport with Logging

object FutureOps {

  implicit class AwaitResult[T](val f: Future[T]) extends AnyVal {
    def await(d: Duration = 10.seconds): T = {
      Await.result(f, d)
    }

    def toTry(d: Duration = 10.seconds): Try[T] = {
      Try(Await.result(f, d))
    }
  }

}
