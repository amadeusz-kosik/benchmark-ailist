package benchmark.ailist.scala

import benchmark.Interval

import scala.util.Random


object BenchmarkDataGenerator {

  def databaseSources(databaseCount: Int): Map[String, Array[Interval]] =
    Map(
      "consecutive" -> BenchmarkDataGenerator.consecutive(databaseCount),
      "sparse"      -> BenchmarkDataGenerator.sparse(databaseCount),
      "overlapping" -> BenchmarkDataGenerator.overlapping(databaseCount),
      "lasting"     -> BenchmarkDataGenerator.lasting(databaseCount),
      "mixed"       -> BenchmarkDataGenerator.mixed(databaseCount),
      "outliers"    -> BenchmarkDataGenerator.outliers(databaseCount)
    )

  def querySources(databaseCount: Int, queryCount: Int): Array[Interval] = {
    val queryWidth = databaseCount * 10 / queryCount

    shuffle {
      (0 until queryCount)
        .map { queryIndex => Interval(queryIndex * queryWidth, (queryIndex + 1) * queryWidth - 1) }
    }
  }

  private def shuffle(intervals: IndexedSeq[Interval]): Array[Interval] = {
    Random
      .shuffle(intervals)
      .toArray
  }

  private def consecutive(rowsCount: Int): Array[Interval] = {
    assert(rowsCount > 0)

    shuffle {
      (0 until rowsCount)
        .map { i => Interval(i, i)}
    }
  }

  private def sparse(rowsCount: Int): Array[Interval] = {
    assert(rowsCount > 0)

    val Gap = 10

    shuffle {
      (0 until rowsCount)
        .map { i => Interval(i * Gap, i * Gap + (Gap / 2))}
    }
  }

  private def overlapping(rowsCount: Int): Array[Interval] = {
    assert(rowsCount > 0)

    val Gap = 10
    val Overlap = 5

    shuffle {
      (0 until rowsCount)
        .map { i => Interval(i * Gap - Overlap, i * Gap + Overlap) }
    }
  }

  private def lasting(rowsCount: Int): Array[Interval] = {
    assert(rowsCount > 0)

    val Gap = 10
    val Width = 1000

    shuffle {
      (0 until rowsCount)
        .map { i => Interval(i * Gap, i * Gap + Width) }
    }
  }

  private def mixed(rowsCount: Int): Array[Interval] = {
    assert(rowsCount > 0)

    val ShortOdd = 5
    val ShortEven = 3
    val Long = 50
    val LongChance = 20

    shuffle {
      (0 until rowsCount)
        .map { i =>
          if(i % LongChance == 0)
            Interval(i / 2, i / 2 + Long)
          else if(i % 2 == 0)
            Interval(i / 2, i / 2 + ShortEven)
          else
            Interval(i / 2, i / 2 + ShortOdd)
        }
    }
  }

  private def outliers(rowsCount: Int): Array[Interval] = {
    assert(rowsCount > 0)

    val Gap = 10
    val OutlierChance = 1000
    val OutlierWidth = 10000
    val Width = 5

    shuffle {
      (0 until rowsCount)
        .map { i =>
          if (i % OutlierChance == 0)
            Interval(i * Gap, i * Gap + OutlierWidth)
          else
            Interval(i * Gap, i * Gap + Width)
        }
    }
  }
}
