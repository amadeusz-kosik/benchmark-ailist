package benchmark.ailist

import benchmark.Configuration
import benchmark.ailist.scala.BenchmarkDataGenerator
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

import _root_.java.util.concurrent.TimeUnit


@BenchmarkMode(Array(Mode.AverageTime))
@Fork(1)
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 10, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
class AIListConfigurationBenchmarkV2 {

  @Param(Array(
    "consecutive",
    "sparse",
    "overlapping",
    "lasting",
    "mixed",
    "outliers"
  ))
  var databaseSourceSet: String = _

  final private val databaseCount = 100000
  final private val queryCount    = 1000

  private val databaseSources = BenchmarkDataGenerator.databaseSources(databaseCount)
  private val query           = BenchmarkDataGenerator.querySources(databaseCount, queryCount)

  @Param(Array("2", "4", "8"))
  var intervalsCountToCheckLookahead: String = _

  @Param(Array("2"))
  var intervalsCountToTriggerExtractionFraction: String = _

  @Param(Array("100000"))
  var maximumComponentSize: String = _

  @Param(Array("100"))
  var earlyStopLeftoverCount: String = _

  @Benchmark
  def benchmarkScalaAIListDeque(blackhole: Blackhole): Unit = {
    val configuration = Configuration(
      intervalsCountToCheckLookahead    = intervalsCountToCheckLookahead.toInt,
      intervalsCountToTriggerExtraction = intervalsCountToCheckLookahead.toInt / intervalsCountToTriggerExtractionFraction.toInt,
      maximumComponentSize              = maximumComponentSize.toInt,
      earlyStopLeftoverCount            = earlyStopLeftoverCount.toInt
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
