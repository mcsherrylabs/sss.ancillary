
package sss.ancillary

import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

trait Marker {
  def getStr: String
}

object myTest extends Marker {
  def getStr = "it's me!"
}

trait PureInterface {
  def name: String
}

class ReflectionUtilsSpec extends AnyFlatSpec with Matchers with BeforeAndAfterAll {

  "Util " should " be able to  load an object by name " in {

    val thing = ReflectionUtils.getInstance[Marker](myTest.getClass.getName)

    assert(thing.getStr == "it's me!")
  }

  it should "be able to create a working proxy instance of an interface " in {
    val someName = "someName"
    val sut = ReflectionUtils.createProxy[PureInterface] {
      (obj, method, params) =>
        {
          assert(method.getName == "name")
          someName
        }
    }

    assert(sut.name == someName)
  }
}