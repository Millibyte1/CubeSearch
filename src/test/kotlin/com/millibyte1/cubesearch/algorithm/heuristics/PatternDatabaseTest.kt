package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.CubeFactoryProducer
import com.millibyte1.cubesearch.cube.SmartCube
import com.millibyte1.cubesearch.cube.SmartCubeFactory
import com.millibyte1.cubesearch.cube.Twist
import com.millibyte1.cubesearch.util.CubeGenerator
import com.millibyte1.cubesearch.util.PatternDatabaseUtils
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import redis.clients.jedis.Jedis
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

data class PatternDBParams(
    val core: PatternDatabaseCore,
    val searchMode: String,
    val considered: MutableList<Int>,
    val isCorner: Boolean
)

@TestMethodOrder(
    MethodOrderer.OrderAnnotation::class)
class CornerPatternDatabaseTest {

    private val factory = SmartCubeFactory()

    private fun solved(): SmartCube {
        return factory.getSolvedCube()
    }

    @ParameterizedTest
    @MethodSource("params")
    @Order(1)
    fun generateDatabase(params: PatternDBParams) {
        makeDatabase(params)
    }

    @ParameterizedTest
    @MethodSource("params")
    @Order(2)
    fun testDatabaseSize(params: PatternDBParams) {
        val database = makeDatabase(params)
        assertEquals(database.getPopulation(), database.getCardinality())
    }

    @ParameterizedTest
    @MethodSource("params")
    @Order(3)
    fun testSolvedCubeCost(params: PatternDBParams) {
        val database = makeDatabase(params)
        assertEquals(database.getCost(solved()), 0)
    }

    @ParameterizedTest
    @MethodSource("params")
    @Order(4)
    fun testSingleMoveCubeCosts(params: PatternDBParams) {
        val database = makeDatabase(params)
        for(twist in Twist.values()) {
            assertTrue(database.getCost(solved().twist(twist)) <= 1)
        }
    }

