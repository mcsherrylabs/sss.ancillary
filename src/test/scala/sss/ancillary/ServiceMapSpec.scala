package sss.ancillary

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{Failure, Try}

/**
  * Created by alan on 2/11/16.
  */
class ServiceMapSpec extends AnyFlatSpec with Matchers {

  class MyType(val v: Int = 8)
  class MyOtherType()
  class MyDerivedType(override val v: Int = 9) extends MyType(v)

  "ServiceMap" should "get back as expected" in {
    val m: Map[String, Long] = Map.empty
    ServiceMap.addService("m", m)
    val n = ServiceMap.service[Map[String, Long]]("m")
    n shouldEqual(m)
  }

  it should "add and retrieve a Map by type name" in {
    val m: Map[String, Long] = Map.empty
    ServiceMap.addService(m)
    val n = ServiceMap.service[Map[String, Long]]()
    n shouldEqual (m)
  }

  it should "fail if the type is wrong" in {
    ServiceMap.addService("m" , new MyType)
    val result = Try(ServiceMap.service[MyOtherType]("m"))
    result should matchPattern {
      case Failure(_: IllegalArgumentException) =>
    }

    ServiceMap.service[MyType]("m").v shouldBe(8)
    ServiceMap.removeService("m").nonEmpty shouldBe true
    ServiceMap.findService("m").isEmpty shouldBe true
  }

  it should "support derived types" in {
    ServiceMap.addService(new MyDerivedType())
    ServiceMap.addService(new MyType())

    val gotMyType = ServiceMap.service[MyType]()
    gotMyType.v shouldBe 8
    val gotMyDerivedType = ServiceMap.service[MyDerivedType]()
    gotMyDerivedType.v shouldBe 9

  }

  it should "support find" in {
    val found = ServiceMap.findService[MyDerivedType]()
    found.nonEmpty shouldBe true
    found.get.v shouldBe 9

    val notFound = ServiceMap.findService[MyOtherType]()
    notFound shouldBe None
  }

  it should "support remove" in {
    val found = ServiceMap.findService[MyDerivedType]()
    found.nonEmpty shouldBe true
    found.get.v shouldBe 9

    ServiceMap.removeService[MyDerivedType]()
    val notFound = ServiceMap.findService[MyDerivedType]()
    notFound.isEmpty shouldBe(true)
  }

  it should "support add All and remove" in {
    val m = Map("a" -> new MyType(1), "b" -> new MyType(2))

    ServiceMap.addAllServices(m)
    ServiceMap.service[MyType]("a").v shouldBe 1
    ServiceMap.service[MyType]("b").v shouldBe 2
    ServiceMap.removeService("a").nonEmpty shouldBe true
    ServiceMap.removeService("b").nonEmpty shouldBe true
    ServiceMap.removeService("a").nonEmpty shouldBe false
    ServiceMap.removeService("b").nonEmpty shouldBe false

  }
}
