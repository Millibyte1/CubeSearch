package com.millibyte1.cubesearch.solver

import com.millibyte1.cubesearch.cube.Cube

import com.millibyte1.cubesearch.util.*

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

    /** Computes the corner index by separately computing the position index and orientation index */
    private fun getCornerIndex(cube: Cube): Long {
        return (2187 * getCornerPositionIndex(cube)) + getCornerOrientationIndex(cube)
    }
    /** Computes the edge index by separately computing the position index and orientation index */
    private fun getEdgeIndex(cube: Cube): Long {
        return (2048 * getEdgePositionIndex(cube)) + getEdgeOrientationIndex(cube)
    }

    /** Gets the Lehmer code of the corner permutation of this cube and converts it to base 10 */
    private fun getCornerPositionIndex(cube: Cube): Long {
        val lehmer = getLehmerCode(SolvabilityUtils.getCornerPermutation(cube))
        //multiplies the value at each index by its factoradic base
        return (lehmer[0] * 5040).toLong() +
               (lehmer[1] * 720).toLong() +
               (lehmer[2] * 120).toLong() +
               (lehmer[3] * 24).toLong() +
               (lehmer[4] * 6).toLong() +
               (lehmer[5] * 2).toLong() +
               (lehmer[6]).toLong()
    }
    /** Gets the Lehmer code of the edge permutation of this cube and converts it to base 10 */
    private fun getEdgePositionIndex(cube: Cube): Long {
        val lehmer = getLehmerCode(SolvabilityUtils.getEdgePermutation(cube))
        //multiplies the value at each index by its factoradic base
        return (lehmer[0] * 39916800).toLong() +
               (lehmer[1] * 3628800).toLong() +
               (lehmer[2] * 362880).toLong() +
               (lehmer[3] * 40320).toLong() +
               (lehmer[4] * 5040).toLong() +
               (lehmer[5] * 720).toLong() +
               (lehmer[6] * 120).toLong() +
               (lehmer[7] * 24).toLong() +
               (lehmer[8] * 6).toLong() +
               (lehmer[9] * 2).toLong() +
               (lehmer[10]).toLong()
    }
    /** Computes the lehmer code of the permutation by counting the number of inversions at each index */
    private fun getLehmerCode(permutation: IntArray): IntArray {
        val lehmerSequence = IntArray(permutation.size)
        var inversions: Int
        for(i in permutation.indices) {
            inversions = 0
            //counts the number of inversions at index i
            for(j in i+1 until permutation.size) if(permutation[i] > permutation[j]) inversions++
            //sets the value of lehmer[i] to be the number of inversions
            lehmerSequence[i] = inversions
        }
        return lehmerSequence
    }
    /** Computes the corner orientation index by converting the orientation string to a base 10 number */
    private fun getCornerOrientationIndex(cube: Cube): Long {
        val orientations = getCornerOrientationSequence(cube)
        //ignores orientations[7] since there are only 7 degrees of choice
        return (orientations[0] * 729).toLong() +
               (orientations[1] * 243).toLong() +
               (orientations[2] * 81).toLong() +
               (orientations[3] * 27).toLong() +
               (orientations[4] * 9).toLong() +
               (orientations[5] * 3).toLong() +
               (orientations[6]).toLong()
    }
    /** Computes the edge orientation index by converting the orientation string to a base 10 number */
    private fun getEdgeOrientationIndex(cube: Cube): Long {
        val orientations = getEdgeOrientationSequence(cube)
        //ignores orientations[11] since there are only 11 degrees of choice
        return (orientations[0] * 1024).toLong() +
               (orientations[1] * 512).toLong() +
               (orientations[2] * 256).toLong() +
               (orientations[3] * 128).toLong() +
               (orientations[4] * 64).toLong() +
               (orientations[5] * 32).toLong() +
               (orientations[6] * 16).toLong() +
               (orientations[7] * 8).toLong() +
               (orientations[8] * 4).toLong() +
               (orientations[9] * 2).toLong() +
               (orientations[10]).toLong()
    }
    /** Encodes the orientations of this cube's corners as a sequence of integers */
    private fun getCornerOrientationSequence(cube: Cube): IntArray {
        val solvedCorners = StandardCubeUtils.getSolvedCorners()
        val orientations = IntArray(8)
        var corner: CornerCubie
        for(i in 0 until 8) {
            corner = StandardCubeUtils.getCubieOnCube(cube, solvedCorners[i]) as CornerCubie
            orientations[i] = SolvabilityUtils.getCornerOrientation(corner)
        }
        return orientations
    }
    /** Encodes the orientations of this cube's edges as a sequence of integers */
    private fun getEdgeOrientationSequence(cube: Cube): IntArray {
        val solvedEdges = StandardCubeUtils.getSolvedEdges()
        val orientations = IntArray(12)
        var edge: EdgeCubie
        for(i in 0 until 12) {
            edge = StandardCubeUtils.getCubieOnCube(cube, solvedEdges[i]) as EdgeCubie
            orientations[i] = SolvabilityUtils.getEdgeOrientation(edge, cube)
        }
        return orientations
    }
}