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
    private fun randomValidCube(): Cube {
        return generator.nextCube()
    }
    /** Should fail on stickering only */
    private fun switchedCenters(): Cube {
        val cube = solved()
        cube.data[0][4] = 1
        cube.data[1][4] = 0
        return cube
    }
    /** Should fail on stickering only */
    private fun duplicateCenters(): Cube {
        val cube = solved()
        cube.data[0][4] = 1
        return cube
    }
    /** Should fail on corner parity only */
    private fun oneCornerTwisted(): Cube {
        val cube = solved()
        cube.data[0][0] = 4
        cube.data[2][2] = 0
        cube.data[4][6] = 2
        return cube
    }
    /** Should fail on edge parity only */
    private fun oneEdgeTwisted(): Cube {
        val cube = solved()
        cube.data[0][1] = 4
        cube.data[4][7] = 0
        return cube
    }
    /** Should fail on permutation parity only */
    private fun twoEdgesSwapped(): Cube {
        val cube = solved()
        val copy = factory.getCube(cube)
        //front-up edge
        copy.data[0][1] = cube.data[0][5]
        copy.data[4][7] = cube.data[3][3]
        //front-right edge
        copy.data[0][5] = cube.data[0][1]
        copy.data[3][3] = cube.data[4][7]
        return copy
    }
    /** Should pass everything */
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
    /** Should pass everything */
    private fun threeEdgesSwapped(): Cube {
        val cube = solved()
        val copy = factory.getCube(cube)
        //front-up edge
        copy.data[0][1] = cube.data[0][5]
        copy.data[4][7] = cube.data[3][3]
        //front-right edge
        copy.data[0][5] = cube.data[0][7]
        copy.data[3][3] = cube.data[5][1]
        //front-down edge
        copy.data[0][7] = cube.data[0][1]
        copy.data[5][1] = cube.data[4][7]
        return copy
    }
    /** Should pass everything */
    private fun threeCornersSwapped(): Cube {
        val cube = solved()
        val copy = factory.getCube(cube)
        //front-left-up corner
        copy.data[0][0] = cube.data[0][2]
        copy.data[2][2] = cube.data[4][8]
        copy.data[4][6] = cube.data[3][0]
        //front-right-up corner
        copy.data[0][2] = cube.data[0][6]
        copy.data[4][8] = cube.data[5][0]
        copy.data[3][0] = cube.data[2][8]
        //front-left-down corner
        copy.data[0][6] = cube.data[0][0]
        copy.data[5][0] = cube.data[2][2]
        copy.data[2][8] = cube.data[4][6]
        return copy
    }
    /** Should pass everything */
    private fun threeEdgesAndCornersSwapped(): Cube {
        val cube = solved()
        val copy = factory.getCube(cube)
        //front-up edge
        copy.data[0][1] = cube.data[0][5]
        copy.data[4][7] = cube.data[3][3]
        //front-right edge
        copy.data[0][5] = cube.data[0][7]
        copy.data[3][3] = cube.data[5][1]
        //front-down edge
        copy.data[0][7] = cube.data[0][1]
        copy.data[5][1] = cube.data[4][7]
        //front-left-up corner
        copy.data[0][0] = cube.data[0][2]
        copy.data[2][2] = cube.data[4][8]
        copy.data[4][6] = cube.data[3][0]
        //front-right-up corner
        copy.data[0][2] = cube.data[0][6]
        copy.data[4][8] = cube.data[5][0]
        copy.data[3][0] = cube.data[2][8]
        //front-left-down corner
        copy.data[0][6] = cube.data[0][0]
        copy.data[5][0] = cube.data[2][2]
        copy.data[2][8] = cube.data[4][6]
        return copy
    }
    /** Should pass everything */
    private fun extraEdgeParityTestCase(): Cube {
        val data = arrayOf(
                intArrayOf(2, 2, 2,
                           0, 0, 5,
                           0, 0, 5),
                intArrayOf(3, 3, 3,
                           4, 1, 1,
                           4, 1, 1),
                intArrayOf(4, 1, 1,
                           2, 2, 2,
                           2, 2, 2),
                intArrayOf(0, 0, 5,
                           3, 3, 3,
                           3, 3, 3),
                intArrayOf(0, 0, 0,
                           4, 4, 4,
                           4, 4, 4),
                intArrayOf(5, 5, 1,
                           5, 5, 1,
                           5, 5, 1)
        )
        return factory.getCube(data)
    }


