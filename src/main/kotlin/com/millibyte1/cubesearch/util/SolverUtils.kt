package com.millibyte1.cubesearch.util

import com.millibyte1.cubesearch.cube.Cube
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
    return (isCorrectlyStickered(cube)) &&
           (passesPermutationParityTest(cube)) &&
           (passesCornerParityTest(cube)) &&
           (passesEdgeParityTest(cube))
}

/**
 * Checks that all stickers that exist on a valid standard Rubik's cube are accounted for and all the center pieces are valid
 * @param cube the cube in question
 * @return whether the cube is correctly stickered
 */
internal fun isCorrectlyStickered(cube: Cube): Boolean {
    if(!centerCubiesAreCorrectlyStickered(cube)) return false
    //the number of stickers of each color/value that haven't been found on the cube yet
    val remaining = IntArray(6) { 9 }
    for(i in 0 until 6) for(j in 0 until 9) remaining[cube.data[i][j]]--
    return remaining.all { i -> i == 0 }
    //TODO replace the hack with one that actually checks each cubie is present
}

/**
 * Checks that there is exactly 1 center cubie of each color
 */
internal fun centerCubiesAreCorrectlyStickered(cube: Cube): Boolean {
    val remaining = IntArray(6) { 1 }
    for(i in 0 until 6) remaining[cube.data[i][4]]--
    return remaining.all { i -> i == 0 }
}

/**
 * Checks that the set of edges and the set of corners could both be solvable (but aren't necessarily)
 *
 * A cube passes the permutation parity test if the permutation parity of its edges equals the permutation parity of its corners.
 * Let S be the edges or the corners. If the total number of inversions in S is even, the permutation parity is even, else odd.
 *
 * Parity laws retrieved from: https://rosettacode.org/wiki/User:Arjun_sunel/Solvability_of_a_3x3x3_Rubik's_Cube_State%3F
 * @param cube the cube in question
 * @return whether the corners and edges have the same permutation parity
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
    var upCubie: CornerCubie
    var downCubie: CornerCubie
    var orientationSum: Int = 0
    //computes the sum of the orientations of every cubie
    for(i in arrayOf(0, 2, 6, 8)) {
        upCubie = getCubie(cube, Tile(Face.UP, i, cube.data[4][i])) as CornerCubie
        downCubie = getCubie(cube, Tile(Face.DOWN, i, cube.data[5][i])) as CornerCubie
        orientationSum += getCornerOrientation(upCubie)
        orientationSum += getCornerOrientation(downCubie)
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
internal fun passesEdgeParityTest(cube: Cube): Boolean {
    var orientationSum: Int = 0
    var cubie1: EdgeCubie
    var cubie2: EdgeCubie
    //for each edge on the top and bottom
    for(i in arrayOf(1, 3, 5, 7)) {
        cubie1 = getCubie(cube, Tile(Face.UP, i, cube.data[4][i])) as EdgeCubie
        cubie2 = getCubie(cube, Tile(Face.DOWN, i, cube.data[5][i])) as EdgeCubie
        orientationSum += getEdgeOrientation(cubie1)
        orientationSum += getEdgeOrientation(cubie2)
    }
    //for each remaining edge on the front and back
    for(i in arrayOf(3, 5)) {
        cubie1 = getCubie(cube, Tile(Face.FRONT, i, cube.data[0][i])) as EdgeCubie
        cubie2 = getCubie(cube, Tile(Face.BACK, i, cube.data[1][i])) as EdgeCubie
        orientationSum += getEdgeOrientation(cubie1)
        orientationSum += getEdgeOrientation(cubie2)
    }
    return (orientationSum % 2 == 0)
}

internal fun getCornerOrientation(corner: CornerCubie): Int {
    var tile: Tile = getUpOrDownColoredTile(corner)
    if(tile.face == Face.UP || tile.face == Face.DOWN) return 0
    tile = getUpOrDownColoredTile(rotateCorner(corner))
    if(tile.face == Face.UP || tile.face == Face.DOWN) return 1
    return 2
}

internal fun getEdgeOrientation(edge: EdgeCubie): Int {
    var tile: Tile
    if(isOnFace(edge, Face.UP) || isOnFace(edge, Face.DOWN)) {
        tile = getUpOrDownColoredTile(edge)
        if(tile.face == Face.UP || tile.face == Face.DOWN) return 0
    }
    else if(isOnFace(edge, Face.FRONT) || isOnFace(edge, Face.BACK)) {
        tile = getFrontOrBackColoredTile(edge)
        if(tile.face == Face.FRONT || tile.face == Face.DOWN) return 0
    }
    return 1
}
/**
 * Gets the tile on the cubie that has the same color as either the up or down face
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

/**
 * Rotates corner 90 degrees clockwise
 */
