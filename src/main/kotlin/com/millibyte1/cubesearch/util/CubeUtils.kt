package com.millibyte1.cubesearch.util

import com.millibyte1.cubesearch.cube.Cube
import com.millibyte1.cubesearch.cube.Twist

/**
 * Collection of utilities for analyzing standard Rubik's cubes
 */

/**
 * Generalization of Manhattan distance to a 3D Rubik's cube.
 * Computes the distance in moves of every cubie from its solved position and returns the sum divided by 8.
 * Uses a pre-generated lookup table of oriented cubie positions to 3D Manhattan distances from their solved positions.
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
 * Accomplishes this by first checking that the stickers are placed correctly,
 * and then that the corners and edges are oriented/twisted in a solvable way.
 *
 * @param cube the cube we want to analyze
 * @return whether the cube can be solvable or not
 */
fun isSolvable(cube: Cube): Boolean {
    return (isCorrectlyStickered(cube)) &&
           (cornersAreSolvable(cube)) &&
           (edgesAreSolvable(cube))
}

/**
 * Checks that all stickers that exist on a valid standard Rubik's cube are accounted for
 */
internal fun isCorrectlyStickered(cube: Cube): Boolean {
    if(!centerCubiesAreCorrectlyStickered(cube)) return false
    //the number of stickers of each color/value that haven't been found on the cube yet
    val remaining = IntArray(6) { 9 }
    for(i in 0 until 6) for(j in 0 until 9) remaining[cube.data[i][j]]--
    return remaining.all { i -> i == 0 }
}

/**
 * Checks that there is exactly 1 center cubie of each color
 */
internal fun centerCubiesAreCorrectlyStickered(cube: Cube): Boolean {
    val remaining = IntArray(6) { 1 }
    for(i in 0 until 6) remaining[cube.data[i][4]]--
    return remaining.all { i -> i == 0 }
}
internal fun cornersAreSolvable(cube: Cube): Boolean {
    return false //TODO
}
internal fun edgesAreSolvable(cube: Cube): Boolean {
    return false //TODO
}