    @ParameterizedTest
    @MethodSource("params")
    @Order(5)
    fun testRandomCubeCostsAreAdmissible(params: PatternDBParams) {
        val database = makeDatabase(params)
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
    @MethodSource("params")
    @Order(6)
    fun theoreticalAnalysis(params: PatternDBParams) {
        val database = makeDatabase(params)
        val costs = ByteArray(database.getCardinality())
        val costCounts = IntArray(12) { 0 }
        val costProbabilities = DoubleArray(12) { 0.0 }
        var costSum = 0
        for(index in 0 until database.getCardinality()) {
            val cost = database.getCost(index)
            costs[index] = cost
            costCounts[cost.toInt()]++
            costSum += cost
        }
        println("Performing theoretical analysis of this PatternDatabase off of the cost values for every possible configuration.")
        println("Cardinality: ${database.getCardinality()}")
        when(params.isCorner) {
            true -> println("Considered corners: ${(database as CornerPatternDatabase).getConsideredCorners().contentToString()}")
            false -> println("Considered edges: ${(database as EdgePatternDatabase).getConsideredEdges().contentToString()}")
        }
        for(cost in 0..11) {
            costProbabilities[cost] = costCounts[cost] / database.getCardinality().toDouble()
            println("# of configurations with cost $cost: " + costCounts[cost])
        }
        var expected = 0.0
        for(cost in 0..11) expected += (cost * costProbabilities[cost])
        println("Expected cost: $expected")
    }
    /*
    @ParameterizedTest
    @MethodSource("params")
    @Order(7)
    fun monteCarloAnalysis(params: PatternDBParams) {
    val database = makeDatabase(params)
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
        fun params(): MutableList<PatternDBParams> {
            /*
            return mutableListOf(
                //different sized corner databases generated via DFS
                PatternDBParams(FileCore("data/corners-0.db"), "dfs", mutableListOf(0), true),
                PatternDBParams(FileCore("data/corners-01.db"), "dfs", mutableListOf(0, 1), true),
                PatternDBParams(FileCore("data/corners-012.db"), "dfs", mutableListOf(0, 1, 2), true),
                PatternDBParams(FileCore("data/corners-0123.db"), "dfs", mutableListOf(0, 1, 2, 3), true),
                PatternDBParams(FileCore("data/corners-01234.db"), "dfs", mutableListOf(0, 1, 2, 3, 4), true),
                PatternDBParams(FileCore("data/corners-012345.db"), "dfs", mutableListOf(0, 1, 2, 3, 4, 5), true),
                PatternDBParams(FileCore("data/corners-full.db"), "dfs", mutableListOf(0, 1, 2, 3, 4, 5, 6, 7), true),
                //different sized edge databases generated via DFS
                PatternDBParams(FileCore("data/edges-0.db"), "dfs", mutableListOf(0), false),
                PatternDBParams(FileCore("data/edges-01.db"), "dfs", mutableListOf(0, 1), false),
                PatternDBParams(FileCore("data/edges-012.db"), "dfs", mutableListOf(0, 1, 2), false),
                PatternDBParams(FileCore("data/edges-0123.db"), "dfs", mutableListOf(0, 1, 2, 3), false),
                PatternDBParams(FileCore("data/edges-01234.db"), "dfs", mutableListOf(0, 1, 2, 3, 4), false),
                PatternDBParams(FileCore("data/edges-012345.db"), "dfs", mutableListOf(0, 1, 2, 3, 4, 5), false),
                PatternDBParams(FileCore("data/edges-full.db"), "dfs", mutableListOf(0, 1, 2, 3, 4, 5, 6), false)
                //different sized corner databases generated via BFS
                PatternDBParams(FileCore("data/corners-0-bfs.db"), "bfs", mutableListOf(0), true),
                PatternDBParams(FileCore("data/corners-01-bfs.db"), "bfs", mutableListOf(0, 1), true),
                PatternDBParams(FileCore("data/corners-012-bfs.db"), "bfs", mutableListOf(0, 1, 2), true),
                PatternDBParams(FileCore("data/corners-0123-bfs.db"), "bfs", mutableListOf(0, 1, 2, 3), true),
                PatternDBParams(FileCore("data/corners-01234-bfs.db"), "bfs", mutableListOf(0, 1, 2, 3, 4), true),
                PatternDBParams(FileCore("data/corners-012345-bfs.db"), "bfs", mutableListOf(0, 1, 2, 3, 4, 5), true),
                PatternDBParams(FileCore("data/corners-full-bfs.db"), "bfs", mutableListOf(0, 1, 2, 3, 4, 5, 6, 7), true),
                //different sized edge databases generated via BFS
                PatternDBParams(FileCore("data/edges-0-bfs.db"), "bfs", mutableListOf(0), false),
                PatternDBParams(FileCore("data/edges-01-bfs.db"), "bfs", mutableListOf(0, 1), false),
                PatternDBParams(FileCore("data/edges-012-bfs.db"), "bfs", mutableListOf(0, 1, 2), false),
                PatternDBParams(FileCore("data/edges-0123-bfs.db"), "bfs", mutableListOf(0, 1, 2, 3), false),
                PatternDBParams(FileCore("data/edges-01234-bfs.db"), "bfs", mutableListOf(0, 1, 2, 3, 4), false),
                PatternDBParams(FileCore("data/edges-012345-bfs.db"), "bfs", mutableListOf(0, 1, 2, 3, 4, 5), false),
                PatternDBParams(FileCore("data/edges-full-bfs.db"), "bfs", mutableListOf(0, 1, 2, 3, 4, 5, 6), false)
            )
            */
            return mutableListOf(
                PatternDBParams(FileCore("data/corners-01234.db"), "dfs", mutableListOf(0, 1, 2, 3, 4), true),
                PatternDBParams(FileCore("data/corners-01234-bfs.db"), "bfs", mutableListOf(0, 1, 2, 3, 4), true)
            )
        }
        @JvmStatic
        fun makeDatabase(params: PatternDBParams): AbstractPatternDatabase {
            return when(params.isCorner) {
                true -> CornerPatternDatabase.create(params.core, params.searchMode, params.considered)
                false -> EdgePatternDatabase.create(params.core, params.searchMode, params.considered)
            }
        }
    }
}