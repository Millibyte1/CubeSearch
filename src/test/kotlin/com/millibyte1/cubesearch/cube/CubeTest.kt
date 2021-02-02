package com.millibyte1.cubesearch.cube

import com.millibyte1.cubesearch.algorithm.heuristics.CornerPatternDatabase
import com.millibyte1.cubesearch.algorithm.heuristics.FileCore
import com.millibyte1.cubesearch.algorithm.heuristics.RedisCore

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import redis.clients.jedis.Jedis
import java.io.File

/**
 * Unit and stress tests for the SmartCube implementation
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CubeTest {

    private val factory = SmartCubeFactory()
    private val arrayCubeFactory = ArrayCubeFactory()

    //test fixtures
    private fun solved(): SmartCube {
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
    @ParameterizedTest
    @Tag("CubeSimulationTest")
    @Order(1)
    @MethodSource("implementationFactories")
    fun testFactoryMethods(factory: CubeFactory) {
        primaryConstructor(factory)
        copyConstructor(factory)
    }
    private fun primaryConstructor(factory: CubeFactory) {
        //asserts that the two cubes are equal but are different objects
        val start = factory.getSolvedCube()
        val clone = factory.getCube(start.getTiles())
        assertTrue(start == clone)
        assertFalse(start === clone)
    }
    private fun copyConstructor(factory: CubeFactory) {
        //asserts that the two cubes are equal but are different objects
        val start = factory.getSolvedCube()
        val clone = factory.getCube(start)
        assertTrue(start == clone)
        assertFalse(start === clone)
    }

    @ParameterizedTest
    @Tag("CubeSimulationTest")
    @Order(2)
    @MethodSource("implementationFactories")
    fun testTwistValues(factory: CubeFactory) {
        frontTwists(factory)
        backTwists(factory)
        leftTwists(factory)
        rightTwists(factory)
        upTwists(factory)
        downTwists(factory)
    }
    private fun frontTwists(factory: CubeFactory) {
        //sets up and tests initial turns
        val start = factory.getSolvedCube()
        var clone1 = factory.getCube(start)
        var clone2 = factory.getCube(start)
        var clone3 = factory.getCube(start)

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
    private fun backTwists(factory: CubeFactory) {
        //sets up and tests initial turns
        val start = factory.getSolvedCube()
        var clone1 = factory.getCube(start)
        var clone2 = factory.getCube(start)
        var clone3 = factory.getCube(start)

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

        //tests that B is actually clone1
        clone1 = start.twist(Twist.BACK_90)
        clone2 = factory.getCube(backData())
        assertEquals(clone1, clone2)
    }
    private fun leftTwists(factory: CubeFactory) {
        //sets up and tests initial turns
        val start = factory.getSolvedCube()
        var clone1 = factory.getCube(start)
        var clone2 = factory.getCube(start)
        var clone3 = factory.getCube(start)

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
    private fun rightTwists(factory: CubeFactory) {
        //sets up and tests initial turns
        val start = factory.getSolvedCube()
        var clone1 = factory.getCube(start)
        var clone2 = factory.getCube(start)
        var clone3 = factory.getCube(start)

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

        //assertEquals(clone1, clone2)
        //tests that R is actually correct
        clone1 = start.twist(Twist.RIGHT_90)
        clone2 = factory.getCube(rightData())
        assertEquals(clone1, clone2)
    }
    private fun upTwists(factory: CubeFactory) {
        //sets up and tests initial turns
        val start = factory.getSolvedCube()
        var clone1 = factory.getCube(start)
        var clone2 = factory.getCube(start)
        var clone3 = factory.getCube(start)

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
    private fun downTwists(factory: CubeFactory) {
        //sets up and tests initial turns
        val start = factory.getSolvedCube()
        var clone1 = factory.getCube(start)
        var clone2 = factory.getCube(start)
        var clone3 = factory.getCube(start)

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

    @ParameterizedTest
    @Tag("CubeSimulationTest")
    @Order(3)
    @MethodSource("implementationFactories")
    fun testTwistsAreSideEffectFree(factory: CubeFactory) {
        val start = factory.getSolvedCube()
        val clone = factory.getCube(start)
        //tests
        for(twist in Twist.values()) {
            start.twist(twist)
            assertEquals(start, clone)
        }
    }
    @ParameterizedTest
    @Tag("CubeSimulationTest")
    @Order(4)
    @MethodSource("analyzableFactories")
    fun testPositionValues(factory: AnalyzableStandardCubeFactory) {
        var cube1 = factory.getSolvedCube()
        var cube2 = factory.getSolvedCube()
        val edgePositions = cube1.getEdgePositionPermutation()
        val cornerPositions = cube1.getCornerPositionPermutation()
        //tests initial values for solved cube
        for(i in edgePositions.indices) assertEquals(edgePositions[i], i)
        for(i in cornerPositions.indices) assertEquals(cornerPositions[i], i)
        assertTrue(edgePositionsMatchExpected(cube1))
        assertTrue(cornerPositionsMatchExpected(cube1))
        //performs parity tests for both mutable and immutable twists
        for(i in 0 until 100) {
            for(twist in Twist.values()) {
                cube1 = cube1.twist(twist)
                cube2 = cube2.twistNoCopy(twist)
                assertTrue(edgePositionsMatchExpected(cube1))
                assertTrue(cornerPositionsMatchExpected(cube1))
                assertTrue(edgePositionsMatchExpected(cube2))
                assertTrue(cornerPositionsMatchExpected(cube2))
            }
        }
    }
    @ParameterizedTest
    @Tag("CubeSimulationTest")
    @Order(5)
    @MethodSource("analyzableFactories")
    fun testOrientationValues(factory: AnalyzableStandardCubeFactory) {
        var cube1 = factory.getSolvedCube()
        var cube2 = factory.getSolvedCube()
        //tests initial values for solved cube
        for(orientation in cube1.getEdgeOrientationPermutation()) assertEquals(orientation, 0)
        for(orientation in cube1.getCornerOrientationPermutation()) assertEquals(orientation, 0)
        //performs parity tests for both mutable and immutable twists
        for(i in 0 until 100) {
            for(twist in Twist.values()) {
                cube1 = cube1.twist(twist)
                cube2 = cube2.twistNoCopy(twist)
                assertTrue(passesEdgeParity(cube1))
                assertTrue(passesCornerParity(cube1))
                assertTrue(edgeOrientationsMatchExpected(cube1))
                assertTrue(cornerOrientationsMatchExpected(cube1))
                assertTrue(passesEdgeParity(cube2))
                assertTrue(passesCornerParity(cube2))
                assertTrue(edgeOrientationsMatchExpected(cube2))
                assertTrue(cornerOrientationsMatchExpected(cube2))
            }
        }
    }
    private fun passesEdgeParity(cube: AnalyzableStandardCube): Boolean {
        var orientationSum = 0
        for(orientation in cube.getEdgeOrientationPermutation()) orientationSum += orientation
        return orientationSum % 2 == 0
    }
    private fun passesCornerParity(cube: AnalyzableStandardCube): Boolean {
        var orientationSum = 0
        for(orientation in cube.getCornerOrientationPermutation()) orientationSum += orientation
        return orientationSum % 3 == 0
    }
    /** Checks that the values produced by this implementation match those of the "standard" ArrayCube implementation */
    private fun edgeOrientationsMatchExpected(cube: AnalyzableStandardCube): Boolean {
        val arrayPermutation = arrayCubeFactory.getCube(cube.getTiles()).getEdgeOrientationPermutation()
        return arrayPermutation.contentEquals(cube.getEdgeOrientationPermutation())
    }
    /** Checks that the values produced by this implementation match those of the "standard" ArrayCube implementation */
    private fun cornerOrientationsMatchExpected(cube: AnalyzableStandardCube): Boolean {
        val arrayPermutation = arrayCubeFactory.getCube(cube.getTiles()).getCornerOrientationPermutation()
        return arrayPermutation.contentEquals(cube.getCornerOrientationPermutation())
    }
    /** Checks that the values produced by this implementation match those of the "standard" ArrayCube implementation */
    private fun edgePositionsMatchExpected(cube: AnalyzableStandardCube): Boolean {
        val arrayPermutation = arrayCubeFactory.getCube(cube.getTiles()).getEdgePositionPermutation()
        return arrayPermutation.contentEquals(cube.getEdgePositionPermutation())
    }
    /** Checks that the values produced by this implementation match those of the "standard" ArrayCube implementation */
    private fun cornerPositionsMatchExpected(cube: AnalyzableStandardCube): Boolean {
        val arrayPermutation = arrayCubeFactory.getCube(cube.getTiles()).getCornerPositionPermutation()
        return arrayPermutation.contentEquals(cube.getCornerPositionPermutation())
    }

    @ParameterizedTest
    @Tag("CubeSimulationTest")
    @Order(6)
    @MethodSource("implementationFactories")
    fun stressTestMutableTwists(factory: CubeFactory) {
        val cube = factory.getSolvedCube()
        //performs 18 million twists using the no-copy twist implementation
        for(i in 0 until 1000000) {
            for(twist in Twist.values()) {
                cube.twistNoCopy(twist)
            }
        }
    }
    @ParameterizedTest
    @Tag("CubeSimulationTest")
    @Order(7)
    @MethodSource("implementationFactories")
    fun stressTestImmutableTwists() {
        var cube = factory.getSolvedCube()
        //performs 18 million twists using the copying twist implementation
        for(i in 0 until 1000000) {
            for(twist in Twist.values()) {
                cube = cube.twist(twist)
            }
        }
    }

    @ParameterizedTest
    @Order(8)
    @MethodSource("databaseWorthyFactories")
    fun stressTestMutableTwistAndHash(factory: AnalyzableStandardCubeFactory) {
        val cube = factory.getSolvedCube()
        //performs 18 million twists and corner hashes using the no-copy twist implementation
        for(i in 0 until 1000000) {
            for(twist in Twist.values()) {
                cube.twistNoCopy(twist)
                cornerPatternDatabase.getIndex(cube)
            }
        }
    }
    @ParameterizedTest
    @Order(9)
    @MethodSource("databaseWorthyFactories")
    fun stressTestImmutableTwistAndHash(factory: AnalyzableStandardCubeFactory) {
        var cube = factory.getSolvedCube()
        //performs 18 million twists and corner hashes using the copying twist implementation
        for(i in 0 until 1000000) {
            for(twist in Twist.values()) {
                cube = cube.twist(twist)
                cornerPatternDatabase.getIndex(cube)
            }
        }
    }

    companion object {

            val generalConfig: Config = ConfigFactory.load("patterndb.conf").getConfig("patterndb")
            val cornerConfig = generalConfig.getConfig("corners-full")

            val searchMode = cornerConfig.getString("search-mode")
            val persistenceMode = generalConfig.getString("persistence-mode")

            val jedis = Jedis()
            val key = cornerConfig.getString("redis-key")

            val file = File("data/corners-full-backup.db")

            val core = when(persistenceMode) {
                "file" -> FileCore(file)
                else -> RedisCore(jedis, key)
            }
        val cornerPatternDatabase = CornerPatternDatabase.create(core, searchMode, mutableListOf(0, 1, 2, 3, 4, 5, 6, 7))
        @JvmStatic
        private fun implementationFactories(): MutableList<CubeFactory> {
            return mutableListOf(ArrayCubeFactory(), SmartCubeFactory())
        }
        @JvmStatic
        private fun analyzableFactories(): MutableList<AnalyzableStandardCubeFactory> {
            return mutableListOf(ArrayCubeFactory(), SmartCubeFactory())
        }
        @JvmStatic
        private fun databaseWorthyFactories(): MutableList<AnalyzableStandardCubeFactory> {
            return mutableListOf(SmartCubeFactory())
        }
    }
}