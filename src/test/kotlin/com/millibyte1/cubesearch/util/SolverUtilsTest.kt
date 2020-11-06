package com.millibyte1.cubesearch.util

import com.millibyte1.cubesearch.util.Cubie

import com.millibyte1.cubesearch.cube.CubeFactory
import com.millibyte1.cubesearch.cube.Cube
import com.millibyte1.cubesearch.cube.Twist
import com.millibyte1.cubesearch.cube.Twist.Face

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Assertions.*
import kotlin.test.assertFailsWith

class SolverUtilsTest {

    /* ========================================== TEST FIXTURES ===================================================== */

    private val factory = CubeFactory()
    private val generator = CubeGenerator<Cube>(factory)

    private fun solved(): Cube {
        return factory.getSolvedCube()
    }
    private fun switchedCenters(): Cube {
        val cube = solved()
        cube.data[0][4] = 1
        cube.data[1][4] = 0
        return cube
    }
    private fun duplicateCenters(): Cube {
        val cube = solved()
        cube.data[0][4] = 1
        return cube
    }
    private fun oneCornerTwisted(): Cube {
        val cube = solved()
        cube.data[0][0] = 4
        cube.data[2][2] = 0
        cube.data[4][6] = 2
        return cube
    }
    private fun oneEdgeTwisted(): Cube {
        val cube = solved()
        cube.data[0][1] = 1
        cube.data[4][7] = 0
        return cube
    }
    private fun superFlip(): Cube {
        val cube = solved()
        //front-up
        cube.data[0][1] = 4
        cube.data[4][7] = 0
        //front-left
        cube.data[0][3] = 2
        cube.data[2][5] = 0
        //front-right
        cube.data[0][5] = 3
        cube.data[3][3] = 0
        //front-down
        cube.data[0][7] = 5
        cube.data[5][1] = 0
        //back-up
        cube.data[1][1] = 4
        cube.data[4][1] = 1
        //back-left
        cube.data[1][5] = 2
        cube.data[2][3] = 1
        //back-right
        cube.data[1][3] = 3
        cube.data[3][5] = 1
        //back-down
        cube.data[1][7] = 5
        cube.data[5][7] = 1
        //up-left
        cube.data[4][3] = 2
        cube.data[2][1] = 4
        //up-right
        cube.data[4][5] = 3
        cube.data[3][1] = 4
        //down-left
        cube.data[5][3] = 2
        cube.data[2][7] = 5
        //down-right
        cube.data[5][5] = 3
        cube.data[3][7] = 5
        return cube
    }
    private fun randomValidCube(): Cube {
        return generator.nextCube()
    }


/* ========================================= SOLVABILITY FUNCTION TESTS ============================================= */

    @Test
    @Tag("CubeSimulationTest")
    fun testIsSolvable() {
        assertTrue(isSolvable(solved()))
        assertTrue(isSolvable(superFlip()))
        for(i in 0 until 100) assertTrue(isSolvable(randomValidCube()))
        assertFalse(isSolvable(switchedCenters()))
        assertFalse(isSolvable(duplicateCenters()))
        assertFalse(isSolvable(oneCornerTwisted()))
        assertFalse(isSolvable(oneEdgeTwisted()))
    }
    @Test
    @Tag("CubeSimulationTest")
    fun testIsCorrectlyStickered() {
        assertTrue(isCorrectlyStickered(solved()))
        assertTrue(isSolvable(superFlip()))
        for(i in 0 until 100) assertTrue(isCorrectlyStickered(randomValidCube()))
        assertFalse(isCorrectlyStickered(switchedCenters()))
        assertFalse(isCorrectlyStickered(duplicateCenters()))
        assertTrue(isCorrectlyStickered(oneCornerTwisted()))
        assertTrue(isCorrectlyStickered(oneEdgeTwisted()))
    }
    @Test
    @Tag("CubeSimulationTest")
    fun testParityTests() {
        assertTrue(passesParityTests(solved()))
        assertTrue(passesParityTests(superFlip()))
        for(i in 0 until 100) assertTrue(passesParityTests(randomValidCube()))
        assertTrue(passesParityTests(switchedCenters()))
        assertTrue(passesParityTests(duplicateCenters()))
        assertFalse(passesParityTests(oneCornerTwisted()))
        assertFalse(passesParityTests(oneEdgeTwisted()))
    }
    @Test
    @Tag("CubeSimulationTest")
    fun testPermutationParityTest() {
        assertTrue(passesPermutationParityTest(solved()))
        assertTrue(passesPermutationParityTest(superFlip()))
        for(i in 0 until 100) assertTrue(passesPermutationParityTest(randomValidCube()))
        assertTrue(passesPermutationParityTest(switchedCenters()))
        assertTrue(passesPermutationParityTest(duplicateCenters()))
        assertFalse(passesPermutationParityTest(oneCornerTwisted()))
        assertFalse(passesPermutationParityTest(oneEdgeTwisted()))
    }
    @Test
    @Tag("CubeSimulationTest")
    fun testEdgeParityTest() {
        assertTrue(passesEdgeParityTest(solved()))
        assertTrue(passesEdgeParityTest(superFlip()))
        for(i in 0 until 100) assertTrue(passesEdgeParityTest(randomValidCube()))
        assertTrue(passesEdgeParityTest(switchedCenters()))
        assertTrue(passesEdgeParityTest(duplicateCenters()))
        assertTrue(passesEdgeParityTest(oneCornerTwisted()))
        assertFalse(passesEdgeParityTest(oneEdgeTwisted()))
    }
    @Test
    @Tag("CubeSimulationTest")
    fun testCornerParityTest() {
        assertTrue(passesCornerParityTest(solved()))
        assertTrue(passesCornerParityTest(superFlip()))
        for(i in 0 until 100) assertTrue(passesCornerParityTest(randomValidCube()))
        assertTrue(passesCornerParityTest(switchedCenters()))
        assertTrue(passesCornerParityTest(duplicateCenters()))
        assertFalse(passesCornerParityTest(oneCornerTwisted()))
        assertTrue(passesCornerParityTest(oneEdgeTwisted()))
    }

}