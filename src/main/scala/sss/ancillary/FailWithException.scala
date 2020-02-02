package sss.ancillary

object FailWithException {

  def fail[T](err: Any): T = throw new RuntimeException(err.toString)
}
