package com.millibyte1.cubesearch.algorithm

import com.millibyte1.cubesearch.cube.AbstractCube
import com.millibyte1.cubesearch.cube.Twist
import com.millibyte1.cubesearch.algorithm.heuristics.CostEvaluator

/**
 * A simple interface wrapping an informed search algorithm on a Rubik's cube.
 * @param T the Cube type on which this Solver operates
 */
interface Solver<T : AbstractCube<T>> : CostEvaluator<T> {
    /**
     * Gets a valid solution to this Rubik's cube
     * @param cube the cube in question
     * @return a valid solution to [cube]
     * @throws IllegalArgumentException if this cube is unsolvable
     */
    @Throws(IllegalArgumentException::class)
    fun getSolution(cube: T): List<Twist>
}