package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.ArrayCube
import com.millibyte1.cubesearch.cube.ArrayCubeFactory
import com.millibyte1.cubesearch.cube.Twist
import com.millibyte1.cubesearch.util.*

import java.util.Queue
import java.util.ArrayDeque

import redis.clients.jedis.Jedis

object CornerPatternDatabase : AbstractPatternDatabase<ArrayCube>() {

    internal const val CARDINALITY = 88179840
    internal const val ORIENTATION_CARDINALITY = 2187
    internal const val POSITION_CARDINALITY = 40320

    private var generated = 0

    private val jedis = Jedis()
    private val key = "cubesearch:patterndb.corner.main"
    private val orientationKey = "cubesearch:patterndb.corner.orientation"
    private val positionKey = "cubesearch:patterndb.corner.position"

    private val tempDatabase = ByteArray(CARDINALITY) { -1 }
    private val tempOrientationDatabase = ByteArray(ORIENTATION_CARDINALITY) { -1 }
    private val tempPositionDatabase = ByteArray(POSITION_CARDINALITY) { -1 }

    init {
        //if(!isPopulated()) populateDatabase()
        //for(i in 0 until CARDINALITY) tempDatabase[i] = -1
        //for(i in 0 until ORIENTATION_CARDINALITY) tempOrientationDatabase[i] = -1
        //for(i in 0 until  POSITION_CARDINALITY) tempPositionDatabase[i] = -1
        populateDatabase()
    }

    override fun getCost(index: Int): Byte {
        return jedis.hget(key, index.toString()).toByte()
        //return tempDatabase[index]
    }

    override fun getIndex(cube: ArrayCube): Int {
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
        //return tempDatabase.fold(0) { total, item -> if(item.toInt() == -1) total else total + 1 }
    }
    internal fun getOrientationPopulation(): Int {
        return jedis.hlen(orientationKey).toInt()
        //return tempOrientationDatabase.fold(0) { total, item -> if(item.toInt() == -1) total else total + 1 }
    }
    internal fun getPositionPopulation(): Int {
        return jedis.hlen(positionKey).toInt()
        //return tempPositionDatabase.fold(0) { total, item -> if(item.toInt() == -1) total else total + 1 }
    }
    /**
     * Generates the pattern database to completion.
     * Should only require a partial BFS w/o heuristic pruning to depth 10 to generate all possible corner configurations.
     */
    private fun populateDatabase() {
        //constructs the queue for the BFS and enqueues the solved cube
        val queue: Queue<PathWithBack> = ArrayDeque()
        queue.add(PathWithBack(ArrayList(), ArrayCubeFactory().getSolvedCube()))
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
    private fun queueContains(queue: Queue<PathWithBack>, cube: ArrayCube): Boolean {
        return queue.any { path -> path.back == cube }
    }
    /** Checks whether this configuration is already in the pattern database */
    private fun databaseContains(cube: ArrayCube): Boolean {
        return jedis.hexists(key, getIndex(cube).toString())
        //return tempDatabase[getIndex(cube)].toInt() != -1
        //return jedis.hexists(orientationKey, getCornerOrientationIndex(cube).toString()) &&
        //       jedis.hexists(positionKey, getCornerPositionIndex(cube).toString())
    }
    /** Adds the cost to the pattern database */
    private fun addCost(cube: ArrayCube, cost: Byte) {
        jedis.hset(key, getIndex(cube).toString(), cost.toString())
        //jedis.hsetnx(orientationKey, getCornerOrientationIndex(cube).toString(), cost.toString())
        //jedis.hsetnx(positionKey, getCornerPositionIndex(cube).toString(), cost.toString())
        //tempDatabase[getIndex(cube)] = cost
        //val orientationVal = tempOrientationDatabase[getCornerOrientationIndex(cube)]
        //val positionVal = tempPositionDatabase[getCornerPositionIndex(cube)]
        //if(orientationVal.toInt() == -1) tempOrientationDatabase[getCornerOrientationIndex(cube)] = cost
        //if(positionVal.toInt() == -1) tempPositionDatabase[getCornerPositionIndex(cube)] = cost
        generated++
        if(generated % 100000 == 0) println(generated)
    }

    /** Gets the Lehmer code of the corner permutation of this cube and converts it to base 10 */
    private fun getCornerPositionIndex(cube: ArrayCube): Int {
        //val permutation = SolvabilityUtils.getCornerPermutation(cube)
        //val lehmer = PatternDatabaseUtils.getLehmerCode(permutation)
        val lehmer = PatternDatabaseUtils.getLehmerCode(SolvabilityUtils.getCornerPermutation(cube))
        //multiplies the value at each index by its factoradic place value
        return lehmer[0] * 5040 +
               lehmer[1] * 720 +
               lehmer[2] * 120 +
               lehmer[3] * 24 +
               lehmer[4] * 6 +
               lehmer[5] * 2 +
               lehmer[6]
    }
    /** Computes the corner orientation index by converting the orientation string to a base 10 number */
    private fun getCornerOrientationIndex(cube: ArrayCube): Int {
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
    private fun getCornerOrientationSequence(cube: ArrayCube): IntArray {
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