package com.millibyte1.cubesearch.util

import com.millibyte1.cubesearch.cube.Cube
import com.millibyte1.cubesearch.cube.Twist
import com.millibyte1.cubesearch.cube.Twist.Face

import com.millibyte1.cubesearch.util.StandardCubeUtils.getCenters
import com.millibyte1.cubesearch.util.StandardCubeUtils.getCorners
import com.millibyte1.cubesearch.util.StandardCubeUtils.getCubieOnCube
import com.millibyte1.cubesearch.util.StandardCubeUtils.getCubies
import com.millibyte1.cubesearch.util.StandardCubeUtils.getEdges
import com.millibyte1.cubesearch.util.StandardCubeUtils.getSolvedCenters
import com.millibyte1.cubesearch.util.StandardCubeUtils.getSolvedCorners
import com.millibyte1.cubesearch.util.StandardCubeUtils.getSolvedCubie
import com.millibyte1.cubesearch.util.StandardCubeUtils.getSolvedCubies
import com.millibyte1.cubesearch.util.StandardCubeUtils.getSolvedEdges
import com.millibyte1.cubesearch.util.StandardCubeUtils.isOnFaces


/**
 * Collection of utilities for analyzing standard Rubik's cubes
 */
object SolvabilityUtils {

/* ============================================ SOLVABILITY FUNCTIONS =============================================== */

    /**
     * Checks whether it's possible to solve this Rubik's cube
     *
     * Accomplishes this by first checking that the stickers are placed correctly,
     * and then that the corners and edges are arranged and oriented in a solvable way.
     *
     * @param cube the cube in question
     * @return whether the cube can be solvable or not
     */
    fun isSolvable(cube: Cube): Boolean {
        return isCorrectlyStickered(cube) && passesParityTests(cube)
    }

    /**
     * Checks that the cube is correctly stickered
     * @param cube the cube in question
     * @return whether every cubie on a standard rubik's cube is accounted for and the centers are in the right places
     */
    fun isCorrectlyStickered(cube: Cube): Boolean {
        return centersAreCorrectlyPlaced(cube) && containsAllRealCubies(cube)
    }

    /**
     * Checks whether the center pieces on this cube are in the correct places
     * @param cube the cube in question
     * @return whether all center cubies are in the correct positions
     */
    fun centersAreCorrectlyPlaced(cube: Cube): Boolean {
        val centers = getCenters(cube)
        return getSolvedCenters().all { solved -> centers.any { unsolved -> unsolved == solved } }
    }

    /** Checks whether this cube contains all of the pieces present on a standard Rubik's cube */
    fun containsAllRealCubies(cube: Cube): Boolean {
        val cubies = getCubies(cube)
        return getSolvedCubies().all { solved -> cubies.any { unsolved -> unsolved.colorEquals(solved) } }
    }

    /**
     * Checks whether this cube passes all three parity tests
     * @param cube the cube in question
     * @return whether the cube passes all parity tests
     */
    fun passesParityTests(cube: Cube): Boolean {
        return (passesPermutationParityTest(cube) &&
                passesCornerParityTest(cube) &&
                passesEdgeParityTest(cube))
    }

    /**
     * Checks that the set of edges and the set of corners could both be solvable (but aren't necessarily)
     *
     * A cube passes the permutation parity test if the permutation parity of its edges equals the permutation parity of its corners.
     * Let S be the edges or the corners. If the total number of inversions in S is even, the permutation parity is even, else odd.
     *
     * Parity laws retrieved from: https://rosettacode.org/wiki/User:Arjun_sunel/Solvability_of_a_3x3x3_Rubik's_Cube_State%3F
     * @param cube the cube in question
     * @return whether the corners and edges can all be solved at the same time
     */
    fun passesPermutationParityTest(cube: Cube): Boolean {
        return (getCornerPermutationParity(cube) == getEdgePermutationParity(cube))
    }

    /**
     * Checks that the corners of this cube are in a solvable orientation.
     *
     * The corners are solvable if their orientation values sum to a number divisible by 3.
     *
     * Parity laws retrieved from: https://rosettacode.org/wiki/User:Arjun_sunel/Solvability_of_a_3x3x3_Rubik's_Cube_State%3F
     * @param cube the cube in question
     * @return whether all the corners of this cube are solvable
     */
    fun passesCornerParityTest(cube: Cube): Boolean {
        var orientationSum = 0
        for (cubie in getCorners(cube)) {
            val orientation = getCornerOrientation(cubie)
            orientationSum += orientation
        }
        return (orientationSum % 3 == 0)
    }

