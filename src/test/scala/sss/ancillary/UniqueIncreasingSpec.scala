package sss.ancillary

import java.util.Date

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by alan on 2/11/16.
  */
class UniqueIncreasingSpec extends FlatSpec with Matchers {

  "UniqueIncreasing" should "produce diferent increasing numbers" in {
    val start = new Date()
    val results = Seq.fill(1000)(UniqueIncreasing.apply)
    val done = new Date()
    assert(results.toSet.size === results.size, "Must all be unique")
    results.foldLeft(0l)((acc, e: Long) => {
      assert(acc < e, "Must be bigger")
      e
    }
    )

    val maxDrift = done.getTime - start.getTime

    //println(s"maxDrift is $maxDrift ms")

    results.foreach (l => {
      val d = new Date(UniqueIncreasing.toMilliseconds(l))
      assert(start.getTime + maxDrift >= d.getTime, s"Max drift is $maxDrift but diff is ${start.getTime-d.getTime}")
    })
  }


}
