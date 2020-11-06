package com.millibyte1.cubesearch.util

import com.millibyte1.cubesearch.util.Cubie

import com.millibyte1.cubesearch.cube.CubeFactory
import com.millibyte1.cubesearch.cube.Cube
import com.millibyte1.cubesearch.cube.Twist
import com.millibyte1.cubesearch.cube.Twist.Face

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Assertions.*
import org.opentest4j.AssertionFailedError
import kotlin.test.assertFailsWith

class StandardCubeUtilsTest {


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

    /* ========================================== TEST FUNCTIONS ==================================================== */

    @Test
    @Tag("CubeSimulationTest")
    fun testGetFunctions() {
        testGetCubies()
        testGetCubieOnFaces()
        testGetCubieAt()
    }
    @Test
    @Tag("CubeSimulationTest")
    fun testGetCubies() {

    }
    @Test
    @Tag("CubeSimulationTest")
    fun testGetCubieOnFaces() {

    }
    @Test
    @Tag("CubeSimulationTest")
    fun testGetCubieAt() {

    }


    @Test
    @Tag("CubeSimulationTest")
    fun testIsOnFunctions() {
        testIsOnCenterCubie()
        testIsOnEdgeCubie()
        testIsOnCornerCubie()
    }
    @Test
    @Tag("CubeSimulationTest")
    fun testIsOnCenterCubie() {
        val cubies = getCubies(solved())
        for(cubie in cubies) {
            for(pos in getTilePositionsOnCubie(cubie)) {
                if(cubie is CenterCubie) assertTrue(isOnCenterCubie(pos))
                else assertFalse(isOnCenterCubie(pos))
            }
        }
    }
    @Test
    @Tag("CubeSimulationTest")
    fun testIsOnEdgeCubie() {
        val cubies = getCubies(solved())
        for(cubie in cubies) {
            for(pos in getTilePositionsOnCubie(cubie)) {
                if(cubie is EdgeCubie) assertTrue(isOnEdgeCubie(pos))
                else assertFalse(isOnEdgeCubie(pos))
            }
        }
    }
    @Test
    @Tag("CubeSimulationTest")
    fun testIsOnCornerCubie() {
        val cubies = getCubies(solved())
        for(cubie in cubies) {
            for(pos in getTilePositionsOnCubie(cubie)) {
                if(cubie is CornerCubie) assertTrue(isOnCornerCubie(pos))
                else assertFalse(isOnCornerCubie(pos))
            }
        }
    }
    @Test
    @Tag("CubeSimulationTest")
    fun testIsOnSameCubie() {
        val cubies = getCubies(solved())
        for(cubie1 in cubies) {
            for(cubie2 in cubies) {
                for(pos1 in getTilePositionsOnCubie(cubie1)) {
                    for(pos2 in getTilePositionsOnCubie(cubie2)) {
                        if(cubie1 == cubie2) assertTrue(isOnSameCubie(pos1, pos2))
                        else assertFalse(isOnSameCubie(pos1, pos2))
                    }
                }
            }
        }
    }

    private fun getTilePositionsOnCubie(cubie: Cubie): Array<TilePosition> {
        return when(cubie) {
            is CenterCubie -> arrayOf(cubie.tile1.pos)
            is EdgeCubie -> arrayOf(cubie.tile1.pos, cubie.tile2.pos)
            is CornerCubie -> arrayOf(cubie.tile1.pos, cubie.tile2.pos, cubie.tile3.pos)
        }
    }
}