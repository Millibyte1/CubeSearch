package com.millibyte1.cubesearch.solver

import com.millibyte1.cubesearch.cube.Cube
import com.millibyte1.cubesearch.cube.Twist
import com.millibyte1.cubesearch.util.*

import com.millibyte1.cubesearch.util.StandardCubeUtils.isSolved
import com.millibyte1.cubesearch.util.failNotSolvable

class IterativeDeepeningAStarSolver(costEvaluator: CostEvaluator<Cube>) : AbstractSolver<Cube>(costEvaluator) {
    @Throws(IllegalArgumentException::class)
    override fun getSolution(cube: Cube): Path {
        if(!SolvabilityUtils.isSolvable(cube)) throw failNotSolvable()
        var solution: Path?
        //searches for solutions with an iteratively deepening depth limit up to God's number
        for(depthLimit in 0..20) {
            solution = getSolution(PathWithBack(ArrayList(), cube), depthLimit)
            if(solution != null) return solution
        }

        throw failCouldNotSolve()
    }
    /** Performs an informed depth first search for the solution */
    private fun getSolution(path: PathWithBack, depthLimit: Int): Path? {
       if(isSolved(path.back)) return path.path
        var nextCube: Cube
        var solution: Path?

        val face1Previous = if(path.size() >= 1) Twist.getFace(path.path[path.size() - 1]) else null
        val face2Previous = if(path.size() >= 2) Twist.getFace(path.path[path.size() - 2]) else null
        //checks for solutions from taking each potentially effective move
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

/*
 * path: List<Twist>
 *
 * getSolution(cube: Cube):
 *    solution: List<Twist>?
 *    for(depthLimit in 0..20):
 *       solution = getSolution(PathWithBack(empty, cube), depthLimit)
 *       if(solution != null) return solution
 *    throw IllegalArgumentException
 *
 * getSolution(path: PathWithBack, depthLimit: Int): List<Twist>?
 *    if(isSolved(path.back)) return path.back
 *    for(twist in Twist.values):
 *       nextCube = path.back.twist(twist)
 *       if(getCost(nextCube) + path.size() <= depthLimit):
 *          solution = getSolution(path.add(twist), depthLimit)
 *          if(solution != null) return solution
 *    return null
 */