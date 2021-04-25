package com.millibyte1.cubesearch.algorithm

import com.millibyte1.cubesearch.algorithm.heuristics.CostEvaluator
import com.millibyte1.cubesearch.cube.AnalyzableStandardCube
import com.millibyte1.cubesearch.util.Path

/**
 * Abstract base implementation of the [Solver] interface.
 * Uses a composed [CostEvaluator] to evaluate the number of moves from the solved cube.
 * @constructor Composes a [CostEvaluator] so that implementations only need to implement the search algorithm for the solver.
 */
abstract class AbstractSolver(private val costEvaluator: CostEvaluator) : Solver, CostEvaluator {
    @Throws(IllegalArgumentException::class)
    override fun getCost(cube: AnalyzableStandardCube): Byte {
        return costEvaluator.getCost(cube)
    }

    /** Gets the total estimated cost of a [path] of past moves and a [candidate] node */
    internal fun totalCost(path: Path, candidate: AnalyzableStandardCube): Int {
        return path.size + costEvaluator.getCost(candidate)
    }
}