    /**
     * Checks that the edges of this cube are in a solvable orientation.
     *
     * The edges are solvable if their orientation values sum to a number divisible by 2.
     *
     * Parity laws retrieved from: https://rosettacode.org/wiki/User:Arjun_sunel/Solvability_of_a_3x3x3_Rubik's_Cube_State%3F
     * @param cube the cube in question
     * @return whether all the edges of this cube are solvable
     */
    fun passesEdgeParityTest(cube: Cube): Boolean {
        var orientationSum = 0
        for (cubie in getEdges(cube)) orientationSum += getEdgeOrientation(cubie, cube)
        return (orientationSum % 2 == 0)
    }

/* =============================================== HELPER FUNCTIONS ================================================= */
    /** Gets the orientation value of this corner cubie */
    internal fun getCornerOrientation(corner: CornerCubie): Int {
        var tile: Tile = getUpOrDownColoredTile(corner)
        if (tile.pos.face == Face.UP || tile.pos.face == Face.DOWN) return 0
        tile = getUpOrDownColoredTile(rotateCorner(corner))
        if (tile.pos.face == Face.UP || tile.pos.face == Face.DOWN) return 1
        return 2
    }

    /** Returns a clockwise rotation of the cubie with the given tiles */
    internal fun rotateCorner(cubie: CornerCubie): CornerCubie {
        //different corners have different orderings of tiles so we can't use the same swaps for every cubie
        return when {
            //tile 1=3, 2=1, 3=2
            isOnFaces(cubie, Face.FRONT, Face.LEFT, Face.UP) ||
                    isOnFaces(cubie, Face.FRONT, Face.RIGHT, Face.DOWN) ||
                    isOnFaces(cubie, Face.BACK, Face.RIGHT, Face.UP) ||
                    isOnFaces(cubie, Face.BACK, Face.LEFT, Face.DOWN) ->
                Cubie.makeCubie(Tile(cubie.tile1.pos, cubie.tile3.color),
                        Tile(cubie.tile2.pos, cubie.tile1.color),
                        Tile(cubie.tile3.pos, cubie.tile2.color)) as CornerCubie
            //tile 1=2, 2=3, 3=1
            isOnFaces(cubie, Face.FRONT, Face.RIGHT, Face.UP) ||
                    isOnFaces(cubie, Face.FRONT, Face.LEFT, Face.DOWN) ||
                    isOnFaces(cubie, Face.BACK, Face.LEFT, Face.UP) ||
                    isOnFaces(cubie, Face.BACK, Face.RIGHT, Face.DOWN) ->
                Cubie.makeCubie(Tile(cubie.tile1.pos, cubie.tile2.color),
                        Tile(cubie.tile2.pos, cubie.tile3.color),
                        Tile(cubie.tile3.pos, cubie.tile1.color)) as CornerCubie
            else -> throw IllegalArgumentException("Error: Invalid CornerCubie. Must lie on 3 adjacent faces")
        }
    }

    /** Returns the flipped image of the edge cubie with the given tiles */
    internal fun flipEdge(cubie: EdgeCubie): EdgeCubie {
        return Cubie.makeCubie(Tile(cubie.tile1.pos, cubie.tile2.color),
                Tile(cubie.tile2.pos, cubie.tile1.color)) as EdgeCubie
    }

    /** Gets the orientation value of this cubie */
    internal fun getEdgeOrientation(edge: EdgeCubie, cube: Cube): Int {
        //Generates the sets of moves we can use that won't flip the orientation of the cubie (no front/back twists)
        val xTwists = arrayListOf(Twist.LEFT_90, Twist.LEFT_180, Twist.LEFT_270,
                Twist.RIGHT_90, Twist.RIGHT_180, Twist.RIGHT_270, null)
        val yTwists = arrayListOf(Twist.UP_90, Twist.UP_180, Twist.UP_270,
                Twist.DOWN_90, Twist.DOWN_180, Twist.DOWN_270, null)
        val solvedEdge = getSolvedCubie(edge) as EdgeCubie

        //brute-forces the cubie into the correct position and checks whether it's correctly oriented
        var newEdge: EdgeCubie
        var firstCube: Cube
        var secondCube: Cube
        var thirdCube: Cube
        for (twist1 in xTwists) {
            firstCube = if (twist1 != null) cube.twist(twist1) else cube
            for (twist2 in yTwists) {
                secondCube = if (twist2 != null) firstCube.twist(twist2) else firstCube
                for (twist3 in xTwists) {
                    thirdCube = if (twist3 != null) secondCube.twist(twist3) else secondCube
                    newEdge = getCubieOnCube(thirdCube, edge) as EdgeCubie
                    if (newEdge == solvedEdge) return 0
                    if (flipEdge(newEdge) == solvedEdge) return 1
                }
            }
        }
        for (twist1 in yTwists) {
            firstCube = if (twist1 != null) cube.twist(twist1) else cube
            for (twist2 in xTwists) {
                secondCube = if (twist2 != null) firstCube.twist(twist2) else firstCube
                for (twist3 in yTwists) {
                    thirdCube = if (twist3 != null) secondCube.twist(twist3) else secondCube
                    newEdge = getCubieOnCube(thirdCube, edge) as EdgeCubie
                    if (newEdge == solvedEdge) return 0
                    if (flipEdge(newEdge) == solvedEdge) return 1
                }
            }
        }
        return 1
    }

