package benchmark.ailist

import benchmark.{Configuration, Interval}
import benchmark.ailist.java
import benchmark.ailist.java.AIListBuilder
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import _root_.scala.collection.Map


class CorrectnessTest extends AnyFunSpec with Matchers {

  // Each correctness test asserts that list stores exactly the same elements as they
  //  were put into the list: no duplication, no data loss.

  private val testScenarios = Map(
    "should return empty list if the database is empty" -> (
      Array.empty[Interval],
      TestDataGenerator.consecutive(100, 200),
      Array.empty[(Interval, Interval)]
    ),

    "should return empty list if the query is empty" -> (
      TestDataGenerator.consecutive(100),
      Array.empty[Interval],
      Array.empty[(Interval, Interval)]
    ),

    "should return empty list if no intervals overlap" -> (
      TestDataGenerator.consecutive(100),
      TestDataGenerator.consecutive(100, 200),
      Array.empty[(Interval, Interval)]
    ),

    "should correctly map 1:1 relation" -> (
      TestDataGenerator.consecutive(100),
      TestDataGenerator.consecutive(100),
      TestDataGenerator.consecutive(100).map(i => (i, i))
    ),

    "should correctly map 1:all relation" -> (
      TestDataGenerator.consecutive(1, 0, 100),
      TestDataGenerator.consecutive(100),
      TestDataGenerator.consecutive(100).map(i => (Interval(0, 100), i))
    ),

    "should correctly map all:1 relation" -> (
      TestDataGenerator.consecutive(100),
      TestDataGenerator.consecutive(1, 0, 100),
      TestDataGenerator.consecutive(100).map(i => (i, Interval(0, 100)))
    ),

    "should preserve duplicates" -> (
      TestDataGenerator.consecutive(100) appendedAll TestDataGenerator.consecutive(100),
      TestDataGenerator.consecutive(100),
      TestDataGenerator.consecutive(100).flatMap(i => Array((i, i), (i, i)))
    )
  )

  Map(
    "built with array list"     -> new AIListBuilder(Configuration.apply()).buildArrayFromIterator _,
    "built with priority queue" -> new AIListBuilder(Configuration.apply()).buildQueueFromIterator _
  ) foreach { case (name, buildFn) =>
    describe(s"Java AIList $name") {
      import _root_.java.util
      import _root_.scala.jdk.CollectionConverters._

      def computeActual(aiLists: util.List[java.AIList], query: Array[Interval]): Array[(Interval, Interval)] =
        for {
          aiList      <- aiLists.asScala.toArray
          rhsInterval <- query
          lhsInterval <- aiList.overlapping(rhsInterval).asScala.toArray
        } yield (lhsInterval, rhsInterval)

      testScenarios foreach { case (title, (database, query, expected)) =>
        it(title) {
          val aiLists = buildFn(database.iterator)
          val actual  = computeActual(aiLists, query)

          actual should contain theSameElementsAs expected
        }
      }
    }
  }

  Map(
    "built with array in place" -> scala.AIListBuilder.buildArrayInPlace _,
    "built with deque"          -> scala.AIListBuilder.buildDeque  _
  ) foreach { case(name, buildFn) =>
    describe(s"Scala AIList $name") {
      def computeActual(aiLists: Array[scala.AIList], query: Array[Interval]): Array[(Interval, Interval)] =
        for {
          aiList      <- aiLists
          rhsInterval <- query
          lhsInterval <- aiList.overlapping(rhsInterval)
        } yield (lhsInterval, rhsInterval)

      testScenarios foreach { case (title, (database, query, expected)) =>
        it(title) {
          val aiLists = buildFn(Configuration(), database.iterator)
          val actual  = computeActual(aiLists, query)

          actual should contain theSameElementsAs expected
        }
      }
    }
  }

}