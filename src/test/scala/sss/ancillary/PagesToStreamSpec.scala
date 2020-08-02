package sss.ancillary

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
/**
  * Created by alan on 2/11/16.
  */
class PagesToStreamSpec extends AnyFlatSpec with Matchers {

  def inifiniteNumberStream(number: Int): LazyList[Int] = LazyList.cons(number, inifiniteNumberStream(number + 1))

  def pager(offset: Long, pageSize: Int): Seq[Int] = {
    //println(s"Calling pager with $offset")
    inifiniteNumberStream(0).slice(offset.toInt, offset.toInt + pageSize)
  }

  "Page to Stream " should "flatten a seq" in {

    val p = PagesToStream(pager, 10)
    val result = p.take(100)
    assert(result === (0 until 100), "Should return a flat seq 0 to 99")
  }

  it should "handle a stream end" in {
    def pager(offset: Long, pageSize: Int): Seq[Int] = Seq(0,1,2)
    val p = PagesToStream(pager, 10)
    val result = p.take(100)
    assert(result === (0 to 2), "Should return a short seq")
  }

  it should "handle an empty stream" in {
    def pager(offset: Long, pageSize: Int): Seq[Int] = Seq.empty
    val p = PagesToStream(pager, 10)
    val result = p.take(100)
    assert(result === Seq.empty, "Should return a short seq")
  }

  it should "not accept a zero pagesize" in {
    intercept[Exception](PagesToStream(pager, 0))
  }

}
