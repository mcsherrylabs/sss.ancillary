package sss.ancillary

import java.util


import scala.collection.mutable


/**
  * Copyright Stepping Stone Software Ltd. 2016, all rights reserved. 
  * mcsherrylabs on 3/11/16.
  */
trait ByteArrayComparisonOps {

  implicit class ByteArrayComparison(ary: Array[Byte]) {
    def isSame(otherAry: mutable.WrappedArray[Byte]): Boolean = otherAry == ary.toSeq
    def hash: Int = ary.toSeq.hashCode()
  }

  implicit class SeqByteArrayComparison(ary: Seq[Array[Byte]]) {
    def isSame(otherAry: Seq[Array[Byte]]): Boolean = otherAry.corresponds(ary)(util.Arrays.equals)
    def hash: Int = ary.map(_.toSeq.hashCode).sum
  }
}

object ByteArrayComparisonOps extends ByteArrayComparisonOps