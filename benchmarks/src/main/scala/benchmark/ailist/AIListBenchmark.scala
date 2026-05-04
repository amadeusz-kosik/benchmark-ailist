package benchmark.ailist

import benchmark.ailist.java
import benchmark.ailist.scala
import benchmark.ailist.scala.BenchmarkDataGenerator
import benchmark.{Configuration, Interval}
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

import _root_.java.util
import _root_.java.util.concurrent.TimeUnit


@State(Scope.Benchmark)
class AIListBenchmark {

  @Param(Array(
    "consecutive",
    "sparse"
//    "overlapping",
//    "lasting",
//    "outliers"
  ))
  var databaseSourceSet: String = _

  final private val databaseCount = 100000
  final private val queryCount = 1000

  private val databaseSources: Map[String, Array[Interval]] = {
    Map(
      "consecutive" -> BenchmarkDataGenerator.consecutive(databaseCount),
      "sparse"      -> BenchmarkDataGenerator.sparse(databaseCount)
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
  def benchmarkJavaAIList(blackhole: Blackhole): Unit = {
    val database = databaseSources(databaseSourceSet)
    val aiListBuilder = new java.AIListBuilder(Configuration.apply())
    val aiLists = aiListBuilder.buildFromIterator(database.iterator)

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
  def benchmarkScalaAIListMemoryOptimized(blackhole: Blackhole): Unit = {
    val database = databaseSources(databaseSourceSet)
    val query    = scalaQuery
    val aiLists = scala.AIListBuilder.buildMemoryOptimized(Configuration.apply(), database.iterator)

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
  def benchmarkScalaAIListSpeedOptimized(blackhole: Blackhole): Unit = {
    val database = databaseSources(databaseSourceSet)
    val query    = scalaQuery
    val aiLists = scala.AIListBuilder.buildSpeedOptimized(Configuration.apply(), database.iterator)

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
  def benchmarkScalaReference(blackhole: Blackhole): Unit = {
    val database = databaseSources(databaseSourceSet)
    val query    = scalaQuery

    val result = for {
      lhsInterval <- database
      rhsInterval <- query
      if Interval.overlaps(lhsInterval, rhsInterval)
    } yield (lhsInterval, rhsInterval)

    blackhole.consume(result)
  }
}
