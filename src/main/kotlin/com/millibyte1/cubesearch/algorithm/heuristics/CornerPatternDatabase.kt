package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.Cube
import com.millibyte1.cubesearch.util.CornerCubie
import com.millibyte1.cubesearch.util.PatternDatabaseUtils
import com.millibyte1.cubesearch.util.SolvabilityUtils
import com.millibyte1.cubesearch.util.StandardCubeUtils

object CornerPatternDatabase : AbstractPatternDatabase<Cube>() {

    private const val CARDINALITY = 88179840

    override fun getCost(index: Long): Byte {
        TODO("Not yet implemented")
    }

    override fun getIndex(cube: Cube): Long {
        //(maxOrientationIndex * positionIndex) + orientationIndex
        return (2187 * getCornerPositionIndex(cube)) + getCornerOrientationIndex(cube)
    }

    internal fun isPopulated(): Boolean {
        TODO("Not yet implemented")
    }

    //should only require a partial BFS w/o heuristic pruning to depth 10 to generate all possible corner configurations
    private fun populateDatabase() {
        TODO("Not yet implemented")
    }

    /** Gets the Lehmer code of the corner permutation of this cube and converts it to base 10 */
    private fun getCornerPositionIndex(cube: Cube): Long {
        val lehmer = PatternDatabaseUtils.getLehmerCode(SolvabilityUtils.getCornerPermutation(cube))
        //multiplies the value at each index by its factoradic base
        return (lehmer[0] * 5040).toLong() +
                (lehmer[1] * 720).toLong() +
                (lehmer[2] * 120).toLong() +
                (lehmer[3] * 24).toLong() +
                (lehmer[4] * 6).toLong() +
                (lehmer[5] * 2).toLong() +
                (lehmer[6]).toLong()
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
}