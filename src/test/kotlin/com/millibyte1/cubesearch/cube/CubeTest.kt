package com.millibyte1.cubesearch.cube

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

import com.millibyte1.cubesearch.cube.Cube
import com.millibyte1.cubesearch.cube.Twist

/**
 * Tests the 3x3 Cube implementation of Twistable
 */
class CubeTest {

    private fun solved(): Cube {
        return Cube(arrayOf(
                IntArray(9) { 0 }, //front face
                IntArray(9) { 1 }, //back face
                IntArray(9) { 2 }, //left face
                IntArray(9) { 3 }, //right face
                IntArray(9) { 4 }, //up face
                IntArray(9) { 5 }  //down face
        ))
    }

    @Test
    fun constructors() {
        primaryConstructor()
        copyConstructor()
    }

    @Test
    fun primaryConstructor() {
        //TODO(tuples)
    }
    @Test
    fun copyConstructor() {
        val start = solved()
        val clone = Cube(start)
        assertTrue(start == clone)
        assertTrue(start !== clone)
    }
    @Test
    fun immutability() {
        //immutability is dependent on constructors behaving as intended
        constructors()
        val start = solved()
        val clone = Cube(start)
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
        //TODO(tuples)
    }
    @Test
    fun backTwists() {
        //TODO(tuples)
    }
    @Test
    fun leftTwists() {
        //TODO(tuples)
    }
    @Test
    fun rightTwists() {
        //TODO(tuples)
    }
    @Test
    fun upTwists() {
        //TODO(tuples)
    }
    @Test
    fun downTwists() {
        //TODO(tuples)
    }
}