/* ========================================= SOLVABILITY FUNCTION TESTS ============================================= */

    @Test
    @Tag("CubeSimulationTest")
    fun testIsSolvable() {
        //tests solved cube and then all twists
        assertTrue(isSolvable(solved()))
        assertTrue(isSolvable(solved().twist(Twist.FRONT_90)))
        assertTrue(isSolvable(solved().twist(Twist.FRONT_180)))
        assertTrue(isSolvable(solved().twist(Twist.FRONT_270)))
        assertTrue(isSolvable(solved().twist(Twist.BACK_90)))
        assertTrue(isSolvable(solved().twist(Twist.BACK_180)))
        assertTrue(isSolvable(solved().twist(Twist.BACK_270)))
        assertTrue(isSolvable(solved().twist(Twist.LEFT_90)))
        assertTrue(isSolvable(solved().twist(Twist.LEFT_180)))
        assertTrue(isSolvable(solved().twist(Twist.LEFT_270)))
        assertTrue(isSolvable(solved().twist(Twist.RIGHT_90)))
        assertTrue(isSolvable(solved().twist(Twist.RIGHT_180)))
        assertTrue(isSolvable(solved().twist(Twist.RIGHT_270)))
        assertTrue(isSolvable(solved().twist(Twist.UP_90)))
        assertTrue(isSolvable(solved().twist(Twist.UP_180)))
        assertTrue(isSolvable(solved().twist(Twist.UP_270)))
        //tests special cases that should pass for everything
        assertTrue(isSolvable(superFlip()))
        assertTrue(isSolvable(threeEdgesSwapped()))
        assertTrue(isSolvable(threeCornersSwapped()))
        assertTrue(isSolvable(threeEdgesAndCornersSwapped()))
        //tests special cases that should fail for some things
        assertFalse(isSolvable(switchedCenters()))
        assertFalse(isSolvable(duplicateCenters()))
        assertFalse(isSolvable(oneCornerTwisted()))
        assertFalse(isSolvable(oneEdgeTwisted()))
        assertFalse(isSolvable(twoEdgesSwapped()))
        assertTrue(isSolvable(extraEdgeParityTestCase()))
        //tests random cubes
        for(i in 0 until 100) assertTrue(isSolvable(randomValidCube()))
    }
    @Test
    @Tag("CubeSimulationTest")
    fun testIsCorrectlyStickered() {
        //tests solved cube and then all twists
        assertTrue(isCorrectlyStickered(solved()))
        assertTrue(isCorrectlyStickered(solved().twist(Twist.FRONT_90)))
        assertTrue(isCorrectlyStickered(solved().twist(Twist.FRONT_180)))
        assertTrue(isCorrectlyStickered(solved().twist(Twist.FRONT_270)))
        assertTrue(isCorrectlyStickered(solved().twist(Twist.BACK_90)))
        assertTrue(isCorrectlyStickered(solved().twist(Twist.BACK_180)))
        assertTrue(isCorrectlyStickered(solved().twist(Twist.BACK_270)))
        assertTrue(isCorrectlyStickered(solved().twist(Twist.LEFT_90)))
        assertTrue(isCorrectlyStickered(solved().twist(Twist.LEFT_180)))
        assertTrue(isCorrectlyStickered(solved().twist(Twist.LEFT_270)))
        assertTrue(isCorrectlyStickered(solved().twist(Twist.RIGHT_90)))
        assertTrue(isCorrectlyStickered(solved().twist(Twist.RIGHT_180)))
        assertTrue(isCorrectlyStickered(solved().twist(Twist.RIGHT_270)))
        assertTrue(isCorrectlyStickered(solved().twist(Twist.UP_90)))
        assertTrue(isCorrectlyStickered(solved().twist(Twist.UP_180)))
        assertTrue(isCorrectlyStickered(solved().twist(Twist.UP_270)))
        //tests special cases that should pass for everything
        assertTrue(isCorrectlyStickered(superFlip()))
        assertTrue(isCorrectlyStickered(threeEdgesSwapped()))
        assertTrue(isCorrectlyStickered(threeCornersSwapped()))
        assertTrue(isCorrectlyStickered(threeEdgesAndCornersSwapped()))
        //tests special cases that should fail for some things
        assertFalse(isCorrectlyStickered(switchedCenters()))
        assertFalse(isCorrectlyStickered(duplicateCenters()))
        assertTrue(isCorrectlyStickered(oneCornerTwisted()))
        assertTrue(isCorrectlyStickered(oneEdgeTwisted()))
        assertTrue(isCorrectlyStickered(twoEdgesSwapped()))
        //tests random cubes
        for(i in 0 until 100) assertTrue(isCorrectlyStickered(randomValidCube()))
    }
    @Test
    @Tag("CubeSimulationTest")
    fun testParityTests() {
        //tests solved cube and then all twists
        assertTrue(passesParityTests(solved()))
        assertTrue(passesParityTests(solved().twist(Twist.FRONT_90)))
        assertTrue(passesParityTests(solved().twist(Twist.FRONT_180)))
        assertTrue(passesParityTests(solved().twist(Twist.FRONT_270)))
        assertTrue(passesParityTests(solved().twist(Twist.BACK_90)))
        assertTrue(passesParityTests(solved().twist(Twist.BACK_180)))
        assertTrue(passesParityTests(solved().twist(Twist.BACK_270)))
        assertTrue(passesParityTests(solved().twist(Twist.LEFT_90)))
        assertTrue(passesParityTests(solved().twist(Twist.LEFT_180)))
        assertTrue(passesParityTests(solved().twist(Twist.LEFT_270)))
        assertTrue(passesParityTests(solved().twist(Twist.RIGHT_90)))
        assertTrue(passesParityTests(solved().twist(Twist.RIGHT_180)))
        assertTrue(passesParityTests(solved().twist(Twist.RIGHT_270)))
        assertTrue(passesParityTests(solved().twist(Twist.UP_90)))
        assertTrue(passesParityTests(solved().twist(Twist.UP_180)))
        assertTrue(passesParityTests(solved().twist(Twist.UP_270)))
        //tests special cases that should pass for everything
        assertTrue(passesParityTests(superFlip()))
        assertTrue(passesParityTests(threeEdgesSwapped()))
        assertTrue(passesParityTests(threeCornersSwapped()))
        assertTrue(passesParityTests(threeEdgesAndCornersSwapped()))
        //tests special cases that should fail for some things
        assertTrue(passesParityTests(switchedCenters()))
        assertTrue(passesParityTests(duplicateCenters()))
        assertFalse(passesParityTests(oneCornerTwisted()))
        assertFalse(passesParityTests(oneEdgeTwisted()))
        assertFalse(passesParityTests(twoEdgesSwapped()))
        assertTrue(isSolvable(extraEdgeParityTestCase()))
        //tests random cubes
        for(i in 0 until 100) assertTrue(passesParityTests(randomValidCube()))
    }
    @Test
    @Tag("CubeSimulationTest")
    fun testPermutationParityTest() {
        //tests solved cube and then all twists
        assertTrue(passesPermutationParityTest(solved()))
        assertTrue(passesPermutationParityTest(solved().twist(Twist.FRONT_90)))
        assertTrue(passesPermutationParityTest(solved().twist(Twist.FRONT_180)))
        assertTrue(passesPermutationParityTest(solved().twist(Twist.FRONT_270)))
        assertTrue(passesPermutationParityTest(solved().twist(Twist.BACK_90)))
        assertTrue(passesPermutationParityTest(solved().twist(Twist.BACK_180)))
        assertTrue(passesPermutationParityTest(solved().twist(Twist.BACK_270)))
        assertTrue(passesPermutationParityTest(solved().twist(Twist.LEFT_90)))
        assertTrue(passesPermutationParityTest(solved().twist(Twist.LEFT_180)))
        assertTrue(passesPermutationParityTest(solved().twist(Twist.LEFT_270)))
        assertTrue(passesPermutationParityTest(solved().twist(Twist.RIGHT_90)))
        assertTrue(passesPermutationParityTest(solved().twist(Twist.RIGHT_180)))
        assertTrue(passesPermutationParityTest(solved().twist(Twist.RIGHT_270)))
        assertTrue(passesPermutationParityTest(solved().twist(Twist.UP_90)))
        assertTrue(passesPermutationParityTest(solved().twist(Twist.UP_180)))
        assertTrue(passesPermutationParityTest(solved().twist(Twist.UP_270)))
        //tests special cases that should pass for everything
        assertTrue(passesPermutationParityTest(superFlip()))
        assertTrue(passesPermutationParityTest(threeEdgesSwapped()))
        assertTrue(passesPermutationParityTest(threeCornersSwapped()))
        assertTrue(passesPermutationParityTest(threeEdgesAndCornersSwapped()))
        //tests special cases that should fail for some things
        assertTrue(passesPermutationParityTest(switchedCenters()))
        assertTrue(passesPermutationParityTest(duplicateCenters()))
        assertTrue(passesPermutationParityTest(oneCornerTwisted()))
        assertTrue(passesPermutationParityTest(oneEdgeTwisted()))
        assertFalse(passesPermutationParityTest(twoEdgesSwapped()))
        assertTrue(passesPermutationParityTest(extraEdgeParityTestCase()))
        //tests random cubes
        for(i in 0 until 100) assertTrue(passesPermutationParityTest(randomValidCube()))
    }
    @Test
    @Tag("CubeSimulationTest")
    fun testEdgeParityTest() {
        //tests solved cube and then all twists
        assertTrue(passesEdgeParityTest(solved()))
        assertTrue(passesEdgeParityTest(solved().twist(Twist.FRONT_90)))
        assertTrue(passesEdgeParityTest(solved().twist(Twist.FRONT_180)))
        assertTrue(passesEdgeParityTest(solved().twist(Twist.FRONT_270)))
        assertTrue(passesEdgeParityTest(solved().twist(Twist.BACK_90)))
        assertTrue(passesEdgeParityTest(solved().twist(Twist.BACK_180)))
        assertTrue(passesEdgeParityTest(solved().twist(Twist.BACK_270)))
        assertTrue(passesEdgeParityTest(solved().twist(Twist.LEFT_90)))
        assertTrue(passesEdgeParityTest(solved().twist(Twist.LEFT_180)))
        assertTrue(passesEdgeParityTest(solved().twist(Twist.LEFT_270)))
        assertTrue(passesEdgeParityTest(solved().twist(Twist.RIGHT_90)))
        assertTrue(passesEdgeParityTest(solved().twist(Twist.RIGHT_180)))
        assertTrue(passesEdgeParityTest(solved().twist(Twist.RIGHT_270)))
        assertTrue(passesEdgeParityTest(solved().twist(Twist.UP_90)))
        assertTrue(passesEdgeParityTest(solved().twist(Twist.UP_180)))
        assertTrue(passesEdgeParityTest(solved().twist(Twist.UP_270)))
        //tests special cases that should pass for everything
        assertTrue(passesEdgeParityTest(superFlip()))
        assertTrue(passesEdgeParityTest(threeEdgesSwapped()))
        assertTrue(passesEdgeParityTest(threeCornersSwapped()))
        assertTrue(passesEdgeParityTest(threeEdgesAndCornersSwapped()))
        //tests special cases that should fail for some things
        assertTrue(passesEdgeParityTest(switchedCenters()))
        assertTrue(passesEdgeParityTest(duplicateCenters()))
        assertTrue(passesEdgeParityTest(oneCornerTwisted()))
        assertFalse(passesEdgeParityTest(oneEdgeTwisted()))
        assertTrue(passesEdgeParityTest(twoEdgesSwapped()))
        assertTrue(passesEdgeParityTest(extraEdgeParityTestCase()))
        //tests random cubes
        for (i in 0 until 100) assertTrue(passesEdgeParityTest(randomValidCube()))
    }
    @Test
    @Tag("CubeSimulationTest")
    fun testCornerParityTest() {
        //tests solved cube and then all twists
        assertTrue(passesCornerParityTest(solved()))
        assertTrue(passesCornerParityTest(solved().twist(Twist.FRONT_90)))
        assertTrue(passesCornerParityTest(solved().twist(Twist.FRONT_180)))
        assertTrue(passesCornerParityTest(solved().twist(Twist.FRONT_270)))
        assertTrue(passesCornerParityTest(solved().twist(Twist.BACK_90)))
        assertTrue(passesCornerParityTest(solved().twist(Twist.BACK_180)))
        assertTrue(passesCornerParityTest(solved().twist(Twist.BACK_270)))
        assertTrue(passesCornerParityTest(solved().twist(Twist.LEFT_90)))
        assertTrue(passesCornerParityTest(solved().twist(Twist.LEFT_180)))
        assertTrue(passesCornerParityTest(solved().twist(Twist.LEFT_270)))
        assertTrue(passesCornerParityTest(solved().twist(Twist.RIGHT_90)))
        assertTrue(passesCornerParityTest(solved().twist(Twist.RIGHT_180)))
        assertTrue(passesCornerParityTest(solved().twist(Twist.RIGHT_270)))
        assertTrue(passesCornerParityTest(solved().twist(Twist.UP_90)))
        assertTrue(passesCornerParityTest(solved().twist(Twist.UP_180)))
        assertTrue(passesCornerParityTest(solved().twist(Twist.UP_270)))
        //tests special cases that should pass for everything
        assertTrue(passesCornerParityTest(superFlip()))
        assertTrue(passesCornerParityTest(threeEdgesSwapped()))
        assertTrue(passesCornerParityTest(threeCornersSwapped()))
        assertTrue(passesCornerParityTest(threeEdgesAndCornersSwapped()))
        //tests special cases that should fail for some things
        assertTrue(passesCornerParityTest(switchedCenters()))
        assertTrue(passesCornerParityTest(duplicateCenters()))
        assertFalse(passesCornerParityTest(oneCornerTwisted()))
        assertTrue(passesCornerParityTest(oneEdgeTwisted()))
        assertTrue(passesCornerParityTest(twoEdgesSwapped()))
        assertTrue(passesCornerParityTest(extraEdgeParityTestCase()))
        //tests random cubes
        for(i in 0 until 100) assertTrue(passesCornerParityTest(randomValidCube()))
    }

}