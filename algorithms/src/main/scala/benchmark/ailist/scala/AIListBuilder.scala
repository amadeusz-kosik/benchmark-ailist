package benchmark.ailist.scala

import benchmark.{Configuration, Interval}

import scala.collection.mutable.ArrayBuffer


object AIListBuilder {

  def build(configuration: Configuration, intervals: Array[Interval]): Array[AIList] = {
    assert(configuration.intervalsCountToCheckLookahead >= configuration.intervalsCountToTriggerExtraction)
    assert(configuration.intervalsCountToCheckLookahead > 0)
    assert(configuration.maximumComponentSize > 0)

    intervals.sorted(Ordering.by[Interval, (Long, Long)](i => (i.to, i.from)))

    var arrayRead = 0
    var arrayWrite = 0

    val componentStarts = ArrayBuffer[Integer]()
    componentStarts += 0

    val componentLengths = ArrayBuffer[Integer]()
    var componentLength = 0

    var sidecar = ArrayBuffer[Interval]()

    while (arrayRead < intervals.length) {
      if (componentLength >= configuration.maximumComponentSize) {
        componentStarts += arrayWrite

        var backfill = arrayWrite

        sidecar.foreach { sidecarInterval =>
          intervals(backfill) = sidecarInterval
          backfill += 1
        }

        sidecar = ArrayBuffer[Interval]()
        componentLengths += componentLength

        componentLength = 0
        arrayRead = arrayWrite
      }

      def nextInterval = intervals(arrayRead)

      val includeInCurrentComponent = {
        val lookaheadCoverage = intervals.view
          .slice(arrayRead + 1, intervals.length)
          .take(configuration.intervalsCountToCheckLookahead)
          .count(_.to <= nextInterval.to)

        lookaheadCoverage <= configuration.intervalsCountToTriggerExtraction
      }

      if (includeInCurrentComponent) {
        // Write only if necessary
        if (arrayRead != arrayWrite)
          intervals(arrayWrite) = intervals(arrayRead)

        arrayRead += 1
        arrayWrite += 1
        componentLength += 1
      } else {
        // Kick the interval out from the current interval
        sidecar += nextInterval
        arrayRead += 1
      }
    }

    // Commit the last component
    if(componentLength != 0) {
      componentLengths += componentLength
      componentLength = 0
    }

    if(arrayWrite != arrayRead) {
      var backfill = arrayWrite

      sidecar.foreach { sidecarInterval =>
        intervals(backfill) = sidecarInterval
        backfill += 1
      }

      componentStarts += arrayWrite
      componentLengths += sidecar.length
    }


    componentStarts
      .zip(componentLengths).map { case (cStart, cLength) =>
        AIList.apply(intervals.view.slice(cStart, cStart + cLength).toArray)
      }
      .toArray
  }
}
