package com.millibyte1.cubesearch.util

import com.millibyte1.cubesearch.cube.ArrayCube
import com.millibyte1.cubesearch.cube.ArrayCubeFactory

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class CubeGeneratorTest {

    private val factory = ArrayCubeFactory()

    //test fixtures
    private fun simpleGenerator(): CubeGenerator<ArrayCube> {
        return CubeGenerator<ArrayCube>(factory, 0, 2)
    }
    private fun simpleGenerator2() : CubeGenerator<ArrayCube> {
        return CubeGenerator<ArrayCube>(factory, 1, 2)
    }
    private fun oneMoveGenerator() : CubeGenerator<ArrayCube> {
        return CubeGenerator<ArrayCube>(factory, 0, 0)
    }

    @Test
    fun sameSeed() {
        var generator1 = simpleGenerator()
        var generator2 = simpleGenerator()
        var cube1 = generator1.nextCube()
        println("Cube1: $cube1")
        var cube2 = generator2.nextCube()
        println("Cube2: $cube2")
        assertEquals(cube1, cube2)
        cube2 = generator2.nextCube()
        println("Cube2: $cube2")
        assertNotEquals(cube1, cube2)
    }
    @Test
    fun differentSeeds() {
        var generator1 = simpleGenerator()
        var generator2 = simpleGenerator2()
        var cube1 = generator1.nextCube()
        var cube2 = generator2.nextCube()
        assertNotEquals(cube1, cube2)
    }
    @Test
    fun differentDifficulties() {
        var generator1 = simpleGenerator()
        var generator2 = oneMoveGenerator()
        var cube1 = generator1.nextCube()
        var cube2 = generator2.nextCube()
        assertNotEquals(cube1, cube2)
        println("cube1: $cube1")
        println("cube2: $cube2")
    }

}