package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.Cube
import com.millibyte1.cubesearch.cube.CubeFactory
import com.millibyte1.cubesearch.cube.Twist
import com.millibyte1.cubesearch.util.*

import java.util.Queue
import java.util.ArrayDeque

import redis.clients.jedis.Jedis

object CornerPatternDatabase : AbstractPatternDatabase<Cube>() {

    internal const val CARDINALITY = 88179840

    private val jedis = Jedis()
    private val key = "cubesearch:corner-pattern-db"

    init {
        if(!isPopulated()) populateDatabase()
    }

    override fun getCost(index: Int): Byte {
        return jedis.hget(key, index.toString()).toByte()
    }

    override fun getIndex(cube: Cube): Int {
        //(maxOrientationIndex * positionIndex) + orientationIndex
        return (2187 * getCornerPositionIndex(cube)) + getCornerOrientationIndex(cube)
    }

    /** Checks whether or not the pattern database has been fully generated */
    internal fun isPopulated(): Boolean {
        return getPopulation() == CARDINALITY
    }
    /** Gets the number of entries in the pattern database */
    internal fun getPopulation(): Int {
        return jedis.hlen(key).toInt()
    }
    /**
     * Generates the pattern database to completion.
     * Should only require a partial BFS w/o heuristic pruning to depth 10 to generate all possible corner configurations.
     */
    private fun populateDatabase() {
        //constructs the queue for the BFS and enqueues the solved cube
        val queue: Queue<PathWithBack> = ArrayDeque()
        queue.add(PathWithBack(ArrayList(), CubeFactory().getSolvedCube()))
        //generates every possible corner configuration via a breadth-first traversal
        while(queue.isNotEmpty()) {
            //dequeues and inserts the cost into the pattern database
            val current = queue.remove()
            addCost(current.back, current.size().toByte())
            //uses a 2-move history to prune some twists resulting in cubes that can be generated in fewer moves
            val face1Previous = if(current.size() >= 1) Twist.getFace(current.path[current.size() - 1]) else null
            val face2Previous = if(current.size() >= 2) Twist.getFace(current.path[current.size() - 2]) else null
            //tries to expand off of each potentially viable twist
            for(twist in SolverUtils.getOptions(face1Previous, face2Previous)) {
                val nextCube = current.back.twist(twist)
                if(!databaseContains(nextCube)) queue.add(current.add(twist))
            }
        }
    }
    /** Checks whether this configuration is already in the pattern database */
    private fun databaseContains(cube: Cube): Boolean {
        return jedis.hexists(key, getIndex(cube).toString())
    }
    /** Adds the cost to the pattern database */
    private fun addCost(cube: Cube, cost: Byte) {
        jedis.hset(key, getIndex(cube).toString(), cost.toString())
    }

    /** Gets the Lehmer code of the corner permutation of this cube and converts it to base 10 */
    private fun getCornerPositionIndex(cube: Cube): Int {
        val lehmer = PatternDatabaseUtils.getLehmerCode(SolvabilityUtils.getCornerPermutation(cube))
        //multiplies the value at each index by its factoradic base
        return lehmer[0] * 5040 +
               lehmer[1] * 720 +
               lehmer[2] * 120 +
               lehmer[3] * 24 +
               lehmer[4] * 6 +
               lehmer[5] * 2 +
               lehmer[6]
    }
    /** Computes the corner orientation index by converting the orientation string to a base 10 number */
    private fun getCornerOrientationIndex(cube: Cube): Int {
        val orientations = getCornerOrientationSequence(cube)
        //ignores orientations[7] since there are only 7 degrees of choice
        return orientations[0] * 729 +
               orientations[1] * 243 +
               orientations[2] * 81 +
               orientations[3] * 27 +
               orientations[4] * 9 +
               orientations[5] * 3 +
               orientations[6]
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