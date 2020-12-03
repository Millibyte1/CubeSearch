package com.millibyte1.cubesearch.solver

import com.millibyte1.cubesearch.cube.Cube

//TODO idea: implement a bidirectional search using IDA* w/ pattern database in the towards solved direction and frontier w/ manhattan distance in the from solved
/*
 * For a cube with a solvable corner configuration and/or edge configuration, there are:
 * 8! ways to choose the positions of its corners,
 * 3^7 ways to choose the orientations of its corners,
 * 12! ways to choose the positions of its edges,
 * 2^11 ways to choose the orientations of its edges.
 *
 * It would thus require a minimum of:
 * 27 bits to index all solvable corner configurations,
 * 40 bits to index all solvable edge configurations.
 *
 * We can encode configurations as integers by separately indexing positions and orientations.
 * We can easily encode corner orientations as a 7-digit base-3 string, and edge orientations as an 11-digit binary string.
 * While less obvious, we can also easily encode the positions as integers using Lehmer coding (factorial-base system for indexing permutations).
 * We then arrive at a minimal indexing using the formulas:
 * cornerIndex = (3^7 * cornerPositionIndex) + cornerOrientationIndex
 * edgeIndex = (2^11 * edgePositionIndex) + edgeOrientationIndex
 *
 */
object PatternDatabaseCostEvaluator : CostEvaluator<Cube> {

    override fun getCost(cube: Cube): Int {
        TODO("Not yet implemented")
    }

    private fun getCornerIndex(cube: Cube): Long {
        TODO()
    }
    private fun getEdgeIndex(cube: Cube): Long {
        TODO()
    }
    private fun getCornerPositionIndex(cube: Cube): Long {
        TODO()
    }
    private fun getEdgePositionIndex(cube: Cube): Long {
        TODO()
    }
    private fun getCornerOrientationIndex(cube: Cube): Long {
        TODO()
    }
    private fun getEdgeOrientationIndex(cube: Cube): Long {
        TODO()
    }
}