package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.CubeFactoryProducer
import com.millibyte1.cubesearch.cube.SmartCube
import com.millibyte1.cubesearch.cube.SmartCubeFactory
import com.millibyte1.cubesearch.cube.Twist
import com.millibyte1.cubesearch.util.CubeGenerator

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CornerPatternDatabaseTest {

    private val factory = SmartCubeFactory()

    private fun solved(): SmartCube {
        return factory.getSolvedCube()
    }

    @Test
    fun testSolvedCubeCost() {
        assertEquals(CornerPatternDatabase.getCost(solved()), 0)
    }
    @Test
    fun testSingleMoveCubeCosts() {
        for(twist in Twist.values()) {
            assertEquals(CornerPatternDatabase.getCost(solved().twist(twist)), 1)
        }
    }
    @Test
    fun testRandomCubeCostsAreAdmissible() {
        //generates 100 random cubes for each walk length and tests that the cost is admissible
        val generator = CubeGenerator<SmartCube>(factory)
        for(walkLength in 2..20) {
            generator.setWalkLength(walkLength)
            for(i in 0 until 100) {
                val cube = generator.nextCube()
                assertTrue(CornerPatternDatabase.getCost(cube) <= walkLength)
            }
        }
    }
    @Test
    fun testDatabaseSize() {
        assertEquals(CornerPatternDatabase.getPopulation(), CornerPatternDatabase.CARDINALITY)
    }
    /*
    @Test
    fun testOrientationDatabaseSize() {
        assertEquals(CornerPatternDatabase.getOrientationPopulation(), CornerPatternDatabase.ORIENTATION_CARDINALITY)
    }
    @Test
    fun testPositionDatabaseSize() {
        assertEquals(CornerPatternDatabase.getPositionPopulation(), CornerPatternDatabase.POSITION_CARDINALITY)
    }
    */

    @Test
    fun theoreticalAnalysis() {
        val costs = ByteArray(CornerPatternDatabase.CARDINALITY)
        val costCounts = IntArray(12) { 0 }
        val costProbabilities = DoubleArray(12) { 0.0 }
        var costSum = 0
        for(index in 0 until CornerPatternDatabase.CARDINALITY) {
            val cost = CornerPatternDatabase.getCost(index)
            costs[index] = cost
            costCounts[cost.toInt()]++
            costSum += cost
        }
        println("Performing theoretical analysis of CornerPatternDatabase off of the cost values for every possible configuration.")
        for(cost in 0..11) {
            costProbabilities[cost] = costCounts[cost] / CornerPatternDatabase.CARDINALITY.toDouble()
            println("# of configurations with cost $cost: " + costCounts[cost])
        }
        var expected = 0.0
        for(cost in 0..11) expected += (cost * costProbabilities[cost])
        println("Expected cost: $expected")
    }
    @Test
    fun monteCarloAnalysis() {
        val generator = CubeGenerator<SmartCube>(CubeFactoryProducer.getFactory("SmartCube"))
        val costs = ByteArray(1000000)
        val costCounts = IntArray(12) { 0 }
        val costProbabilities = DoubleArray(12) { 0.0 }
        var costSum = 0
        for(i in 0 until 1000000) {
            val cube = generator.nextCube()
            val index = CornerPatternDatabase.getIndex(cube)
            val cost = CornerPatternDatabase.getCost(index)
            costs[i] = cost
            costCounts[cost.toInt()]++
            costSum += cost
        }
        println("Performing analysis of CornerPatternDatabase via a Monte-Carlo simulation of ${costs.size} cubes.")
        for(cost in 0..11) {
            costProbabilities[cost] = costCounts[cost] / 1000000.toDouble()
            println("# of cubes with cost $cost: " + costCounts[cost])
        }
        var expected = 0.0
        for(cost in 0..11) expected += (cost * costProbabilities[cost])
        println("Expected cost: $expected")
    }
}