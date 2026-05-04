package benchmark.ailist.scala

import benchmark.Interval

object BenchmarkDataGenerator {

  def consecutive(rowsCount: Int): Array[Interval] = intervals(rowsCount, 2, 0)

  def sparse(rowsCount: Int): Array[Interval] = {
    assert(rowsCount > 0)

    val Gap = 10

    (0 until rowsCount)
      .map { i =>
        Interval(i * Gap, i * Gap + (Gap / 2))
      }
      .toArray
  }

  def overlapping(rowsCount: Int): Array[Interval] = intervals(rowsCount, 2, 1)

  private def intervals(rowsCount: Int, width: Int, overlap: Int): Array[Interval] = {
    assert(rowsCount > 0)
    assert(width >= 0)
    assert(overlap >= 0)

    (0 until rowsCount)
      .map { i =>
        val from = i.toLong * width - (overlap / 2)
        val to = from + width - 1 + overlap

        Interval(from, to)
      }
      .toArray
  }
}

//object DataGenerator {
//
//
//  def wide(rowsCount: Int): ArrayList[Interval] = intervals(rowsCount, 256, 0, new ArrayList(rowsCount))
//
//  def lasting(rowsCount: Int): ArrayList[Interval] = intervals(rowsCount, 16, 16, new ArrayList(rowsCount))
//
//  def mixed(rowsCount: Int, layers: Int): ArrayList[Interval] = {
//    assert(layers > 0)
//    val output = new ArrayList(rowsCount * layers)
//    for (i <- 1 to layers) {
//      intervals(rowsCount, 4 * i, 0, output)
//    }
//    output
//  }
//
//  def shortPoisson(rowsCount: Int): ArrayList[Interval] = {
//    assert(rowsCount > 0)
//    val WIDTH_MEAN = 16
//    val result = new ArrayList(rowsCount)
//    val randomGenerator = new Well19937c(1337)
//    val generator = new PoissonDistribution(randomGenerator, PoissonDistribution.DEFAULT_EPSILON, PoissonDistribution.DEFAULT_MAX_ITERATIONS, WIDTH_MEAN)
//    for (i <- 0 until rowsCount) {
//      val width = generator.sample
//      val from = i.toLong * (WIDTH_MEAN + 1)
//      val to = from + width
//      result.add(new Interval(i, from, to))
//    }
//    result
//  }
//}
