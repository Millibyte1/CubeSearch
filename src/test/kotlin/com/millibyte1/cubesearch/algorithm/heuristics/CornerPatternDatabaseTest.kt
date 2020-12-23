package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.Cube
import com.millibyte1.cubesearch.cube.CubeFactory
import com.millibyte1.cubesearch.cube.Twist
import com.millibyte1.cubesearch.util.CubeGenerator

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CornerPatternDatabaseTest {

    private val factory = CubeFactory()

    private fun solved(): Cube {
        return factory.getSolvedCube()
    }

    @Test
    fun testSolvedCubeCost() {
        assertEquals(CornerPatternDatabase.getCost(solved()), 0)
    }
    @Test
    fun testSingleMoveCubeCosts() {
        for(twist in Twist.values()) assertEquals(CornerPatternDatabase.getCost(solved().twist(twist)), 0)
    }
    @Test
    fun testRandomCubeCosts() {
        //generates 100 random cubes for each walk length and tests that the cost is possible
        val generator = CubeGenerator(factory)
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
}