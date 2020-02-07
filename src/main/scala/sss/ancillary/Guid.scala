package sss.ancillary

import java.util
import java.util.UUID

import ByteArrayEncodedStrOps._

object Guid {
  val guidSize = 16

  def apply(guidBase64Str: String): Guid =
    Guid(guidBase64Str.fromBase64Str)

  def apply(): Guid = {
    import java.nio.ByteBuffer
    val uuid = UUID.randomUUID()
    val bb = ByteBuffer.wrap(new Array[Byte](16))
    bb.putLong(uuid.getMostSignificantBits)
    bb.putLong(uuid.getLeastSignificantBits)
    Guid(bb.array())
  }
}

case class Guid(value: Array[Byte]) {
  require(value.length == Guid.guidSize)

  override def equals(o: Any): Boolean = o match {
    case that: Guid => value.sameElements(that.value)
    case _ => false
  }

  override def hashCode(): Int = util.Arrays.hashCode(value)

  override def toString: String = value.toBase64Str
}
