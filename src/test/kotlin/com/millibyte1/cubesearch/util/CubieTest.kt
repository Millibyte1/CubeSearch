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

//TODO: figure out how to nest test cases
class CubieTest {

    private val factory = CubeFactory()

    //test fixtures
    private fun solved(): Cube {
        return factory.getSolvedCube()
    }
    private fun centerTile(): Tile {
        return Tile(Face.FRONT, 4, 0)
    }
    private fun edgeTile(): Tile {
        return Tile(Face.FRONT, 1, 0)
    }
    private fun cornerTile(): Tile {
        return Tile(Face.FRONT, 0, 0)
    }

    @Test
    @Tag("CubeSimulationTest")
    fun getAllCubies() {
        getCenterCubies()
        getEdgeCubies()
        getCornerCubies()
    }

    @Test
    @Tag("CubeSimulationTest")
    fun getCubieFromTile() {
        getValidCubieFromTile()
        getInvalidCubieFromTile()
    }
    @Test
    @Tag("CubeSimulationTest")
    fun getValidCubieFromTile() {
        var cube = solved()
        var t: Tile
        var color: Int
        for(face in Face.values()) {
            color = face.ordinal
            for(index in 0 until 9) {
                t = Tile(face, index, color)
                getCubie(cube, t)
            }
        }
    }
    @Test
    @Tag("CubeSimulationTest")
    fun getInvalidCubieFromTile() {
        var cube = solved()
        var t: Tile
        for(face in Face.values()) {
            for (color in 0 until 6) {
                for (index in 0 until 9) {
                    t = Tile(face, index, color)
                    if(color != face.ordinal) {
                        assertFailsWith(IllegalArgumentException::class) { getCubie(cube, t) }
                    }
                }
            }
        }
    }

    @Test
    @Tag("CubeSimulationTest")
    fun getCenterCubies() {
        getValidCenterCubies()
        getInvalidCenterCubies()
    }
    @Test
    @Tag("CubeSimulationTest")
    fun getValidCenterCubies() {
        var t: Tile
        for(face in Face.values()) {
            for(color in 0 until 6) {
                t = Tile(face, 4, color)
                //if this throws an exception, the test fails
                getCenterCubie(t)
            }
        }
    }
    @Test
    @Tag("CubeSimulationTest")
    fun getInvalidCenterCubies() {
        var t: Tile
        for(face in Face.values()) {
            for(color in 0 until 6) {
                for(index in 0 until 9) {
                    t = Tile(face, index, color)
                    if(!isOnCenterCubie(t)) {
                        assertFailsWith(IllegalArgumentException::class) { getCenterCubie(t) }
                    }
                }
            }
        }
    }

    @Test
    @Tag("CubeSimulationTest")
    fun getEdgeCubies() {
        getValidEdgeCubies()
        getInvalidEdgeCubies()
    }
    @Test
    @Tag("CubeSimulationTest")
    fun getValidEdgeCubies() {
        val cube = solved()
        var t1: Tile
        var t2: Tile
        var color: Int
        for(face in Face.values()) {
            color = face.ordinal
            //for every edge tile on this face
            for(index in 1 until 9 step 2) {
                //checks that it succeeds with the correct other tile
                t1 = Tile(face, index, color)
                t2 = getOtherTileOnEdgeCubie(cube, t1)
                getEdgeCubie(t1, t2)
            }
        }
    }
    @Test
    @Tag("CubeSimulationTest")
    fun getInvalidEdgeCubies() {
        val cube = solved()
        var t1: Tile
        var t2: Tile
        for(face1 in Face.values()) {
            for(color1 in 0 until 6) {
                for(index1 in 0 until 9) {
                    t1 = Tile(face1, index1, color1)
                    for(face2 in Face.values()) {
                        for(color2 in 0 until 6) {
                            for(index2 in 0 until 9) {
                                t2 = Tile(face2, index2, color2)
                                if(!isOnSameEdgeCubie(t1, t2)) {
                                    assertFailsWith(IllegalArgumentException::class) { getEdgeCubie(t1, t2) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    @Tag("CubeSimulationTest")
    fun getCornerCubies() {
        getValidCornerCubies()
        getInvalidCornerCubies()
    }
    @Test
    @Tag("CubeSimulationTest")
    fun getValidCornerCubies() {
        val cube = solved()
        var t1: Tile
        var others: Pair<Tile, Tile>
        var color: Int
        for(face in Face.values()) {
            color = face.ordinal
            for(index in 0 until 9 step 2) {
                if(index != 4) {
                    //this is a corner tile
                    t1 = Tile(face, index, color)
                    others = getOtherTilesOnCornerCubie(cube, t1)
                    getCornerCubie(t1, others.first, others.second)
                }
            }
        }
    }
    @Test
    @Tag("CubeSimulationTest")
    //This test will take several minutes to run
    //TODO: make this test case run in parallel
    fun getInvalidCornerCubies() {
        val cube = solved()
        var t1: Tile
        var t2: Tile
        var t3: Tile
        for(face1 in Face.values()) {
            for(color1 in 0 until 6) {
                //println("face1: $face1, color1: $color1")
                for(index1 in 0 until 9) {
                    t1 = Tile(face1, index1, color1)
                    for(face2 in Face.values()) {
                        for(color2 in 0 until 6) {
                            for(index2 in 0 until 9) {
                                t2 = Tile(face2, index2, color2)
                                for(face3 in Face.values()) {
                                    for(color3 in 0 until 6) {
                                        for(index3 in 0 until 9) {
                                            t3 = Tile(face3, index3, color2)
                                            if(!isOnSameCornerCubie(t1, t2, t3)) {
                                                assertFailsWith(IllegalArgumentException::class) { getCornerCubie(t1, t2, t3) }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}