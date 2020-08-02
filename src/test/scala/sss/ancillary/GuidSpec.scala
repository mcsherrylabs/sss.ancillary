package sss.ancillary

import java.util.Date

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
/**
  * Created by alan on 2/11/16.
  */
class GuidSpec extends AnyFlatSpec with Matchers {

  val g = Guid()
  val g2 = Guid()

  "Guid" should "respect equals" in {
    assert(g != g2)
    assert(g2 != g)
    assert(g == g)
    assert(g2 == g2)
  }

  it should "convert from and to byte array" in {
    assert(g.value.sameElements(Guid(g.value).value))
    assert(!g.value.sameElements(g2.value) )
  }

  it should "convert from and to String" in {
    assert(Guid(g.toString).value.sameElements(g.value))
    assert(Guid(g2.toString).value.sameElements(g2.value))
    assert(!Guid(g2.toString).value.sameElements(g.value))
  }

}
