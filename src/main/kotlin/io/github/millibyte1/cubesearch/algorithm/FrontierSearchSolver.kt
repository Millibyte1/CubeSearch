package io.github.millibyte1.cubesearch.algorithm

import io.github.millibyte1.cubesearch.cube.Twist
import io.github.millibyte1.cubesearch.algorithm.heuristics.CostEvaluator
import io.github.millibyte1.cubesearch.cube.AnalyzableStandardCube
import io.github.millibyte1.cubesearch.util.*

import io.github.millibyte1.cubesearch.util.ArrayCubeUtils.isSolved
import io.github.millibyte1.cubesearch.util.failNotSolvable

import java.util.concurrent.PriorityBlockingQueue

/** Implements the frontier search algorithm, which is like A* but without a table of already explored nodes */
class FrontierSearchSolver(costEvaluator: CostEvaluator) : io.github.millibyte1.cubesearch.algorithm.AbstractSolver(costEvaluator) {

    private val candidates: PriorityBlockingQueue<PathWithBack> = PriorityBlockingQueue(100, Comparator {
        path1: PathWithBack, path2: PathWithBack -> when {
            path1.size() + getCost(path1.back) < path2.size() + getCost(path2.back) -> -1
            path1.size() + getCost(path1.back) == path2.size() + getCost(path2.back) -> 0
            else -> 1
        }
    })

    @Synchronized
    @Throws(IllegalArgumentException::class)
    override fun getSolution(cube: AnalyzableStandardCube): Path {
        if(!SolvabilityUtils.isSolvable(cube)) throw failNotSolvable()
        reset()

        //puts the initial cube into the candidates queue
        candidates.add(PathWithBack(ArrayList(), cube))

        while(candidates.isNotEmpty()) {
            //updates local variables
            val path = candidates.remove()
            val currentCube = path.back
            val face1Previous = if(path.size() >= 1) Twist.getFace(path.path[path.size() - 1]) else null
            val face2Previous = if(path.size() >= 2) Twist.getFace(path.path[path.size() - 2]) else null

            //if the current cube is solved, returns the path that led to it
            if(isSolved(currentCube)) return path.path

            //otherwise, go through all the potential successors and enqueue the good ones
            for(twist in SolverUtils.getOptions(face1Previous, face2Previous)) {
                val nextCube = currentCube.twist(twist)

                //if it's impossible for this move to produce an optimal solution, skip it
                if(path.size() + getCost(nextCube) > MAX_DEPTH) continue

                //inserts good successors into candidates queue
                candidates.add(path.add(twist))
            }

        }
        throw failCouldNotSolve()
    }

    private fun reset() {
        candidates.clear()
    }

    companion object {
        private const val MAX_DEPTH = 20
    }

}