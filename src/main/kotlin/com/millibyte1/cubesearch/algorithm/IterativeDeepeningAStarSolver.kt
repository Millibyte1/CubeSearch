package com.millibyte1.cubesearch.algorithm

import com.millibyte1.cubesearch.cube.Twist
import com.millibyte1.cubesearch.algorithm.heuristics.CostEvaluator
import com.millibyte1.cubesearch.cube.AnalyzableStandardCube

import com.millibyte1.cubesearch.util.*
import com.millibyte1.cubesearch.util.ArrayCubeUtils.isSolved
import com.millibyte1.cubesearch.util.failNotSolvable

class IterativeDeepeningAStarSolver(costEvaluator: CostEvaluator, val usesBestFirstSearch: Boolean = true) : AbstractSolver(costEvaluator) {
    @Throws(IllegalArgumentException::class)
    override fun getSolution(cube: AnalyzableStandardCube): Path {
        if(!SolvabilityUtils.isSolvable(cube)) throw failNotSolvable()
        var solution: Path?
        //searches for solutions with an iteratively deepening depth limit up to God's number
        for(depthLimit in 0..20) {
            solution = when(usesBestFirstSearch) {
                true -> getSolutionBestFirst(PathWithBack(ArrayList(), cube), depthLimit)
                false -> getSolution(PathWithBack(ArrayList(), cube), depthLimit)
            }
            if(solution != null) return solution
        }
        throw failCouldNotSolve()
    }
    /** Performs an informed depth first search for the solution */
    private fun getSolution(path: PathWithBack, depthLimit: Int): Path? {
       if(isSolved(path.back)) return path.path
        //uses a 2-move history to prune some twists that aren't viable and create an "options" list
        val face1Previous = if(path.size() >= 1) Twist.getFace(path.path[path.size() - 1]) else null
        val face2Previous = if(path.size() >= 2) Twist.getFace(path.path[path.size() - 2]) else null
        //searches for solutions by taking each potentially viable move
        for(twist in SolverUtils.getOptions(face1Previous, face2Previous)) {
            val nextCube = path.back.twist(twist)
            //if it's possible for this move to produce a solution below the depth limit, try it
            if(getCost(nextCube) + path.size() <= depthLimit) {
                val solution = getSolution(path.add(twist), depthLimit)
                if(solution != null) return solution
            }
        }
        return null
    }

    /** Performs an informed depth first search for the solution, choosing the best successor paths first */
    private fun getSolutionBestFirst(path: PathWithBack, depthLimit: Int): Path? {
        if(isSolved(path.back)) return path.path

        //uses a 2-move history to prune some twists that aren't viable and create an "options" list
        val face1Previous = if(path.size() >= 1) Twist.getFace(path.path[path.size() - 1]) else null
        val face2Previous = if(path.size() >= 2) Twist.getFace(path.path[path.size() - 2]) else null

        //gets a list of all the potentially viable successors, sorted by estimated cost
        val sortedSuccessors = getSortedSuccessors(path, face1Previous, face2Previous, depthLimit)

        //searches for solutions by taking each potentially viable move
        for(successor in sortedSuccessors) {
            val solution = getSolutionBestFirst(successor.first, depthLimit)
            if(solution != null) return solution
        }
        return null
    }

    /** Gets a list of successor paths sorted by the estimated cost of the back cube ascending */
    private inline fun getSortedSuccessors(
        path: PathWithBack,
        face1Previous: Twist.Face?,
        face2Previous: Twist.Face?,
        depthLimit: Int
    ): ArrayList<Pair<PathWithBack, Byte>> {
        val sortedSuccessors = ArrayList<Pair<PathWithBack, Byte>>(18)

        //goes through each of the candidate successors that remain after non-heuristic pruning
        for(twist in SolverUtils.getOptions(face1Previous, face2Previous)) {
            val nextPath = path.add(twist)
            val cost = getCost(nextPath.back)
            //prunes some candidates with the heuristic before inserting
            if(cost + path.size() <= depthLimit) insertSorted(Pair(nextPath, cost), sortedSuccessors)
        }
        return sortedSuccessors
    }
}
/**
 * Inserts the element at the appropriate spot in a list sorted by cost ascending
 * @param element the (path, cost) pair to insert into a list of candidates
 * @param list the candidate list
 */
private inline fun insertSorted(element: Pair<PathWithBack, Byte>, list: MutableList<Pair<PathWithBack, Byte>>) {
    //Since the array is very small, linear search is actually faster than binary search
    for(i in list.indices) {
        if(element.second <= list[i].second) {
            list.add(i, element)
            return
        }
    }
    list.add(element)
}