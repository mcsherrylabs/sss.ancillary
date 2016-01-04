
package sss.ancillary

import com.typesafe.config._
import org.scalatest._

import scala.collection.JavaConversions._

trait MimicInterface {
  val name: String
  val estimatedCost: Int
  val estimatedCostOpt: Option[Int]
  val notThereOpt: Option[BigDecimal]
  val ingredients: java.lang.Iterable[String]
}

class DynConfigSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  "DynConfig " should " be able to miminic an interface given a config name" in {

    val sut = DynConfig[MimicInterface]("dish")
    assert(sut.name == "SomeCompany")
    assert(sut.ingredients.toSet == Set("potato", "bacon", "onion", "salt", "pepper"))
    assert(sut.estimatedCost == 10)
  }

  it should " be able to miminic an interface given a config" in {

    val conf = ConfigFactory.load().getConfig("dish")
    val sut = DynConfig[MimicInterface](conf)
    assert(sut.name == "SomeCompany")
  }

  it should " be able to return a property ending in Opt as as Option " in {

    val sut = DynConfig[MimicInterface]("dish")
    assert(sut.name == "SomeCompany")
    assert(sut.estimatedCostOpt == Some(10))
    assert(sut.notThereOpt == None)
  }

  it should " be able to honour equals and hashCode as normal " in {

    val sut1 = DynConfig[MimicInterface]("dish")
    val sut2 = DynConfig[MimicInterface]("dish")

    assert(!sut1.equals(sut2))
    assert(sut1.hashCode == sut2.hashCode)

  }
}