package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.CubeFactoryProducer
import com.millibyte1.cubesearch.cube.SmartCube
import com.millibyte1.cubesearch.cube.SmartCubeFactory
import com.millibyte1.cubesearch.cube.Twist
import com.millibyte1.cubesearch.util.CubeGenerator
import com.millibyte1.cubesearch.util.PatternDatabaseUtils
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import redis.clients.jedis.Jedis
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CornerPatternDatabaseTest {

    private val factory = SmartCubeFactory()
    //internal val database: CornerPatternDatabase

    init {
        val generalConfig: Config = ConfigFactory.load("patterndb.conf").getConfig("patterndb")
        val cornerConfig = generalConfig.getConfig("corners-full")

        val searchMode = cornerConfig.getString("search-mode")
        val persistenceMode = generalConfig.getString("persistence-mode")

        val jedis = Jedis()
        val key = cornerConfig.getString("redis-key")

        val file = File("data/corners-full-backup.db")

        val core = when(persistenceMode) {
            "file" -> FileCore(file)
            else -> RedisCore(jedis, key)
        }
        //database = CornerPatternDatabase.create(core, searchMode, mutableListOf(0, 1, 2, 3, 4, 5, 6, 7))
    }

    private fun solved(): SmartCube {
        return factory.getSolvedCube()
    }
    @Test
    fun testLehmerCode() {
        val testCase = listOf(1, 3)
        val lehmer = PatternDatabaseUtils.getLehmerCode(testCase, 4)
        assertTrue(lehmer.contentEquals(intArrayOf(1, 2)))
    }

    @ParameterizedTest
    @MethodSource("patternDatabases")
    fun testSolvedCubeCost(database: CornerPatternDatabase) {
        assertEquals(database.getCost(solved()), 0)
    }
    @ParameterizedTest
    @MethodSource("patternDatabases")
    fun testSingleMoveCubeCosts(database: CornerPatternDatabase) {
        for(twist in Twist.values()) {
            assertTrue(database.getCost(solved().twist(twist)) <= 1)
        }
    }
    @ParameterizedTest
    @MethodSource("patternDatabases")
    fun testRandomCubeCostsAreAdmissible(database: CornerPatternDatabase) {
        //generates 100 random cubes for each walk length and tests that the cost is admissible
        val generator = CubeGenerator<SmartCube>(factory)
        for(walkLength in 2..20) {
            generator.setWalkLength(walkLength)
            for(i in 0 until 100) {
                val cube = generator.nextCube()
                assertTrue(database.getCost(cube) <= walkLength)
            }
        }
    }
    @ParameterizedTest
    @MethodSource("patternDatabases")
    fun testDatabaseSize(database: CornerPatternDatabase) {
        assertEquals(database.getPopulation(), database.cardinality)
    }

    @ParameterizedTest
    @MethodSource("patternDatabases")
    fun theoreticalAnalysis(database: CornerPatternDatabase) {
        val costs = ByteArray(database.cardinality)
        val costCounts = IntArray(12) { 0 }
        val costProbabilities = DoubleArray(12) { 0.0 }
        var costSum = 0
        for(index in 0 until database.cardinality) {
            val cost = database.getCost(index)
            costs[index] = cost
            costCounts[cost.toInt()]++
            costSum += cost
        }
        println("Performing theoretical analysis of CornerPatternDatabase off of the cost values for every possible configuration.")
        println("Cardinality: ${database.cardinality}")
        println("Considered corners: ${database.getConsideredCorners().contentToString()}")
        for(cost in 0..11) {
            costProbabilities[cost] = costCounts[cost] / database.cardinality.toDouble()
            println("# of configurations with cost $cost: " + costCounts[cost])
        }
        var expected = 0.0
        for(cost in 0..11) expected += (cost * costProbabilities[cost])
        println("Expected cost: $expected")
    }
    /*
    @ParameterizedTest
    @MethodSource("patternDatabases")
    fun monteCarloAnalysis(database: CornerPatternDatabase) {
        val generator = CubeGenerator<SmartCube>(CubeFactoryProducer.getFactory("SmartCube"))
        val costs = ByteArray(1000000)
        val costCounts = IntArray(12) { 0 }
        val costProbabilities = DoubleArray(12) { 0.0 }
        var costSum = 0
        for(i in 0 until 1000000) {
            val cube = generator.nextCube()
            val index = database.getIndex(cube)
            val cost = database.getCost(index)
            costs[i] = cost
            costCounts[cost.toInt()]++
            costSum += cost
        }
        println("Performing analysis of CornerPatternDatabase via a Monte-Carlo simulation of ${costs.size} cubes.")
        println("Considered corners: ${database.getConsideredCorners().contentToString()}")
        for(cost in 0..11) {
            costProbabilities[cost] = costCounts[cost] / 1000000.toDouble()
            println("# of cubes with cost $cost: " + costCounts[cost])
        }
        var expected = 0.0
        for(cost in 0..11) expected += (cost * costProbabilities[cost])
        println("Average cost: $expected")
    }*/

    companion object {
        @JvmStatic
        fun patternDatabases(): List<CornerPatternDatabase> {
            //return listOf(CornerPatternDatabase.create(FileCore("data/corners-full.db"), "dfs", mutableListOf(0, 1, 2, 3, 4, 5, 6, 7)))
            //return listOf(CornerPatternDatabase.create(FileCore("data/corners-012.db"), "dfs", mutableListOf(0, 1, 2)))
            return listOf(
                CornerPatternDatabase.create(FileCore("data/corners-0.db"), "dfs", mutableListOf(0)),
                CornerPatternDatabase.create(FileCore("data/corners-01.db"), "dfs", mutableListOf(0, 1)),
                CornerPatternDatabase.create(FileCore("data/corners-012.db"), "dfs", mutableListOf(0, 1, 2)),
                CornerPatternDatabase.create(FileCore("data/corners-0123.db"), "dfs", mutableListOf(0, 1, 2, 3)),
                CornerPatternDatabase.create(FileCore("data/corners-01234.db"), "dfs", mutableListOf(0, 1, 2, 3, 4)),
                CornerPatternDatabase.create(FileCore("data/corners-012345.db"), "dfs", mutableListOf(0, 1, 2, 3, 4, 5)),
                CornerPatternDatabase.create(FileCore("data/corners-full.db"), "dfs", mutableListOf(0, 1, 2, 3, 4, 5, 6, 7))
            )
        }
    }
}