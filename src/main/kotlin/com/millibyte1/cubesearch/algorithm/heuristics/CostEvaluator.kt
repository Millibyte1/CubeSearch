package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.StandardCube

/**
 * A simple interface wrapping a cost function for use in informed search algorithms on Rubik's cubes.
 * @param T the Cube type on which this CostEvaluator operates
 */
@FunctionalInterface
interface CostEvaluator<T : StandardCube<T>> {
    /**
     * An estimate of the distance from the solved cube
     * @param cube the cube in question
     * @return the estimated distance of [cube] from the solved cube
     * @throws IllegalArgumentException if the cost cannot be evaluated (e.g. if the cube is incorrectly stickered)
     */
    @Throws(IllegalArgumentException::class)
    fun getCost(cube: T): Byte
}