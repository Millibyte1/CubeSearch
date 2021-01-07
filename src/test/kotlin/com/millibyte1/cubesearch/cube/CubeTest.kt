package com.millibyte1.cubesearch.cube

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Tag

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse

/**
 * Unit and stress tests for the ArrayCube implementation
 */
class CubeTest {

    private val factory = ArrayCubeFactory()

    //test fixtures
    private fun solved(): ArrayCube {
        return factory.getSolvedCube()
    }
    private fun solvedData(): Array<IntArray> {
        return arrayOf(
                IntArray(9) { 0 }, //front face
                IntArray(9) { 1 }, //back face
                IntArray(9) { 2 }, //left face
                IntArray(9) { 3 }, //right face
                IntArray(9) { 4 }, //up face
                IntArray(9) { 5 }  //down face
        )
    }
    /** The data of a solved cube after a single F */
    private fun frontData(): Array<IntArray> {
        return arrayOf(
                //front face
                IntArray(9) { 0 },
                //back face
                IntArray(9) { 1 },
                //left face
                intArrayOf(2, 2, 5,
                           2, 2, 5,
                           2, 2, 5
                ),
                //right face
                intArrayOf(4, 3, 3,
                           4, 3, 3,
                           4, 3, 3
                ),
                //up face
                intArrayOf(4, 4, 4,
                           4, 4, 4,
                           2, 2, 2
                ),
                //down face
                intArrayOf(3, 3, 3,
                           5, 5, 5,
                           5, 5, 5
                )
        )
    }
    /** The data of a solved cube after a single B */
    private fun backData(): Array<IntArray> {
        return arrayOf(
                //front face
                IntArray(9) { 0 },
                //back face
                IntArray(9) { 1 },
                //left face
                intArrayOf(4, 2, 2,
                           4, 2, 2,
                           4, 2, 2
                ),
                //right face
                intArrayOf(3, 3, 5,
                           3, 3, 5,
                           3, 3, 5
                ),
                //up face
                intArrayOf(3, 3, 3,
                           4, 4, 4,
                           4, 4, 4
                ),
                //down face
                intArrayOf(5, 5, 5,
                           5, 5, 5,
                           2, 2, 2
                )
        )
    }
    /** The data of a solved cube after a single L */
    private fun leftData(): Array<IntArray> {
        return arrayOf(
                //front face
                intArrayOf(4, 0, 0,
                           4, 0, 0,
                           4, 0, 0
                ),
                //back face
                intArrayOf(1, 1, 5,
                           1, 1, 5,
                           1, 1, 5
                ),
                //left face
                IntArray(9) { 2 },
                //right face
                IntArray(9) { 3 },
                //up face
                intArrayOf(1, 4, 4,
                           1, 4, 4,
                           1, 4, 4
                ),
                //down face
                intArrayOf(0, 5, 5,
                           0, 5, 5,
                           0, 5, 5
                )
        )
    }
    /** The data of a solved cube after a single R */
    private fun rightData(): Array<IntArray> {
        return arrayOf(
                //front face
                intArrayOf(0, 0, 5,
                           0, 0, 5,
                           0, 0, 5
                ),
                //back face
                intArrayOf(4, 1, 1,
                           4, 1, 1,
                           4, 1, 1
                ),
                //left face
                IntArray(9) { 2 },
                //right face
                IntArray(9) { 3 },
                //up face
                intArrayOf(4, 4, 0,
                           4, 4, 0,
                           4, 4, 0
                ),
                //down face
                intArrayOf(5, 5, 1,
                           5, 5, 1,
                           5, 5, 1
                )
        )
    }
    /** The data of a solved cube after a single U */
    private fun upData(): Array<IntArray> {
        return arrayOf(
                //front face
                intArrayOf(3, 3, 3,
                           0, 0, 0,
                           0, 0, 0
                ),
                //back face
                intArrayOf(2, 2, 2,
                           1, 1, 1,
                           1, 1, 1
                ),
                //left face
                intArrayOf(0, 0, 0,
                           2, 2, 2,
                           2, 2, 2
                ),
                //right face
                intArrayOf(1, 1, 1,
                           3, 3, 3,
                           3, 3, 3
                ),
                //up face
                IntArray(9) { 4 },
                //down face
                IntArray(9) { 5 }
        )
    }
    /** The data of a solved cube after a single D */
    private fun downData(): Array<IntArray> {
        return arrayOf(
                //front face
                intArrayOf(0, 0, 0,
                           0, 0, 0,
                           2, 2, 2
                ),
                //back face
                intArrayOf(1, 1, 1,
                           1, 1, 1,
                           3, 3, 3
                ),
                //left face
                intArrayOf(2, 2, 2,
                           2, 2, 2,
                           1, 1, 1
                ),
                //right face
                intArrayOf(3, 3, 3,
                           3, 3, 3,
                           0, 0, 0
                ),
                //up face
                IntArray(9) { 4 },
                //down face
                IntArray(9) { 5 }
        )
    }

