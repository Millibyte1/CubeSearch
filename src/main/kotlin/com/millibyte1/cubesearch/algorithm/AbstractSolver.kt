package com.millibyte1.cubesearch.algorithm

import com.millibyte1.cubesearch.cube.AbstractCube
import com.millibyte1.cubesearch.algorithm.heuristics.CostEvaluator
import com.millibyte1.cubesearch.util.Path

/**
 * Abstract base implementation of the Solver interface.
 * Uses a composed CostEvaluator to evaluate the number of moves from the solved cube.
 * @param T the Cube type on which this Solver operates
 * @constructor Composes a CostEvaluator so that implementations only need to implement the search algorithm for the solver.
 */
abstract class AbstractSolver<T : AbstractCube<T>>(private val costEvaluator: CostEvaluator<T>) : Solver<T> {
    @Throws(IllegalArgumentException::class)
    override fun getCost(cube: T): Byte {
        return costEvaluator.getCost(cube)
    }

    /**
     * Gets the total estimated cost of a path of past moves and a candidate node
     */
    internal fun totalCost(path: Path, candidate: T): Int {
        return path.size + costEvaluator.getCost(candidate)
    }
}