package com.millibyte1.cubesearch.cube

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse

/**
 * Tests the 3x3 Cube implementation of Twistable
 */
class CubeTest {

    //test fixtures
    private fun solved(): Cube {
        return Cube(solvedData())
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

    //unit tests
    @Test
    fun constructors() {
        primaryConstructor()
        copyConstructor()
    }
    @Test
    fun primaryConstructor() {
        //primary constructor will work if the array copy extension function works
        val array1 = solvedData()
        val array2 = array1.copy()
        array1[0][0] = 1
        assertFalse(array1.contentDeepEquals(array2))
    }
    @Test
    fun copyConstructor() {
        //asserts that the two cubes are equal but are different objects
        val start = solved()
        val clone = Cube(start)
        assertTrue(start == clone)
        assertFalse(start === clone)
    }

    @Test
    fun immutability() {
        //immutability is dependent on constructors behaving as intended
        constructors()
        val start = solved()
        val clone = Cube(start)
        //tests
        for(twist in Twist.values()) {
            start.twist(twist)
            assertEquals(start, clone)
        }
    }

    @Test
    fun twists() {
        frontTwists()
        backTwists()
        leftTwists()
        rightTwists()
        upTwists()
        downTwists()
    }
    @Test
    fun frontTwists() {
        //sets up and tests initial turns
        val start = solved()
        var clone1: Twistable = Cube(start)
        var clone2: Twistable = Cube(start)
        var clone3: Twistable = Cube(start)

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
    }
    @Test
    fun backTwists() {
        //sets up and tests initial turns
        val start = solved()
        var clone1: Twistable = Cube(start)
        var clone2: Twistable = Cube(start)
        var clone3: Twistable = Cube(start)

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
    }
    @Test
    fun leftTwists() {
        //sets up and tests initial turns
        val start = solved()
        var clone1: Twistable = Cube(start)
        var clone2: Twistable = Cube(start)
        var clone3: Twistable = Cube(start)

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
    }
    @Test
    fun rightTwists() {
        //sets up and tests initial turns
        val start = solved()
        var clone1: Twistable = Cube(start)
        var clone2: Twistable = Cube(start)
        var clone3: Twistable = Cube(start)

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
    }
    @Test
    fun upTwists() {
        //sets up and tests initial turns
        val start = solved()
        var clone1: Twistable = Cube(start)
        var clone2: Twistable = Cube(start)
        var clone3: Twistable = Cube(start)

        clone1 = clone1.twist(Twist.UP_90)
        clone2 = clone2.twist(Twist.UP_180)
        clone3 = clone3.twist(Twist.UP_270)
        println("upTwists")
        println("Clone 1: $clone1")
        println("Clone 2: $clone2")
        println("Clone 3: $clone3")
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
    }
    @Test
    fun downTwists() {
        //sets up and tests initial turns
        val start = solved()
        var clone1: Twistable = Cube(start)
        var clone2: Twistable = Cube(start)
        var clone3: Twistable = Cube(start)

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
    }
}