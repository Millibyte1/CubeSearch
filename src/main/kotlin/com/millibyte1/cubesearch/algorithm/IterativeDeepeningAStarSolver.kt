package com.millibyte1.cubesearch.algorithm

import com.millibyte1.cubesearch.cube.ArrayCube
import com.millibyte1.cubesearch.cube.Twist
import com.millibyte1.cubesearch.algorithm.heuristics.CostEvaluator

import com.millibyte1.cubesearch.util.*
import com.millibyte1.cubesearch.util.ArrayCubeUtils.isSolved
import com.millibyte1.cubesearch.util.failNotSolvable

class IterativeDeepeningAStarSolver(costEvaluator: CostEvaluator<ArrayCube>) : AbstractSolver<ArrayCube>(costEvaluator) {
    @Throws(IllegalArgumentException::class)
    override fun getSolution(cube: ArrayCube): Path {
        if(!SolvabilityUtils.isSolvable(cube)) throw failNotSolvable()
        var solution: Path?
        //searches for solutions with an iteratively deepening depth limit up to God's number
        for(depthLimit in 0..20) {
            solution = getSolution(PathWithBack<ArrayCube>(ArrayList(), cube), depthLimit)
            if(solution != null) return solution
        }
        throw failCouldNotSolve()
    }
    /** Performs an informed depth first search for the solution */
    private fun getSolution(path: PathWithBack<ArrayCube>, depthLimit: Int): Path? {
       if(isSolved(path.back)) return path.path
        var nextCube: ArrayCube
        var solution: Path?
        //uses a 2-move history to prune some twists that aren't viable and create an "options" list
        val face1Previous = if(path.size() >= 1) Twist.getFace(path.path[path.size() - 1]) else null
        val face2Previous = if(path.size() >= 2) Twist.getFace(path.path[path.size() - 2]) else null
        //searches for solutions by taking each potentially viable move
        for(twist in SolverUtils.getOptions(face1Previous, face2Previous)) {
            nextCube = path.back.twist(twist)
            //if it's possible for this move to produce a solution below the depth limit, try it
            if(getCost(nextCube) + path.size() <= depthLimit) {
                solution = getSolution(path.add(twist), depthLimit)
                if(solution != null) return solution
            }
        }
        return null
    }
}