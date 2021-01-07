package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.*
import com.millibyte1.cubesearch.util.*

import java.util.Queue
import java.util.ArrayDeque

import redis.clients.jedis.Jedis
object CornerPatternDatabase : AbstractPatternDatabase<SmartCube>() {

    internal const val CARDINALITY = 88179840
    internal const val ORIENTATION_CARDINALITY = 2187
    internal const val POSITION_CARDINALITY = 40320

    private const val USE_REDIS = false

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
        if(!USE_REDIS) {
            for (i in 0 until CARDINALITY) tempDatabase[i] = -1
            for (i in 0 until ORIENTATION_CARDINALITY) tempOrientationDatabase[i] = -1
            for (i in 0 until POSITION_CARDINALITY) tempPositionDatabase[i] = -1
        }
        populateDatabase()
        TODO("change of plan in how we generate this. First generate locally in an array," +
                "then split the database and insert into redis in parallel non-atomically. Should implement a caching scheme for " +
                "the pattern database look-ups to reduce the memory overhead of the cost evaluator." +
                "also should add a file-based implementation of persistence as an alternative to redis.")
    }

    override fun getCost(index: Int): Byte {
        if(USE_REDIS) return jedis.hget(key, index.toString()).toByte()
        return tempDatabase[index]
    }
    fun getOrientationCost(index: Int): Byte {
        if(USE_REDIS) return jedis.hget(orientationKey, index.toString()).toByte()
        return tempDatabase[index]
    }
    fun getPositionCost(index: Int): Byte {
        if(USE_REDIS) return jedis.hget(positionKey, index.toString()).toByte()
        return tempDatabase[index]
    }

    override fun getIndex(cube: SmartCube): Int {
        //(maxOrientationIndex * positionIndex) + orientationIndex
        return (2187 * getCornerPositionIndex(cube)) + getCornerOrientationIndex(cube)
    }
    fun getIndex(cube: Analyzable): Int {
        return (2187 * getCornerPositionIndex(cube)) + getCornerOrientationIndex(cube)
    }

    /** Checks whether or not the pattern database has been fully generated */
    internal fun isPopulated(): Boolean {
        return getPopulation() == CARDINALITY
    }
    /** Gets the number of entries in the pattern database */
    internal fun getPopulation(): Int {
        if(USE_REDIS) return jedis.hlen(key).toInt()
        return tempDatabase.fold(0) { total, item -> if(item.toInt() == -1) total else total + 1 }
    }
    internal fun getOrientationPopulation(): Int {
        if(USE_REDIS) return jedis.hlen(orientationKey).toInt()
        return tempOrientationDatabase.fold(0) { total, item -> if(item.toInt() == -1) total else total + 1 }
    }
    internal fun getPositionPopulation(): Int {
        if(USE_REDIS) return jedis.hlen(positionKey).toInt()
        return tempPositionDatabase.fold(0) { total, item -> if(item.toInt() == -1) total else total + 1 }
    }
    /**
     * Generates the pattern database to completion.
     * Should only require a partial BFS w/o heuristic pruning to depth 10 to generate all possible corner configurations.
     */
    private fun populateDatabase() {
        //constructs the queue for the BFS and enqueues the solved cube
        val queue: Queue<PathWithBack<SmartCube>> = ArrayDeque()
        queue.add(PathWithBack<SmartCube>(ArrayList(), SmartCubeFactory().getSolvedCube()))
        addCost(SmartCubeFactory().getSolvedCube(), 0)
        //generates every possible corner configuration via a breadth-first traversal
        while(queue.isNotEmpty()) {
            //dequeues the cube
            val current = queue.remove()
            //addCost(current.back, current.size().toByte())
            //uses a 2-move history to prune some twists resulting in cubes that can be generated in fewer moves
            val face1Previous = if(current.size() >= 1) Twist.getFace(current.path[current.size() - 1]) else null
            val face2Previous = if(current.size() >= 2) Twist.getFace(current.path[current.size() - 2]) else null
            //tries to expand off of each potentially viable twist
            for(twist in SolverUtils.getOptions(face1Previous, face2Previous)) {
                val nextCube = current.back.twist(twist)
                //if we haven't already encountered the cube, add to the database and to the expansion queue
                if(!databaseContains(nextCube)) {
                    queue.add(current.add(twist))
                    addCost(nextCube, (current.size() + 1).toByte())
                }
            }
        }
    }
    private fun queueContains(queue: Queue<PathWithBack<SmartCube>>, cube: SmartCube): Boolean {
        return queue.any { path -> path.back == cube }
    }
    /** Checks whether this configuration is already in the pattern database */
    private fun databaseContains(cube: SmartCube): Boolean {
        //return jedis.hexists(orientationKey, getCornerOrientationIndex(cube).toString()) &&
        //       jedis.hexists(positionKey, getCornerPositionIndex(cube).toString())
        if(USE_REDIS) return jedis.hexists(key, getIndex(cube).toString())
        return tempDatabase[getIndex(cube)].toInt() != -1
    }
    /** Adds the cost to the pattern database */
    private fun addCost(cube: SmartCube, cost: Byte) {
        if(USE_REDIS) {
            jedis.hset(key, getIndex(cube).toString(), cost.toString())
            jedis.hsetnx(orientationKey, getCornerOrientationIndex(cube).toString(), cost.toString())
            jedis.hsetnx(positionKey, getCornerPositionIndex(cube).toString(), cost.toString())
        }
        else {
            tempDatabase[getIndex(cube)] = cost
            val orientationVal = tempOrientationDatabase[getCornerOrientationIndex(cube)]
            val positionVal = tempPositionDatabase[getCornerPositionIndex(cube)]
            if (orientationVal.toInt() == -1) tempOrientationDatabase[getCornerOrientationIndex(cube)] = cost
            if (positionVal.toInt() == -1) tempPositionDatabase[getCornerPositionIndex(cube)] = cost
        }
        generated++
        if(generated % 100000 == 0) println(generated)
    }

    /** Gets the Lehmer code of the corner permutation of this cube and converts it to base 10 */
    fun getCornerPositionIndex(cube: Analyzable): Int {
        //val permutation = SolvabilityUtils.getCornerPermutation(cube)
        //val lehmer = PatternDatabaseUtils.getLehmerCode(permutation)
        //val lehmer = PatternDatabaseUtils.getLehmerCode(SolvabilityUtils.getCornerPermutation(cube))
        val lehmer = PatternDatabaseUtils.getLehmerCode(cube.getCornerPositionPermutation())
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
    fun getCornerOrientationIndex(cube: Analyzable): Int {
        //val orientations = getCornerOrientationSequence(cube)
        val orientations = cube.getCornerOrientationPermutation()
        //ignores orientations[7] since there are only 7 degrees of choice
        return orientations[0] * 729 +
               orientations[1] * 243 +
               orientations[2] * 81 +
               orientations[3] * 27 +
               orientations[4] * 9 +
               orientations[5] * 3 +
               orientations[6]
    }
    /*
    //TODO: get rid of
    /** Encodes the orientations of this cube's corners as a sequence of integers */
    private fun getCornerOrientationSequence(cube: ArrayCube): IntArray {
        val solvedCorners = ArrayCubeUtils.getSolvedCorners()
        val orientations = IntArray(8)
        var corner: CornerCubie
        for(i in 0 until 8) {
            corner = ArrayCubeUtils.getCubieOnCube(cube, solvedCorners[i]) as CornerCubie
            orientations[i] = SolvabilityUtils.getCornerOrientation(corner)
        }
        return orientations
    }
     */
}