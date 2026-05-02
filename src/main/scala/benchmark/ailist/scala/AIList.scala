package benchmark.ailist.scala


case class AIList(intervals: Array[Interval], maxE: Array[Long]) {

    def length: Int =
        intervals.length

    def overlapping(query: Interval): Array[Interval] = {
        intervals.iterator
            .zip(maxE.iterator)
            .dropWhile { case (interval, maxE) => maxE < query.from }
            .takeWhile { case (interval, maxE) => interval.from <= query.to }
            .map { case (interval, maxE) => interval }
            .filter(interval => Interval.overlaps(interval, query))
            .toArray
    }
}

object AIList {
    def apply(intervals: Array[Interval]): AIList = {
        val maxE = {
            if(! intervals.isEmpty)
                intervals.scanLeft(Long.MinValue)(_ max _.to).drop(1)
            else
                Array.empty[Long]
        }

        AIList(intervals, maxE)
    }
}