    /**
     * Gets the tile on this corner cubie that has the same color as either the up or down center
     * @param corner the corner cubie in question
     * @return the tile with the same color as the up or down center
     * @throws IllegalArgumentException if this cubie has no up or down colored tile (impossible for a valid corner cubie)
     */
    @Throws(IllegalArgumentException::class)
    private fun getUpOrDownColoredTile(corner: CornerCubie): Tile {
        return when {
            (corner.tile1.color == 4 || corner.tile1.color == 5) -> corner.tile1
            (corner.tile2.color == 4 || corner.tile2.color == 5) -> corner.tile2
            (corner.tile3.color == 4 || corner.tile3.color == 5) -> corner.tile3
            else -> throw IllegalArgumentException("Cubie has no up or down colored tile")
        }
    }

    /**
     * Gets the tile on this edge cubie that has the same color as either the up or down center
     * @param edge the edge cubie in question
     * @return the tile with the same color as the up or down center
     * @throws IllegalArgumentException if this cubie has no up or down colored tile (does not mean this edge is invalid)
     */
    @Throws(IllegalArgumentException::class)
    private fun getUpOrDownColoredTile(edge: EdgeCubie): Tile {
        return when {
            (edge.tile1.color == 4 || edge.tile1.color == 5) -> edge.tile1
            (edge.tile2.color == 4 || edge.tile2.color == 5) -> edge.tile2
            else -> throw IllegalArgumentException("Cubie has no up or down colored tile")
        }
    }

    /**
     * Gets the tile on this edge cubie that has the same color as either the front or back center
     * @param edge the edge cubie in question
     * @return the tile with the same color as the front or back center
     * @throws IllegalArgumentException If this cubie has no front or back colored tile (does not mean this edge is invalid)
     */
    @Throws(IllegalArgumentException::class)
    private fun getFrontOrBackColoredTile(edge: EdgeCubie): Tile {
        return when {
            (edge.tile1.color == 0 || edge.tile1.color == 1) -> edge.tile1
            (edge.tile2.color == 0 || edge.tile2.color == 1) -> edge.tile2
            else -> throw IllegalArgumentException("Cubie has no front or back colored tile")
        }
    }

    /** Determines the position of this cubie or its rotations in the provided "sorted" list of cubies */
    private fun cubieNumber(cubie: Cubie, solved: List<Cubie>): Int {
        for (i in solved.indices) if (solved[i].colorEquals(cubie)) return i
        return -1
    }

    /** Gets the boolean parity of the set of corners on this cube */
    private fun getCornerPermutationParity(cube: Cube): Boolean {
        val current = getCorners(cube)
        val solved = getSolvedCorners()
        var inversions = 0
        //count inversions in currentCorners
        for (i in current.indices) {
            for (j in i + 1 until current.size) {
                //if current[j] comes before current[i], it's an inversion
                if (cubieNumber(current[j], solved) < cubieNumber(current[i], solved)) inversions++
            }
        }
        return (inversions % 2 != 0)
    }

    /** Gets the boolean parity of the set of edges on this cube */
    private fun getEdgePermutationParity(cube: Cube): Boolean {
        val current = getEdges(cube)
        val solved = getSolvedEdges()
        var inversions = 0
        //count inversions in currentCorners
        for (i in current.indices) {
            for (j in i + 1 until current.size) {
                //if current[j] comes before current[i], it's an inversion
                if (cubieNumber(current[j], solved) < cubieNumber(current[i], solved)) inversions++
            }
        }
        return (inversions % 2 != 0)
    }

}