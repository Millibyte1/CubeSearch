package com.millibyte1.cubesearch.util

import com.millibyte1.cubesearch.cube.Cube
import com.millibyte1.cubesearch.cube.CubeFactory
import com.millibyte1.cubesearch.cube.Twist
import com.millibyte1.cubesearch.cube.Twist.Face

/**
 * Collection of utilities for analyzing standard Rubik's cubes
 */


/**
 * A pre-generated map from cubies to the distance from their solved positions.
 */
private var cubieManhattanDistances: Map<out Cubie, Int> = HashMap<Cubie, Int>()

/**
 * Fills the manhattan distance lookup table
 */
internal fun populateManhattanDistances() {
    //TODO
}

/**
 * Generalization of Manhattan distance to a 3D Rubik's cube.
 * Computes the distance in moves of every cubie from its solved position and returns the sum divided by 8.
 *
 * Uses a pre-generated lookup table of oriented cubie positions to 3D Manhattan distances from their solved positions.
 *
 * Performant and admissible heuristic for a 3x3x3 Rubik's cube.
 *
 * @param cube the cube we want to analyze
 * @return the 3D Manhattan distance of the cube
 */
fun getManhattanDistance(cube: Cube): Int {
    return -1 //TODO
}

/* ============================================ SOLVABILITY FUNCTIONS =============================================== */

/**
 * Returns whether it's possible to solve this Rubik's cube.
 *
 * Accomplishes this by first checking that the stickers are placed correctly,
 * and then that the corners and edges are oriented/twisted in a solvable way.
 *
 * @param cube the cube we want to analyze
 * @return whether the cube can be solvable or not
 */
fun isSolvable(cube: Cube): Boolean {
    return isCorrectlyStickered(cube) && passesParityTests(cube)
}
/**
 * Checks that the cube is correctly stickered
 * @param cube the cube in question
 * @return whether every cubie on a standard rubik's cube is accounted for
 */
fun isCorrectlyStickered(cube: Cube): Boolean {
    val cubies = getCubies(cube)
    return getSolvedCubies().all { solved -> cubies.any { unsolved -> unsolved.colorEquals(solved) } }
}
/**
 * Returns whether this cube passes every parity test
 * @param cube the cube we want to analyze
 * @return whether the cube passes all the parity tests
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
internal fun passesPermutationParityTest(cube: Cube): Boolean {
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
internal fun passesCornerParityTest(cube: Cube): Boolean {
    var orientationSum: Int = 0
    for(cubie in getCorners(cube)) orientationSum += getCornerOrientation(cubie)
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
internal fun passesEdgeParityTest(cube: Cube): Boolean {
    var orientationSum: Int = 0
    for(cubie in getEdges(cube)) orientationSum += getEdgeOrientation(cubie)
    return (orientationSum % 2 == 0)
}

/** Gets the orientation value of this cubie */
internal fun getCornerOrientation(corner: CornerCubie): Int {
    var tile: Tile = getUpOrDownColoredTile(corner)
    if(tile.pos.face == Face.UP || tile.pos.face == Face.DOWN) return 0
    tile = getUpOrDownColoredTile(rotateCorner(corner))
    if(tile.pos.face == Face.UP || tile.pos.face == Face.DOWN) return 1
    return 2
}
/** Returns a clockwise rotation of the cubie with the given tiles */
private fun rotateCorner(cubie: CornerCubie): CornerCubie {
    return Cubie.makeCubie(Tile(cubie.tile1.pos, cubie.tile3.color),
            Tile(cubie.tile2.pos, cubie.tile1.color),
            Tile(cubie.tile3.pos, cubie.tile2.color)) as CornerCubie
}
/** Gets the orientation value of this cubie */
internal fun getEdgeOrientation(edge: EdgeCubie): Int {
    var tile: Tile
    if(isOnFace(edge, Face.UP) || isOnFace(edge, Face.DOWN)) {
        tile = getUpOrDownColoredTile(edge)
        if(tile.pos.face == Face.UP || tile.pos.face == Face.DOWN) return 0
    }
    else if(isOnFace(edge, Face.FRONT) || isOnFace(edge, Face.BACK)) {
        tile = getFrontOrBackColoredTile(edge)
        if(tile.pos.face == Face.FRONT || tile.pos.face == Face.DOWN) return 0
    }
    return 1
}

/**
 * Gets the tile on the cubie that has the same color as either the up or down face
 *
 * Assumes that the corner cubie is a valid cubie
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
 * Gets the tile on the cubie that has the same color as either the up or down face
 *
 * Assumes that the edge cubie is a valid cubie
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
 * Gets the tile on the cubie that has the same color as either the front or back face
 * Assumes that the edge cubie is a valid cubie
 */
@Throws(IllegalArgumentException::class)
private fun getFrontOrBackColoredTile(edge: EdgeCubie): Tile {
    return when {
        (edge.tile1.color == 0 || edge.tile1.color == 1) -> edge.tile1
        (edge.tile2.color == 0 || edge.tile2.color == 1) -> edge.tile2
        else -> throw IllegalArgumentException("Cubie has no front or back colored tile")
    }
}

private fun getCornerPermutationParity(cube: Cube): Boolean {
    //TODO
    return false
}
private fun getEdgePermutationParity(cube: Cube): Boolean {
    //TODO
    return false
}