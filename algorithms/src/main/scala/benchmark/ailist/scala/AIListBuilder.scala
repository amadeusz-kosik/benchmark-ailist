package benchmark.ailist.scala

import benchmark.{Configuration, Interval}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


object AIListBuilder {

  def buildSpeedOptimized(configuration: Configuration, sourceIntervals: Iterator[Interval]): Array[AIList] = {
    assert(configuration.intervalsCountToCheckLookahead >= configuration.intervalsCountToTriggerExtraction)
    assert(configuration.intervalsCountToCheckLookahead > 0)
    assert(configuration.maximumComponentSize > 0)

    var intervals = mutable.ArrayDeque.from(
      sourceIntervals.toArray.sorted(Ordering.by[Interval, (Long, Long)](i => (i.from, i.to)))
    )
    val results = ArrayBuffer[AIList]()

    while(intervals.nonEmpty) {
      val newComponent = mutable.ArrayBuilder.make[Interval]
      val leftovers = mutable.ArrayDeque[Interval]()

      while(intervals.nonEmpty && newComponent.length < configuration.maximumComponentSize) {
        val nextInterval = intervals.head
        intervals.remove(0)

        val includeInCurrentComponent = {
          val lookaheadCoverage = intervals.view
            .slice(0, configuration.intervalsCountToCheckLookahead)
            .count(_.to <= nextInterval.to)

          lookaheadCoverage <= configuration.intervalsCountToTriggerExtraction
        }

        if(includeInCurrentComponent)
          newComponent += nextInterval
        else
          leftovers += nextInterval
      }

      if (intervals.nonEmpty)
        leftovers.addAll(intervals)

      intervals = leftovers
      results += AIList(newComponent.result())
    }

    results.toArray
  }

  def buildMemoryOptimized(configuration: Configuration, sourceIntervals: Iterator[Interval]): Array[AIList] = {
    assert(configuration.intervalsCountToCheckLookahead >= configuration.intervalsCountToTriggerExtraction)
    assert(configuration.intervalsCountToCheckLookahead > 0)
    assert(configuration.maximumComponentSize > 0)

    val intervals = sourceIntervals.toArray
    intervals.sortInPlace()(Ordering.by[Interval, (Long, Long)](i => (i.from, i.to)))

    val componentStarts = ArrayBuffer[Integer]()
    val componentLengths = ArrayBuffer[Integer]()

    var componentLength = 0

    var arrayRead = 0
    var arrayWrite = 0

    def lastComponentEnd: Int = {
      if(componentLengths.isEmpty)
        0
      else
        componentStarts(componentLengths.length - 1) + componentLengths.last
    }

    // Go through all the intervals until all of them are stored in the components.
    while (lastComponentEnd < intervals.length) {
      // Start new component
      componentStarts += arrayRead
      componentLength = 0

      // If there are few enough components for the early stop - stop.
      if (intervals.length - lastComponentEnd <= configuration.earlyStopLeftoverCount) {
        // Commit component for all leftover intervals without looping again.
        componentLengths += intervals.length - lastComponentEnd
        componentLength = 0
      } else {
        // Store intervals extracted from currently constructed component.
        val sidecar = ArrayBuffer[Interval]()

        // Until the component has not yet reached maximum size and there are more intervals that might be included,
        //  loop through the input array of intervals.
        while (arrayRead < intervals.length) {
          if(componentLength >= configuration.maximumComponentSize) {
            // Jump to next interval, but do not empty sidecar.
            componentStarts += arrayWrite
            componentLengths += componentLength
            componentLength = 0
          }

          def nextInterval = intervals(arrayRead)

          // Compute the interval coverage - how many further intervals are fully shadowed by the current one.
          val includeInCurrentComponent = {
            val lookaheadCoverage = intervals.view
              .slice(arrayRead + 1, intervals.length)
              .take(configuration.intervalsCountToCheckLookahead)
              .count(_.to <= nextInterval.to)

            lookaheadCoverage <= configuration.intervalsCountToTriggerExtraction
          }

          if (includeInCurrentComponent) {
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

        // Commit the component.
        componentLengths += componentLength

        // Bring intervals from the sidecar back into the main array.
        if (sidecar.nonEmpty) {
          var backfill = arrayWrite

          sidecar.foreach { sidecarInterval =>
            intervals(backfill) = sidecarInterval
            backfill += 1
          }

          arrayRead = arrayWrite
        }
      }
    }

    componentStarts
      .zip(componentLengths).map { case (cStart, cLength) =>
        AIList.apply(intervals.view.slice(cStart, cStart + cLength).toArray)
      }
      .toArray
  }
}
