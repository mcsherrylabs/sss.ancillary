package sss.ancillary

import java.util.Date
import java.util.concurrent.atomic.AtomicInteger

object UniqueIncreasing extends (() => Long) {

  type UniqueIncreasing = Long

  private val subMilliDifferentiator: AtomicInteger = new AtomicInteger(0)

  override def apply(): Long =
    (new Date().getTime << 16) +
      subMilliDifferentiator.incrementAndGet().toShort

  def toMilliseconds(uniqueIncreasing: Long): Long = uniqueIncreasing >> 16

}
