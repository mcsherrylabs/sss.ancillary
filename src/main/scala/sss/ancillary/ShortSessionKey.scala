package sss.ancillary

import java.util.concurrent.atomic.AtomicInteger

import sss.ancillary.Serialize._

object ShortSessionKey {

  implicit class SessionKeyToBytes(sessKey: ShortSessionKey) extends ToBytes {
    override def toBytes: Array[Byte] = ShortSerializer(sessKey.value).toBytes
  }

  implicit class SessionKeyFromBytes(bs:Array[Byte]) {
    def toShortSessionKey: ShortSessionKey = ShortSessionKey(bs.extract(ShortDeSerialize))
  }


  private val sessionCounter: AtomicInteger = new AtomicInteger(0)

  def apply(): ShortSessionKey = {
    ShortSessionKey(sessionCounter.incrementAndGet().toShort)
  }
}

case class ShortSessionKey(value:Short)
