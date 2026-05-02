package benchmark.ailist

import benchmark.Interval
import benchmark.ailist.java
import benchmark.ailist.scala
import _root_.java.util


object BenchmarkAdapter {

  def benchmarkJavaAIList(database: Array[Interval], query: Array[Interval]): Array[(Interval, Interval)] = {
    import _root_.scala.jdk.CollectionConverters._

    val aiListBuilder = new java.AIListBuilder(java.Configuration.DEFAULT)
    val aiLists: Array[java.AIList] = aiListBuilder.build(new util.ArrayList(database.toBuffer.asJava)).asScala.toArray

    for {
      aiList      <- aiLists
      rhsInterval <- query
      lhsInterval <- aiList.overlapping(rhsInterval).asScala
    } yield (lhsInterval, rhsInterval)
  }

  def benchmarkScalaAIList(database: Array[Interval], query: Array[Interval]): Array[(Interval, Interval)] = {
    val aiLists = scala.AIListBuilder.build(scala.Configuration.apply(), database)

    for {
      aiList      <- aiLists
      rhsInterval <- query
      lhsInterval <- aiList.overlapping(rhsInterval)
    } yield (lhsInterval, rhsInterval)
  }

  def benchmarkScalaReference(database: Array[Interval], query: Array[Interval]): Array[(Interval, Interval)] = {
    for {
      lhsInterval <- database
      rhsInterval <- query
      if Interval.overlaps(lhsInterval, rhsInterval)
    } yield (lhsInterval, rhsInterval)
  }
}
