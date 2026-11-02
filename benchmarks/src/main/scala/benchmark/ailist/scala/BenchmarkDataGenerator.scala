package benchmark.ailist.scala

import benchmark.Interval


object BenchmarkDataGenerator {

  def databaseSources(databaseCount: Int): Map[String, Array[Interval]] =
    Map(
      "consecutive" -> BenchmarkDataGenerator.consecutive(databaseCount),
      "sparse"      -> BenchmarkDataGenerator.sparse(databaseCount),
      "overlapping" -> BenchmarkDataGenerator.overlapping(databaseCount),
      "lasting"     -> BenchmarkDataGenerator.lasting(databaseCount),
      "outliers"    -> BenchmarkDataGenerator.outliers(databaseCount)
    )

  def querySources(databaseCount: Int, queryCount: Int): Array[Interval] = {
    val queryWidth = databaseCount * 10 / queryCount

    (0 until queryCount)
      .map { queryIndex => Interval(queryIndex * queryWidth, (queryIndex + 1) * queryWidth - 1) }
      .toArray
  }

  private def consecutive(rowsCount: Int): Array[Interval] = {
    assert(rowsCount > 0)

    (0 until rowsCount)
      .map { i =>
        Interval(i, i)
      }
      .toArray
  }

  private def sparse(rowsCount: Int): Array[Interval] = {
    assert(rowsCount > 0)

    val Gap = 10

    (0 until rowsCount)
      .map { i =>
        Interval(i * Gap, i * Gap + (Gap / 2))
      }
      .toArray
  }

  private def overlapping(rowsCount: Int): Array[Interval] = {
    assert(rowsCount > 0)

    val Gap = 10
    val Overlap = 5

    (0 until rowsCount)
      .map { i =>
        Interval(i * Gap - Overlap, i * Gap + Overlap)
      }
      .toArray
  }

  private def lasting(rowsCount: Int): Array[Interval] = {
    assert(rowsCount > 0)

    val Gap = 10
    val Width = 1000

    (0 until rowsCount)
      .map { i =>
        Interval(i * Gap, i * Gap + Width)
      }
      .toArray
  }

  private def outliers(rowsCount: Int): Array[Interval] = {
    assert(rowsCount > 0)

    val Gap = 10
    val OutlierChance = 1000
    val OutlierWidth = 10000
    val Width = 5

    (0 until rowsCount)
      .map { i =>
        if (i % OutlierChance == 0)
          Interval(i * Gap, i * Gap + OutlierWidth)
        else
          Interval(i * Gap, i * Gap + Width)
      }
      .toArray
  }
}