@Throws(IllegalArgumentException::class)
private fun rotateCorner(corner: CornerCubie): CornerCubie {
    return when {
        isOnFaces(corner, Face.UP, Face.RIGHT, Face.FRONT) ->
            rotateCorner(CornerCubie(getTileOnFace(corner, Face.UP),
                                     getTileOnFace(corner, Face.RIGHT),
                                     getTileOnFace(corner, Face.FRONT)))
        isOnFaces(corner, Face.UP, Face.BACK, Face.RIGHT) ->
            rotateCorner(CornerCubie(getTileOnFace(corner, Face.UP),
                                     getTileOnFace(corner, Face.BACK),
                                     getTileOnFace(corner, Face.RIGHT)))
        isOnFaces(corner, Face.UP, Face.LEFT, Face.BACK) ->
            rotateCorner(CornerCubie(getTileOnFace(corner, Face.UP),
                                     getTileOnFace(corner, Face.LEFT),
                                     getTileOnFace(corner, Face.BACK)))
        isOnFaces(corner, Face.UP, Face.FRONT, Face.LEFT) ->
            rotateCorner(CornerCubie(getTileOnFace(corner, Face.UP),
                                     getTileOnFace(corner, Face.FRONT),
                                     getTileOnFace(corner, Face.LEFT)))
        isOnFaces(corner, Face.DOWN, Face.FRONT, Face.RIGHT) ->
            rotateCorner(CornerCubie(getTileOnFace(corner, Face.DOWN),
                                     getTileOnFace(corner, Face.FRONT),
                                     getTileOnFace(corner, Face.RIGHT)))
        isOnFaces(corner, Face.DOWN, Face.RIGHT, Face.BACK) ->
            rotateCorner(CornerCubie(getTileOnFace(corner, Face.DOWN),
                                     getTileOnFace(corner, Face.RIGHT),
                                     getTileOnFace(corner, Face.BACK)))
        isOnFaces(corner, Face.DOWN, Face.BACK, Face.LEFT) ->
            rotateCorner(CornerCubie(getTileOnFace(corner, Face.DOWN),
                                     getTileOnFace(corner, Face.BACK),
                                     getTileOnFace(corner, Face.LEFT)))
        isOnFaces(corner, Face.DOWN, Face.LEFT, Face.FRONT) ->
            rotateCorner(CornerCubie(getTileOnFace(corner, Face.DOWN),
                                     getTileOnFace(corner, Face.LEFT),
                                     getTileOnFace(corner, Face.FRONT)))
        else -> throw IllegalArgumentException("Invalid cubie")
    }
}

/**
 * Pre: tiles are listed in a clockwise ordering
 */
private fun rotateCorner(tile1: Tile, tile2: Tile, tile3: Tile): CornerCubie {
    return CornerCubie(Tile(tile1.face, tile1.index, tile3.color),
                       Tile(tile2.face, tile2.index, tile1.color),
                       Tile(tile3.face, tile3.index, tile2.color))
}

private fun getCornerPermutationParity(cube: Cube): Boolean {
    return false
}
private fun getEdgePermutationParity(cube: Cube): Boolean {
    return false
}