package benchmark.ailist.scala

import benchmark.Interval

object BenchmarkDataGenerator {

  def consecutive(rowsCount: Int): Array[Interval] = {
    assert(rowsCount > 0)

    (0 until rowsCount)
      .map { i =>
        Interval(i, i)
      }
      .toArray
  }

  def sparse(rowsCount: Int): Array[Interval] = {
    assert(rowsCount > 0)

    val Gap = 10

    (0 until rowsCount)
      .map { i =>
        Interval(i * Gap, i * Gap + (Gap / 2))
      }
      .toArray
  }

  def overlapping(rowsCount: Int): Array[Interval] = {
    assert(rowsCount > 0)

    val Gap = 10
    val Overlap = 5

    (0 until rowsCount)
      .map { i =>
        Interval(i * Gap - Overlap, i * Gap + Overlap)
      }
      .toArray
  }

  def lasting(rowsCount: Int): Array[Interval] = {
    assert(rowsCount > 0)

    val Gap = 10
    val Width = 1000

    (0 until rowsCount)
      .map { i =>
        Interval(i * Gap, i * Gap + Width)
      }
      .toArray
  }

  def outliers(rowsCount: Int): Array[Interval] = {
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
