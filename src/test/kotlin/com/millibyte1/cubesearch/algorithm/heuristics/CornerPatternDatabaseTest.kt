package com.millibyte1.cubesearch.algorithm.heuristics

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

    @Test
    fun notActuallyATest() {
        /*
        val orientationCosts = ByteArray(2187)
        val positionCosts = ByteArray(40320)
        var orientationCostSum: Int = 0
        var positionCostSum: Int = 0
        var orientationCostMin: Byte = 100
        var positionCostMin: Byte = 100
        var orientationCostMax: Byte = 0
        var positionCostMax: Byte = 0
        for(index in 0 until 2187) {
            val cost = CornerPatternDatabase.getOrientationCost(index)
            if(cost < orientationCostMin) orientationCostMin = cost
            if(cost > orientationCostMax) orientationCostMax = cost
            orientationCosts[index] = cost
            orientationCostSum += cost
        }
        for(index in 0 until 40320) {
            val cost = CornerPatternDatabase.getPositionCost(index)
            if(cost < positionCostMin) positionCostMin = cost
            if(cost > positionCostMax) positionCostMax = cost
            positionCosts[index] = cost
            positionCostSum += cost
        }
        println("Min orientation cost: $orientationCostMin")
        println("Max orientation cost: $orientationCostMax")
        println("Average orientation cost: " + (orientationCostSum / 2187.0))
        println("Min position cost: $positionCostMin")
        println("Max position cost: $positionCostMax")
        println("Average position cost: " + (positionCostSum / 40320.0))
        */
    }
    */
}