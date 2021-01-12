package com.millibyte1.cubesearch.algorithm

import com.millibyte1.cubesearch.cube.StandardCube
import com.millibyte1.cubesearch.cube.Twist
import com.millibyte1.cubesearch.algorithm.heuristics.CostEvaluator
import com.millibyte1.cubesearch.cube.AnalyzableStandardCube

/**
 * A simple interface wrapping an informed search algorithm on a Rubik's cube.
 */
interface Solver {
    /**
     * Gets a valid solution to this Rubik's cube
     * @param cube the cube in question
     * @return a valid solution to [cube]
     * @throws IllegalArgumentException if this cube is unsolvable
     */
    @Throws(IllegalArgumentException::class)
    fun getSolution(cube: AnalyzableStandardCube<*>): List<Twist>
}