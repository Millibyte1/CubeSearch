package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.Cube
import com.millibyte1.cubesearch.cube.CubeFactory
import com.millibyte1.cubesearch.cube.Twist
import com.millibyte1.cubesearch.util.Cubie
import com.millibyte1.cubesearch.util.SolvabilityUtils
import com.millibyte1.cubesearch.util.StandardCubeUtils

object ManhattanDistanceCostEvaluator : CostEvaluator<Cube> {

    private val solved = CubeFactory().getSolvedCube()

    private val cubieManhattanDistances: MutableMap<Cubie, Int> = HashMap()

    init {
        initializeManhattanDistances()
    }

    /**
     * Performant and admissible heuristic for a standard Rubik's cube. Generalizes the concept of Manhattan distance to cubies.
     * Evaluates to either the sum of corner distances divided by 4 or the sum of edge distances divided by 4, whichever is larger.
     *
     * Uses a pre-generated lookup table of oriented cubie positions to 3D Manhattan distances from their solved positions.
     *
     * O(1) worst-case time complexity.
     *
     * @param cube the cube we want to analyze
     * @return the 3D Manhattan distance of the cube
     * @throws IllegalArgumentException if this cube is improperly stickered
     */
    @Throws(IllegalArgumentException::class)
    override fun getCost(cube: Cube): Byte {
        return when {
            SolvabilityUtils.isCorrectlyStickered(cube) ->
                (Integer.max(
                        (StandardCubeUtils.getCorners(cube)
                                .map { corner -> cubieManhattanDistances[corner] } as List<Int>)
                                .reduce { sum, distance -> sum + distance },
                        (StandardCubeUtils.getEdges(cube)
                                .map { edge -> cubieManhattanDistances[edge] } as List<Int>)
                                .reduce { sum, distance -> sum + distance }
                ) / 4).toByte()
            else -> throw failInvalidCubies()
        }
    }

    /** Initializes the Manhattan distances of each cubie possible in the solved group */
    private fun initializeManhattanDistances() {
        val twists = setOf(null, *Twist.values())
        //Only 3 moves are needed to reach any possible position and orientation of a particular cubie
        for(twist1 in twists) {
            val cube1 = if(twist1 != null) solved.twist(twist1) else solved
            for(twist2 in twists) {
                val cube2 = if(twist2 != null) cube1.twist(twist2) else cube1
                for(twist3 in twists) {
                    val cube3 = if(twist3 != null) cube2.twist(twist3) else cube2
                    for(cubie in StandardCubeUtils.getCubies(cube3)) {
                        val moves = (twist1 != null).toInt() + (twist2 != null).toInt() + (twist3 != null).toInt()
                        if(cubieManhattanDistances[cubie] == null) cubieManhattanDistances[cubie] = moves
                    }
                }
            }
        }
    }
}

private fun Boolean.toInt() = if(this) 1 else 0

private fun failInvalidCubies(): IllegalArgumentException {
    return IllegalArgumentException("Error: cube contains invalid cubies")
}