    //unit tests
    @Test
    @Tag("CubeSimulationTest")
    fun constructors() {
        primaryConstructor()
        copyConstructor()
    }
    @Test
    @Tag("CubeSimulationTest")
    fun primaryConstructor() {
        //primary constructor will work if the array copy extension function works
        val array1 = solvedData()
        val array2 = array1.copy()
        array1[0][0] = 1
        assertFalse(array1.contentDeepEquals(array2))
    }
    @Test
    @Tag("CubeSimulationTest")
    fun copyConstructor() {
        //asserts that the two cubes are equal but are different objects
        val start = solved()
        val clone = factory.getCube(start)
        assertTrue(start == clone)
        assertFalse(start === clone)
    }

    @Test
    @Tag("CubeSimulationTest")
    fun twistsAreSideEffectFree() {
        val start = solved()
        val clone = factory.getCube(start)
        //tests
        for(twist in Twist.values()) {
            start.twist(twist)
            assertEquals(start, clone)
        }
    }

    @Test
    @Tag("CubeSimulationTest")
    fun twists() {
        frontTwists()
        backTwists()
        leftTwists()
        rightTwists()
        upTwists()
        downTwists()
    }
    @Test
    @Tag("CubeSimulationTest")
    fun frontTwists() {
        //sets up and tests initial turns
        val start = solved()
        var clone1: ArrayCube = factory.getCube(start)
        var clone2: ArrayCube = factory.getCube(start)
        var clone3: ArrayCube = factory.getCube(start)

        clone1 = clone1.twist(Twist.FRONT_90)
        clone2 = clone2.twist(Twist.FRONT_180)
        clone3 = clone3.twist(Twist.FRONT_270)
        //println("frontTwists")
        //println("Clone 1: $clone1")
        //println("Clone 2: $clone2")
        //println("Clone 3: $clone3")
        assertFalse(clone1 == start)
        assertFalse(clone2 == start)
        assertFalse(clone3 == start)

        //tests clockwise / F
        clone1 = clone1.twist(Twist.FRONT_90)
        assertTrue(clone1 == clone2)
        clone1 = clone1.twist(Twist.FRONT_90)
        assertTrue(clone1 == clone3)
        clone1 = clone1.twist(Twist.FRONT_90)
        assertTrue(clone1 == start)
        clone1 = clone1.twist(Twist.FRONT_90)
        //tests half-turn / F2
        clone2 = clone2.twist(Twist.FRONT_180)
        assertTrue(clone2 == start)
        clone2 = clone2.twist(Twist.FRONT_180)
        //tests counter-clockwise / F'
        clone3 = clone3.twist(Twist.FRONT_270)
        assertTrue(clone3 == clone2)
        clone3 = clone3.twist(Twist.FRONT_270)
        assertTrue(clone3 == clone1)
        clone3 = clone3.twist(Twist.FRONT_270)
        assertTrue(clone3 == start)

        //tests that F is actually correct
        clone1 = start.twist(Twist.FRONT_90)
        clone2 = factory.getCube(frontData())
        //println("clone1: $clone1")
        //println("clone2: $clone2")
        assertEquals(clone1, clone2)
    }
    @Test
    @Tag("CubeSimulationTest")
    fun backTwists() {
        //sets up and tests initial turns
        val start = solved()
        var clone1: ArrayCube = factory.getCube(start)
        var clone2: ArrayCube = factory.getCube(start)
        var clone3: ArrayCube = factory.getCube(start)

        clone1 = clone1.twist(Twist.BACK_90)
        clone2 = clone2.twist(Twist.BACK_180)
        clone3 = clone3.twist(Twist.BACK_270)
        //println("backTwists")
        //println("Clone 1: $clone1")
        //println("Clone 2: $clone2")
        //println("Clone 3: $clone3")
        assertFalse(clone1 == start)
        assertFalse(clone2 == start)
        assertFalse(clone3 == start)

        //tests clockwise / B
        clone1 = clone1.twist(Twist.BACK_90)
        assertTrue(clone1 == clone2)
        clone1 = clone1.twist(Twist.BACK_90)
        assertTrue(clone1 == clone3)
        clone1 = clone1.twist(Twist.BACK_90)
        assertTrue(clone1 == start)
        clone1 = clone1.twist(Twist.BACK_90)
        //tests half-turn / B2
        clone2 = clone2.twist(Twist.BACK_180)
        assertTrue(clone2 == start)
        clone2 = clone2.twist(Twist.BACK_180)
        //tests counter-clockwise / B'
        clone3 = clone3.twist(Twist.BACK_270)
        assertTrue(clone3 == clone2)
        clone3 = clone3.twist(Twist.BACK_270)
        assertTrue(clone3 == clone1)
        clone3 = clone3.twist(Twist.BACK_270)
        assertTrue(clone3 == start)

        //tests that B is actually clone1clone1 = start.twist(Twist.BACK_90)
        clone1 = solved().twist(Twist.BACK_90)
        clone2 = factory.getCube(backData())
        assertEquals(clone1, clone2)
    }
    @Test
    @Tag("CubeSimulationTest")
    fun leftTwists() {
        //sets up and tests initial turns
        val start = solved()
        var clone1: ArrayCube = factory.getCube(start)
        var clone2: ArrayCube = factory.getCube(start)
        var clone3: ArrayCube = factory.getCube(start)

        clone1 = clone1.twist(Twist.LEFT_90)
        clone2 = clone2.twist(Twist.LEFT_180)
        clone3 = clone3.twist(Twist.LEFT_270)
        //println("leftTwists")
        //println("Clone 1: $clone1")
        //println("Clone 2: $clone2")
        //println("Clone 3: $clone3")
        assertFalse(clone1 == start)
        assertFalse(clone2 == start)
        assertFalse(clone3 == start)

        //tests clockwise / L
        clone1 = clone1.twist(Twist.LEFT_90)
        assertTrue(clone1 == clone2)
        clone1 = clone1.twist(Twist.LEFT_90)
        assertTrue(clone1 == clone3)
        clone1 = clone1.twist(Twist.LEFT_90)
        assertTrue(clone1 == start)
        clone1 = clone1.twist(Twist.LEFT_90)
        //tests half-turn / L2
        clone2 = clone2.twist(Twist.LEFT_180)
        assertTrue(clone2 == start)
        clone2 = clone2.twist(Twist.LEFT_180)
        //tests counter-clockwise / L'
        clone3 = clone3.twist(Twist.LEFT_270)
        assertTrue(clone3 == clone2)
        clone3 = clone3.twist(Twist.LEFT_270)
        assertTrue(clone3 == clone1)
        clone3 = clone3.twist(Twist.LEFT_270)
        assertTrue(clone3 == start)

        //tests that L is actually correct
        clone1 = start.twist(Twist.LEFT_90)
        clone2 = factory.getCube(leftData())
        assertEquals(clone1, clone2)
    }
    @Test
    @Tag("CubeSimulationTest")
    fun rightTwists() {
        //sets up and tests initial turns
        val start = solved()
        var clone1: ArrayCube = factory.getCube(start)
        var clone2: ArrayCube = factory.getCube(start)
        var clone3: ArrayCube = factory.getCube(start)

        clone1 = clone1.twist(Twist.RIGHT_90)
        clone2 = clone2.twist(Twist.RIGHT_180)
        clone3 = clone3.twist(Twist.RIGHT_270)
        //println("rightTwists")
        //println("Clone 1: $clone1")
        //println("Clone 2: $clone2")
        //println("Clone 3: $clone3")
        assertFalse(clone1 == start)
        assertFalse(clone2 == start)
        assertFalse(clone3 == start)

        //tests clockwise / R
        clone1 = clone1.twist(Twist.RIGHT_90)
        assertTrue(clone1 == clone2)
        clone1 = clone1.twist(Twist.RIGHT_90)
        assertTrue(clone1 == clone3)
        clone1 = clone1.twist(Twist.RIGHT_90)
        assertTrue(clone1 == start)
        clone1 = clone1.twist(Twist.RIGHT_90)
        //tests half-turn / R2
        clone2 = clone2.twist(Twist.RIGHT_180)
        assertTrue(clone2 == start)
        clone2 = clone2.twist(Twist.RIGHT_180)
        //tests counter-clockwise / R'
        clone3 = clone3.twist(Twist.RIGHT_270)
        assertTrue(clone3 == clone2)
        clone3 = clone3.twist(Twist.RIGHT_270)
        assertTrue(clone3 == clone1)
        clone3 = clone3.twist(Twist.RIGHT_270)
        assertTrue(clone3 == start)

        //tests that R is actually correct
        clone1 = start.twist(Twist.RIGHT_90)
        clone2 = factory.getCube(rightData())
        assertEquals(clone1, clone2)
    }
    @Test
    @Tag("CubeSimulationTest")
    fun upTwists() {
        //sets up and tests initial turns
        val start = solved()
        var clone1: ArrayCube = factory.getCube(start)
        var clone2: ArrayCube = factory.getCube(start)
        var clone3: ArrayCube = factory.getCube(start)

        clone1 = clone1.twist(Twist.UP_90)
        clone2 = clone2.twist(Twist.UP_180)
        clone3 = clone3.twist(Twist.UP_270)
        //println("upTwists")
        //println("Clone 1: $clone1")
        //println("Clone 2: $clone2")
        //println("Clone 3: $clone3")
        assertFalse(clone1 == start)
        assertFalse(clone2 == start)
        assertFalse(clone3 == start)

        //tests clockwise / U
        clone1 = clone1.twist(Twist.UP_90)
        assertTrue(clone1 == clone2)
        clone1 = clone1.twist(Twist.UP_90)
        assertTrue(clone1 == clone3)
        clone1 = clone1.twist(Twist.UP_90)
        assertTrue(clone1 == start)
        clone1 = clone1.twist(Twist.UP_90)
        //tests half-turn / U2
        clone2 = clone2.twist(Twist.UP_180)
        assertTrue(clone2 == start)
        clone2 = clone2.twist(Twist.UP_180)
        //tests counter-clockwise / U'
        clone3 = clone3.twist(Twist.UP_270)
        assertTrue(clone3 == clone2)
        clone3 = clone3.twist(Twist.UP_270)
        assertTrue(clone3 == clone1)
        clone3 = clone3.twist(Twist.UP_270)
        assertTrue(clone3 == start)

        //tests that U is actually correct
        clone1 = start.twist(Twist.UP_90)
        clone2 = factory.getCube(upData())
        assertEquals(clone1, clone2)
    }
    @Test
    @Tag("CubeSimulationTest")
    fun downTwists() {
        //sets up and tests initial turns
        val start = solved()
        var clone1: ArrayCube = factory.getCube(start)
        var clone2: ArrayCube = factory.getCube(start)
        var clone3: ArrayCube = factory.getCube(start)

        clone1 = clone1.twist(Twist.DOWN_90)
        clone2 = clone2.twist(Twist.DOWN_180)
        clone3 = clone3.twist(Twist.DOWN_270)
        //println("downTwists")
        //println("Clone 1: $clone1")
        //println("Clone 2: $clone2")
        //println("Clone 3: $clone3")
        assertFalse(clone1 == start)
        assertFalse(clone2 == start)
        assertFalse(clone3 == start)

        //tests clockwise / D
        clone1 = clone1.twist(Twist.DOWN_90)
        assertTrue(clone1 == clone2)
        clone1 = clone1.twist(Twist.DOWN_90)
        assertTrue(clone1 == clone3)
        clone1 = clone1.twist(Twist.DOWN_90)
        assertTrue(clone1 == start)
        clone1 = clone1.twist(Twist.DOWN_90)
        //tests half-turn / D2
        clone2 = clone2.twist(Twist.DOWN_180)
        assertTrue(clone2 == start)
        clone2 = clone2.twist(Twist.DOWN_180)
        //tests counter-clockwise / D'
        clone3 = clone3.twist(Twist.DOWN_270)
        assertTrue(clone3 == clone2)
        clone3 = clone3.twist(Twist.DOWN_270)
        assertTrue(clone3 == clone1)
        clone3 = clone3.twist(Twist.DOWN_270)
        assertTrue(clone3 == start)

        //tests that D is actually correct
        clone1 = start.twist(Twist.DOWN_90)
        clone2 = factory.getCube(downData())
        assertEquals(clone1, clone2)
    }

    @Test
    @Tag("CubeSimulationTest")
    fun stressTestImmutableTwists() {
        var cube = solved()
        //performs 18 million twists using the copying twist implementation
        for(i in 0 until 1000000) {
            for(twist in Twist.values()) {
                cube = cube.twist(twist)
            }
        }
    }
    @Test
    @Tag("CubeSimulationTest")
    fun stressTestMutableTwists() {
        val cube = solved()
        //performs 18 million twists using the no-copy twist implementation
        for(i in 0 until 1000000) {
            for(twist in Twist.values()) {
                cube.twistNoCopy(twist)
            }
        }
    }
}