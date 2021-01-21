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

class CornerPatternDatabase(
    private val core: PatternDatabaseCore,
    private val searchMode: String = "dfs"
) : AbstractPatternDatabase() {

    internal val cardinality = 88179840

    private val factory = SmartCubeFactory()
    private var generated = 0
    private var tempDatabase = ByteArray(cardinality) { -1 }

    init {
        val bytes = core.readDatabase()
        //if the core is empty, populates the database and stores it in the core
        if(bytes == null) {
            //performs the search to populate the database
            when(searchMode) {
                "bfs" -> populateDatabaseBFS()
                "dfs" -> populateDatabaseDFS()
            }
            //stores the database in the core for persistent storage
            core.writeDatabase(tempDatabase)
        }
        //otherwise reads the database from the core
        else tempDatabase = bytes
    }

    override fun getCost(index: Int): Byte {
        return tempDatabase[index]
    }

    override fun getIndex(cube: AnalyzableStandardCube): Int {
        //(maxOrientationIndex * positionIndex) + orientationIndex
        return (2187 * getCornerPositionIndex(cube)) + getCornerOrientationIndex(cube)
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

    /** Performs a recursive DP-optimized DFS up to the given depth limit. */
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

    /** Gets the number of entries in the pattern database */
    internal fun getPopulation(): Int {
        return tempDatabase.fold(0) { total, item -> if(item.toInt() == -1) total else total + 1 }
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