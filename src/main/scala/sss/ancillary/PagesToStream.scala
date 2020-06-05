package sss.ancillary

object PagesToStream {

  type Pager[T] = (Long, Int) => Seq[T]

  def apply[T](f: Pager[T], pageSize: Int): LazyList[T] = {

    require(pageSize > 0, s"Pagesize ($pageSize) must be greater than 0")

    def toStream(offset: Long): LazyList[Seq[T]] = {
      f(offset, pageSize) match {
        case segment if segment.size == pageSize => segment #:: toStream(offset + pageSize)
        case segment if segment.nonEmpty => segment #:: LazyList.empty
        case segment => LazyList.empty
      }

    }
    toStream(0).flatten
  }

}
