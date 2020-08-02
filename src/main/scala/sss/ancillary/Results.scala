package sss.ancillary

object Results {

  trait ResultOrMsg[R] {
    def orErrMsg[E](msg: => E): Result[R, E]
  }

  type Result[+R, E] = Either[R, Errors[E]]
  type Errors[E] = List[E]
  type ErrMsg = String
  type Error[E] = Result[_, E]
  type OkResult = Either[_, Errors[ErrMsg]]

  def ok[R](r: R = ()) = Left(r)

  def error(msg: String*): Error[String] = Right(msg.toList)

  implicit class ResultOps[R, E](val r: Result[R, E]) extends AnyVal {

    def isOk: Boolean = r.isLeft
    def isError: Boolean = r.isRight
    def errors: Errors[E] = r.getOrElse(throw new RuntimeException("Use isError guard"))
    def result:R = r.left.getOrElse(throw new RuntimeException("Use isOk guard"))

    def andThen[R](other: => Result[R, E]): Result[R, E] = {
      if (r.isOk && other.isOk) other
      else
        Right(
          r.getOrElse(List[E]()) ++
            other.getOrElse(List[E]())
        )
    }

    def ifOk(other: R => Result[_, E]): Result[_, E] = {
      if (r.isOk) other(r.result)
      else r
    }

    def ifNotOk(other: => Result[_, E]): Result[_, E] = {
      if (!r.isOk)
        if (other.isOk) other
        else
          Right(
            r.getOrElse(List()) ++
              other.getOrElse(List())
          )
      else r
    }
  }

  implicit class FromOpt[R](val r: Option[R]) extends ResultOrMsg[R] {
    def orErrMsg[E](msg: => E): Result[R, E] = {
      if (r.isDefined) Left(r.get)
      else Right(List(msg))
    }
  }

  implicit class FromBool(val r: Boolean) extends ResultOrMsg[Boolean] {
    def orErrMsg[E](msg: => E): Result[Boolean, E] = {
      if (r) Left(true)
      else Right(List(msg))
    }

    def orErrMsg(msg: String): OkResult = {
      if (r) Left(true)
      else Right(List(msg))
    }
  }
}
