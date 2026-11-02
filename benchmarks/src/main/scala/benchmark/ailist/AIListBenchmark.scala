package benchmark.ailist

import benchmark.ailist.scala.BenchmarkDataGenerator
import benchmark.{Configuration, Interval}
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

import _root_.java.util
import _root_.java.util.concurrent.TimeUnit


@BenchmarkMode(Array(Mode.AverageTime))
@Fork(1)
@Warmup(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 90, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
class AIListBenchmark {

  @Param(Array(
    "consecutive",
    "sparse",
    "overlapping",
    "lasting",
    "outliers"
  ))
  var databaseSourceSet: String = _

  final private val databaseCount = 100000
  final private val queryCount    = 1000

  private val databaseSources = BenchmarkDataGenerator.databaseSources(databaseCount)
  private val query           = BenchmarkDataGenerator.querySources(databaseCount, queryCount)


  @Benchmark
  def benchmarkJavaAIListArray(blackhole: Blackhole): Unit = {
    val database = databaseSources(databaseSourceSet)
    val aiListBuilder = new java.AIListBuilder(Configuration.apply())
    val aiLists = aiListBuilder.buildArrayFromIterator(database.iterator)

    val result = new util.LinkedList[(Interval, Interval)]()

    aiLists.forEach ( aiList =>
      query.foreach( rhsInterval =>
        aiList.overlapping(rhsInterval).forEachRemaining(lhsInterval => result.push((lhsInterval, rhsInterval)))
      )
    )
    blackhole.consume(result)
  }

  @Benchmark
  def benchmarkJavaAIListQueue(blackhole: Blackhole): Unit = {
    val database = databaseSources(databaseSourceSet)
    val aiListBuilder = new java.AIListBuilder(Configuration.apply())
    val aiLists = aiListBuilder.buildQueueFromIterator(database.iterator)

    val result = new util.LinkedList[(Interval, Interval)]()

    aiLists.forEach ( aiList =>
      query.foreach( rhsInterval =>
        aiList.overlapping(rhsInterval).forEachRemaining(lhsInterval => result.push((lhsInterval, rhsInterval)))
      )
    )
    blackhole.consume(result)
  }

  @Benchmark
  def benchmarkScalaAIListArrayInPlace(blackhole: Blackhole): Unit = {
    val database = databaseSources(databaseSourceSet)
    val aiLists = scala.AIListBuilder.buildArrayInPlace(Configuration.apply(), database.iterator)

    val result = for {
      aiList      <- aiLists
      rhsInterval <- query
      lhsInterval <- aiList.overlapping(rhsInterval)
    } yield (lhsInterval, rhsInterval)

    blackhole.consume(result)
  }

  @Benchmark
  def benchmarkScalaAIListDeque(blackhole: Blackhole): Unit = {
    val database = databaseSources(databaseSourceSet)
    val aiLists = scala.AIListBuilder.buildDeque(Configuration.apply(), database.iterator)

    val result = for {
      aiList      <- aiLists
      rhsInterval <- query
      lhsInterval <- aiList.overlapping(rhsInterval)
    } yield (lhsInterval, rhsInterval)

    blackhole.consume(result)
  }

  @Benchmark
  def benchmarkScalaReference(blackhole: Blackhole): Unit = {
    val database = databaseSources(databaseSourceSet)

    val result = for {
      lhsInterval <- database
      rhsInterval <- query
      if Interval.overlaps(lhsInterval, rhsInterval)
    } yield (lhsInterval, rhsInterval)

    blackhole.consume(result)
  }
}
