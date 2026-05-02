package benchmark.ailist

import org.openjdk.jmh.annotations.{Benchmark, Param, Scope, State}
import org.openjdk.jmh.infra.Blackhole


@State(Scope.Benchmark)
class ScalaAIListBenchmark {

  @Param(Array(
    "consecutiveIntervals",
    "overlappingIntervals",
    "lastingIntervals",
    "shortPoissonIntervals",
    "mixed1Intervals",
    "mixed2Intervals",
    "mixed3Intervals",
    "mixed4Intervals"
  ))
  var dataSourceName: String = _

  @Benchmark
  def listBuilding(blackhole: Blackhole): Unit = {

  }
}
