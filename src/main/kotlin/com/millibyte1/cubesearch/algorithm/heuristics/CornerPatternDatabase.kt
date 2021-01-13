package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.*
import com.millibyte1.cubesearch.util.*

import java.util.Queue
import java.util.ArrayDeque

import redis.clients.jedis.Jedis

import java.io.File
import java.io.IOException
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

import org.apache.commons.io.FileUtils

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

//TODO: implement full orientation and position databases

object CornerPatternDatabase : AbstractPatternDatabase() {

    internal const val CARDINALITY = 88179840

    private val generalConfig: Config = ConfigFactory.load("patterndb.conf").getConfig("patterndb")
    private val cornerConfig = generalConfig.getConfig("corners-full")

    private val searchMode = cornerConfig.getString("search-mode")
    private val persistenceMode = generalConfig.getString("persistence-mode")

    private val factory = SmartCubeFactory()

    private var generated = 0

    private val jedis = Jedis()
    private val key = cornerConfig.getString("redis-key")

    private val file = File("data/corners-full.db")

    private var tempDatabase = ByteArray(CARDINALITY) { -1 }

    init {
        //if the database has already been populated, load it into memory from the persistent store
        if(isPopulated()) {
            when(persistenceMode) {
                "file" -> tempDatabase = FileUtils.readFileToByteArray(file)
            }
        }
        //otherwise goes through the process of populating it
        else {
            //performs the search to populate the database
            when(searchMode) {
                "bfs" -> populateDatabaseBFS()
                "dfs" -> populateDatabaseDFS()
                //"iddfs" -> populateDatabaseIDDFS()
            }
            //saves the contents of the populated database into the appropriate persistent store
            when(persistenceMode) {
                "redis" -> for(i in 0 until CARDINALITY) jedis.hset(key, i.toString(), tempDatabase[i].toString())
                "file" -> writeDatabaseToFile()
            }
        }
    }

