package benchmark.ailist

import java.AIListBuilder
import _root_.java.util
import benchmark.{Configuration, Interval}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers


class ListSplittingTest extends AnyFunSpec with Matchers {

  final private val basicData = Array(
    // Interval       // Coverage on initial loop (1st component)
    Interval( 1, 11), //  0
    Interval( 1, 12), //  1
    Interval( 2, 13), //  2
    Interval( 2, 14), //  3
    Interval( 3, 15), //  4
    Interval( 4, 12), //  2
    Interval( 4, 64), //  5
    Interval( 5, 10), //  0
    Interval( 6, 12), //  0
    Interval( 6, 14), //  0
    Interval( 7, 18), //  0
    Interval( 8, 62), //  2
    Interval( 8, 64), //  5
    Interval( 9, 63), //  5
    Interval( 9, 61), //  5
    Interval(10, 33), //  4
    Interval(10, 60), //  4
    Interval(11, 32), //  1
    Interval(12, 33), //  2
    Interval(12, 32), //  0
    Interval(15, 33)  //  0
  )

  final private val outlierData = Array(
    // Outliers
    Interval(0,  64),
    Interval(0,  96),
    Interval(0, 128),
    // Flat list
    Interval(1, 2), 
    Interval(2, 3), 
    Interval(3, 4), 
    Interval(4, 5), 
    Interval(5, 6), 
    Interval(6, 7), 
    Interval(7, 8), 
    Interval(8, 9), 
    Interval(9, 10)
  )

  describe("Java AIList") {
    import _root_.scala.jdk.CollectionConverters._

    it("splits example list to separate long, overlapping intervals from short ones") {
      val configuration = new Configuration(5, 2, 64, 0)
      val aiListBuilder = new AIListBuilder(configuration)
      val aiLists = aiListBuilder.buildFromIterator(basicData.iterator)

      aiLists.size should be(3)
      aiLists.get(0).size() should be (12)
      aiLists.get(1).size() should be (6)
      aiLists.get(2).size() should be (3)
    }

    it("does not split if the coverage lookahead is turned off") {
      val configuration = new Configuration(5, 5, 64, 0)
      val aiListBuilder = new AIListBuilder(configuration)
      val aiLists = aiListBuilder.buildFromIterator(basicData.iterator)

      aiLists.size should be(1)
      aiLists.get(0).size() should be (21)
    }

    it("splits to honour maximum component length") {
      val configuration = new Configuration(5, 5, 5, 0)
      val aiListBuilder = new AIListBuilder(configuration)
      val aiLists = aiListBuilder.buildFromIterator(basicData.iterator)

      aiLists.size should be(5)
      aiLists.get(0).size() should be (5)
      aiLists.get(1).size() should be (5)
      aiLists.get(2).size() should be (5)
      aiLists.get(3).size() should be (5)
      aiLists.get(4).size() should be (1)
    }

    it("extracts the outliers from the flat group and put them at the end") {
      val configuration = new Configuration(5, 2, 64, 0)
      val aiListBuilder = new AIListBuilder(configuration)
      val aiLists = aiListBuilder.buildFromIterator(outlierData.iterator)

      aiLists.size should be(2)
      aiLists.get(0).size() should be (9)
      aiLists.get(1).size() should be (3)
    }
  }

  describe("Scala AIList") {
    it("splits example list to separate long, overlapping intervals from short ones") {
      val configuration = new Configuration(5, 2, 64, 0)
      val aiLists = scala.AIListBuilder.buildMemoryOptimized(configuration, basicData.iterator)

      aiLists.length should be(3)
      aiLists.map(_.length) should be (Array(12, 6, 3))
    }

    it("does not split if the coverage lookahead is turned off") {
      val configuration = new Configuration(5, 5, 64, 0)
      val aiLists = scala.AIListBuilder.buildMemoryOptimized(configuration, basicData.iterator)

      aiLists.length should be(1)
      aiLists.map(_.length) should be (Array(21))
    }

    it("splits to honour maximum component length") {
      val configuration = new Configuration(5, 5, 5, 0)
      val aiLists = scala.AIListBuilder.buildMemoryOptimized(configuration, basicData.iterator)

      aiLists.length should be(5)
      aiLists.map(_.length) should be (Array(5, 5, 5, 5, 1))
    }

    it("extracts the outliers from the flat group and put them at the end") {
      val configuration = new Configuration(5, 2, 64, 0)
      val aiLists = scala.AIListBuilder.buildMemoryOptimized(configuration, outlierData.iterator)

      aiLists.length should be(2)
      aiLists.map(_.length) should be (Array(9, 3))
    }
  }

}
