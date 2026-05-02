package benchmark.ailist

import benchmark.{Configuration, Interval}
import _root_.java.util


object BenchmarkAdapter {

  def benchmarkJavaAIList(configuration: Configuration)(database: Array[Interval], query: Array[Interval]): Array[(Interval, Interval)] = {
    import _root_.scala.jdk.CollectionConverters._

    val aiListBuilder = new java.AIListBuilder(configuration)
    val aiLists: Array[java.AIList] = aiListBuilder.build(new util.ArrayList(database.toBuffer.asJava)).asScala.toArray

    for {
      aiList      <- aiLists
      rhsInterval <- query
      lhsInterval <- aiList.overlapping(rhsInterval).asScala
    } yield (lhsInterval, rhsInterval)
  }

  def benchmarkScalaAIList(configuration: Configuration)(database: Array[Interval], query: Array[Interval]): Array[(Interval, Interval)] = {
    val aiLists = scala.AIListBuilder.build(configuration, database)

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