    private fun writeDatabaseToFile() {
        try {
            val fileStream = FileOutputStream(file)
            fileStream.write(tempDatabase)
            fileStream.close()
        }
        catch(e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getCost(index: Int): Byte {
        return tempDatabase[index]
    }

    override fun getIndex(cube: AnalyzableStandardCube): Int {
        //(maxOrientationIndex * positionIndex) + orientationIndex
        return (2187 * getCornerPositionIndex(cube)) + getCornerOrientationIndex(cube)
    }

    /** Checks whether or not the pattern database has been fully generated */
    internal fun isPopulated(): Boolean {
        return getPopulation() == CARDINALITY
    }
    /** Gets the number of entries in the pattern database */
    internal fun getPopulation(): Int {
        return when(persistenceMode) {
            "redis" -> jedis.hlen(key).toInt()
            "file" -> {
                return when {
                    file.exists() -> FileUtils.readFileToByteArray(file).size
                    else -> 0
                }
            }
            else -> tempDatabase.fold(0) { total, item -> if(item.toInt() == -1) total else total + 1 }
        }
    }

    /**
     * Generates the pattern database to completion.
     * Should only require a partial BFS w/o to depth 11 to generate all possible corner configurations.
     */
    private fun populateDatabaseBFS() {
        //constructs the queue for the BFS and enqueues the solved cube
        val queue: Queue<PathWithBack> = ArrayDeque()
        queue.add(PathWithBack(ArrayList(), factory.getSolvedCube()))
        addCost(factory.getSolvedCube(), 0)
        //generates every possible corner configuration via a breadth-first traversal
        while(queue.isNotEmpty()) {
            //dequeues the cube
            val current = queue.remove()
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

    /**
     * A more memory efficient method of generating the pattern database. Performs a complete depth-first traversal
     * from the solved cube to depth 11.
     * Should have a similar runtime to the BFS because a closed list (the database) is still viable to maintain.
     * Not guaranteed to encounter the best paths first like the BFS, however, so there's some slowdown.
     */
    private fun populateDatabaseDFS() {
        populateDatabaseDFS(PathWithBack(ArrayList(), factory.getSolvedCube()), 11)
    }

    /*
    /**
     * Strictly less performant than the DFS both in memory and runtime, but asymptotically the same.
     * Performs a complete depth first traversal from the solved cube to each depth up to 11.
     */
    private fun populateDatabaseIDDFS() {
        //generates the pattern database via limited depth first traversals to every depth up to 11
        for(i in 0..11) {
            println("Now performing DFS at depth $i")
            // builds a closed list for this search; unlike the BFS we can't just use the patterndb
            val closedList = ByteArray(CARDINALITY) { -1 }
            //populates the closed list
            populateDatabaseDFS(PathWithBack(ArrayList(), factory.getSolvedCube()), closedList, i)
            //pushes every real element of the closed list to the pattern database
            pushClosedListToDatabase(closedList)
            println("Finished performing DFS at depth $i. Real database size: ${getRealSizeOfTempDatabase()}")
        }
    }
     */
    /**
     * Performs a recursive DP-optimized DFS up to the given depth limit.
     * This search function is used by both the DFS and IDDFS modes.
     */
    private fun populateDatabaseDFS(path: PathWithBack, depthLimit: Int) {

        val current = path.back
        val currentDepth = path.size()
        val index = getIndex(current)
        //short circuits if we've already encountered a cube with this corner configuration at this low a depth
        if(tempDatabase[index].toInt() != -1 && tempDatabase[index].toInt() <= currentDepth) return
        //adds this configuration to the database
        addCost(index, currentDepth.toByte())

        //if we're not at the depth limit, try more twists
        if(currentDepth < depthLimit) {
            //uses a 2-move history to prune some twists resulting in cubes that can be generated in fewer moves
            val face1Previous = if(path.size() >= 1) Twist.getFace(path.path[path.size() - 1]) else null
            val face2Previous = if(path.size() >= 2) Twist.getFace(path.path[path.size() - 2]) else null
            //tries to expand off of each potentially viable twist
            for (twist in SolverUtils.getOptions(face1Previous, face2Previous)) {
                populateDatabaseDFS(path.add(twist), depthLimit)
            }
        }
    }

    private fun getRealSizeOfTempDatabase(): Int {
        return tempDatabase
            .filter { value -> value.toInt() != -1 }
            .size
    }
    /** For every hit (every item that isn't -1) in the closed list, push it into the database */
    private fun pushClosedListToDatabase(closedList: ByteArray) {
        for(i in 0 until CARDINALITY) if(closedList[i].toInt() != -1) addCost(i, closedList[i])
    }
    /** Checks whether this configuration is already in the pattern database */
    private fun databaseContains(cube: AnalyzableStandardCube): Boolean {
        return tempDatabase[getIndex(cube)].toInt() != -1
    }
    /** Adds the cost to the pattern database */
    private fun addCost(cube: AnalyzableStandardCube, cost: Byte) {
        tempDatabase[getIndex(cube)] = cost
        generated++
        if(generated % 1000000 == 0) println(generated)
    }
    /** Adds the cost to the pattern database */
    private fun addCost(index: Int, cost: Byte) {
        tempDatabase[index] = cost
        generated++
        if(generated % 1000000 == 0) println(generated)
    }

    /** Gets the Lehmer code of the corner permutation of this cube and converts it to base 10 */
    private fun getCornerPositionIndex(cube: AnalyzableStandardCube): Int {
        val lehmer = PatternDatabaseUtils.getLehmerCode(cube.getCornerPositionPermutation())
        //multiplies the value at each index by its factoradic place value. only 7 degrees of choice so ignore lehmer[7]
        return lehmer[0] * 5040 +
               lehmer[1] * 720 +
               lehmer[2] * 120 +
               lehmer[3] * 24 +
               lehmer[4] * 6 +
               lehmer[5] * 2 +
               lehmer[6]
    }
    /** Computes the corner orientation index by converting the orientation string to a base 10 number */
    private fun getCornerOrientationIndex(cube: AnalyzableStandardCube): Int {
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
}