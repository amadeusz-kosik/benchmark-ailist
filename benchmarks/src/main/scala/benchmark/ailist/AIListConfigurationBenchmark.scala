package benchmark.ailist

import benchmark.ailist.scala.BenchmarkDataGenerator
import benchmark.{Configuration, Interval}
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

import _root_.java.util.concurrent.TimeUnit


@BenchmarkMode(Array(Mode.AverageTime))
@Fork(1)
@Warmup(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 90, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
class AIListConfigurationBenchmark {

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

  @Param(Array(8, 16, 24, 32))
  var intervalsCountToCheckLookahead: Int = _

  @Param(Array(1, 2, 4))
  var intervalsCountToTriggerExtractionFraction: Int = _

  @Param(Array(1000, 10 * 1000, 100 * 1000, 1000 * 1000))
  var maximumComponentSize: Int = _

  @Param(Array(1, 10, 100, 1000, 10 * 1000))
  var earlyStopLeftoverCount: Int = _

  @Benchmark
  def benchmarkScalaAIListDeque(blackhole: Blackhole): Unit = {
    val configuration = Configuration(
      intervalsCountToCheckLookahead = intervalsCountToCheckLookahead,
      intervalsCountToTriggerExtraction = intervalsCountToCheckLookahead / intervalsCountToTriggerExtractionFraction,
      maximumComponentSize = maximumComponentSize,
      earlyStopLeftoverCount = earlyStopLeftoverCount
    )

    val database = databaseSources(databaseSourceSet)
    val aiLists = scala.AIListBuilder.buildDeque(configuration, database.iterator)

    val result = for {
      aiList      <- aiLists
      rhsInterval <- query
      lhsInterval <- aiList.overlapping(rhsInterval)
    } yield (lhsInterval, rhsInterval)

    blackhole.consume(result)
  }
}
