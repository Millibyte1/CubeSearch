package com.millibyte1.cubesearch.solver

import com.millibyte1.cubesearch.cube.Cube
import com.millibyte1.cubesearch.cube.Twist

class IterativeDeepeningAStarSolver(costEvaluator: CostEvaluator<Cube>) : AbstractSolver<Cube>(costEvaluator) {
    @Throws(IllegalArgumentException::class)
    override fun getSolution(cube: Cube): List<Twist> {
        TODO("Not yet implemented")
    }
}