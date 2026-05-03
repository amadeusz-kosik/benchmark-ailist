package benchmark.ailist

import benchmark.{Configuration, Interval}
import benchmark.ailist.java
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CorrectnessTest extends AnyFunSpec with Matchers {

  /* Each correctness test asserts that list stores exactly the same elements as they
   *  were put into the list: no duplication, no data loss. */

  describe("Java AIList") {
    import _root_.java.util
    import _root_.scala.jdk.CollectionConverters._

    def computeActual(aiLists: util.List[java.AIList], query: Array[Interval]): Array[(Interval, Interval)] =
      for {
        aiList      <- aiLists.asScala.toArray
        rhsInterval <- query
        lhsInterval <- aiList.overlapping(rhsInterval).asScala.toArray
      } yield (lhsInterval, rhsInterval)


    it("should return empty list if no intervals overlap") {
      val configuration = Configuration.apply()
      val database = TestDataGenerator.consecutive(100)
      val query    = TestDataGenerator.consecutive(100, 200)

      val aiListBuilder = new java.AIListBuilder(configuration)
      val aiLists = aiListBuilder.build(new util.ArrayList(database.toBuffer.asJava))

      val expected = Array.empty[(Interval, Interval)]
      val actual = computeActual(aiLists, query)

      actual should contain theSameElementsAs expected
    }

    it("should correctly map 1:1 relation") {
      val configuration = Configuration.apply()
      val database = TestDataGenerator.consecutive(100)
      val query    = TestDataGenerator.consecutive(100)

      val aiListBuilder = new java.AIListBuilder(configuration)
      val aiLists = aiListBuilder.build(new util.ArrayList(database.toBuffer.asJava))

      val expected = TestDataGenerator.consecutive(100).map(i => (i, i))
      val actual = computeActual(aiLists, query)

      actual should contain theSameElementsAs expected
    }

    it("should correctly map 1:all relation") {
      val configuration = Configuration.apply()
      val database = TestDataGenerator.consecutive(1, 0, 100)
      val query    = TestDataGenerator.consecutive(100)

      val aiListBuilder = new java.AIListBuilder(configuration)
      val aiLists = aiListBuilder.build(new util.ArrayList(database.toBuffer.asJava))

      val expected = TestDataGenerator.consecutive(100).map(i => (Interval(0, 100), i))
      val actual = computeActual(aiLists, query)

      actual should contain theSameElementsAs expected
    }

    it("should correctly map all:1 relation") {
      val configuration = Configuration.apply()
      val database = TestDataGenerator.consecutive(100)
      val query    = TestDataGenerator.consecutive(1, 0, 100)

      val aiListBuilder = new java.AIListBuilder(configuration)
      val aiLists = aiListBuilder.build(new util.ArrayList(database.toBuffer.asJava))

      val expected = TestDataGenerator.consecutive(100).map(i => (i, Interval(0, 100)))
      val actual = computeActual(aiLists, query)

      actual should contain theSameElementsAs expected
    }
  }

  describe("Scala AIList") {

    def computeActual(aiLists: Array[scala.AIList], query: Array[Interval]): Array[(Interval, Interval)] =
      for {
        aiList      <- aiLists
        rhsInterval <- query
        lhsInterval <- aiList.overlapping(rhsInterval)
      } yield (lhsInterval, rhsInterval)


    it("should return empty list if no intervals overlap") {
      val configuration = Configuration()
      val database = TestDataGenerator.consecutive(100)
      val query    = TestDataGenerator.consecutive(100, 200)

      val aiLists = scala.AIListBuilder.build(configuration, database)

      val expected = Array.empty[(Interval, Interval)]
      val actual = computeActual(aiLists, query)

      actual should contain theSameElementsAs expected
    }

    it("should correctly map 1:1 relation") {
      val configuration = Configuration()
      val database = TestDataGenerator.consecutive(100)
      val query    = TestDataGenerator.consecutive(100)

      val aiLists = scala.AIListBuilder.build(configuration, database)

      val expected = TestDataGenerator.consecutive(100).map(i => (i, i))
      val actual = computeActual(aiLists, query)

      actual should contain theSameElementsAs expected
    }

    it("should correctly map 1:all relation") {
      val configuration = Configuration.apply()
      val database = TestDataGenerator.consecutive(1, 0, 100)
      val query    = TestDataGenerator.consecutive(100)

      val aiLists = scala.AIListBuilder.build(configuration, database)

      val expected = TestDataGenerator.consecutive(100).map(i => (Interval(0, 100), i))
      val actual = computeActual(aiLists, query)

      actual should contain theSameElementsAs expected
    }

    it("should correctly map all:1 relation") {
      val configuration = Configuration.apply()
      val database = TestDataGenerator.consecutive(100)
      val query    = TestDataGenerator.consecutive(1, 0, 100)

      val aiLists = scala.AIListBuilder.build(configuration, database)

      val expected = TestDataGenerator.consecutive(100).map(i => (i, Interval(0, 100)))
      val actual = computeActual(aiLists, query)

      actual should contain theSameElementsAs expected
    }
  }
}