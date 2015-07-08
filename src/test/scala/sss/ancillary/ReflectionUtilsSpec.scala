
package sss.ancillary

import org.scalatest._

trait Marker {
  def getStr: String
}

object myTest extends Marker {
  def getStr = "it's me!"
}

class ReflectionUtilsSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  "Util " should " be able to  load an object by name " in {

    val thing = ReflectionUtils.getInstance[Marker]("sss.ancillary.myTest")

    assert(thing.getStr == "it's me!")
  }

}