package benchmark.ailist

import benchmark.ailist.java
import benchmark.ailist.scala
import benchmark.{Configuration, Interval}
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

import _root_.java.util
import _root_.java.util.concurrent.TimeUnit


@State(Scope.Benchmark)
class ScalaAIListBenchmark {

  @Param(Array(
    "consecutive"
//    "sparse",
//    "overlapping",
//    "lasting",
//    "outliers"
  ))
  var databaseSourceSet: String = _

  @Param(Array(
    "100",
    "1000",
    "10000"
  ))
  var queryRowLength: Long = _

  final private val databaseCount = 100000
  final private val queryCount = 1000

  private val javaDatabaseSources: Map[String, util.ArrayList[Interval]] = {
    Map(
      "consecutive" -> java.DataGenerator.consecutive(databaseCount)
    )
  }

  private val scalaDatabaseSources: Map[String, Array[Interval]] = {
    Map(
      "consecutive" -> scala.DataGenerator.consecutive(databaseCount)
    )
  }

  private val javaQuery: util.ArrayList[Interval] = {
    val result = new util.ArrayList[Interval](queryCount)
    val queryWidth = databaseCount / queryCount

    (0 until queryCount)
      .foreach { queryIndex => result.add(Interval(queryIndex * queryWidth, (queryIndex + 1) * queryWidth - 1)) }

    result
  }

  private val scalaQuery: Array[Interval] = {
    val queryWidth = databaseCount / queryCount

    (0 until queryCount)
      .map { queryIndex => Interval(queryIndex * queryWidth, (queryIndex + 1) * queryWidth - 1) }
      .toArray
  }


  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @Fork(1)
  @Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
  @Measurement(iterations = 10, time = 3, timeUnit = TimeUnit.SECONDS)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  def benchmarkJavaAIList(blackhole: Blackhole) = {
    val database = javaDatabaseSources(databaseSourceSet)
    val aiListBuilder = new java.AIListBuilder(Configuration.apply())
    val aiLists = aiListBuilder.build(database)

    val result = new util.LinkedList[(Interval, Interval)]()

    aiLists.forEach ( aiList =>
      javaQuery.forEach( rhsInterval =>
        aiList.overlapping(rhsInterval).forEachRemaining(lhsInterval => result.push((lhsInterval, rhsInterval)))
      )
    )
    blackhole.consume(result)
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @Fork(1)
  @Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
  @Measurement(iterations = 10, time = 3, timeUnit = TimeUnit.SECONDS)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  def benchmarkScalaAIList(blackhole: Blackhole) = {
    val database = scalaDatabaseSources(databaseSourceSet)
    val query    = scalaQuery
    val aiLists = scala.AIListBuilder.build(Configuration.apply(), database)

    val result = for {
      aiList      <- aiLists
      rhsInterval <- query
      lhsInterval <- aiList.overlapping(rhsInterval)
    } yield (lhsInterval, rhsInterval)
    blackhole.consume(result)
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @Fork(1)
  @Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
  @Measurement(iterations = 10, time = 3, timeUnit = TimeUnit.SECONDS)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  def benchmarkScalaReference(blackhole: Blackhole) = {
    val database = scalaDatabaseSources(databaseSourceSet)
    val query    = scalaQuery

    val result = for {
      lhsInterval <- database
      rhsInterval <- query
      if Interval.overlaps(lhsInterval, rhsInterval)
    } yield (lhsInterval, rhsInterval)
    blackhole.consume(result)
  }
}
