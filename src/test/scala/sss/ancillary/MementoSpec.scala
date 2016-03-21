
package sss.ancillary

import org.scalatest._

class MementoSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  val msg = "Hello world, what's going on???"

  "Memento " should " save a bit of text " in {

    val m = Memento("tag1")
    m.write(msg)

  }

  it should " be able to retrieve msg " in {
     assert(Memento("tag1").read === Some(msg))
  }

  it should " be able to clear msg " in {
    val m = Memento("tag1")
    m.clear
    assert(m.read === None)
  }

}