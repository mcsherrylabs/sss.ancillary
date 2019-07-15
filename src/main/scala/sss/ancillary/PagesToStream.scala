package sss.ancillary

object PagesToStream {

  def apply[T](f: (Long, Int) => Seq[T], pageSize: Int): Stream[T] = {

    require(pageSize > 0, s"Pagesize ($pageSize) must be greater than 0")

    def toStream(offset: Long): Stream[Seq[T]] = {
      f(offset, pageSize) match {
        case segment if segment.size == pageSize => segment #:: toStream(offset + pageSize)
        case segment if segment.nonEmpty => segment #:: Stream.empty
        case segment => Stream.empty
      }

    }
    toStream(0).flatten
  }

}
