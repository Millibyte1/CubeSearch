package com.millibyte1.cubesearch.util

import com.millibyte1.cubesearch.algorithm.heuristics.AbstractPatternDatabase
import com.millibyte1.cubesearch.cube.AnalyzableStandardCube
import com.millibyte1.cubesearch.cube.SmartCubeFactory
import com.millibyte1.cubesearch.cube.Twist
import java.util.*
import kotlin.collections.ArrayList

//TODO idea: bidirectional search using IDA* w/ pattern database in the towards solved direction and frontier search w/ manhattan distance in the from solved
/*
 * For a cube with a solvable corner configuration and/or edge configuration, there are:
 * 8! ways to choose the positions of its corners,
 * 3^7 ways to choose the orientations of its corners,
 * 12! ways to choose the positions of its edges,
 * 2^11 ways to choose the orientations of its edges.
 *
 * There are
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
 * Since we have the cardinality of the sets of all edge configurations and all corner configurations, we know we must
 * explore at minimum all cubes up to depth log_15(CARDINALITY) without heuristic pruning. This amounts to a minimum of:
 * optimisticDepth = log_15(8! * 3^7) = ~6.75 for the corner database,
 * optimisticDepth = log_15(12! * 2^11) = ~10.2 for the full edge database.
 *
 * In reality these depths are much higher. It takes a search up to depth 11 to populate the corner database, and the depth required
 * to populate the edge database is unknown.
 *
 * This is feasible with a simple breadth-first search for the corner database, but not for the edge database.
 * There may or may not be some algorithm which could exhaustively traverse up to this depth in a realistic time.
 *
 * TODO: Implement queue that spills over to disk so that we can perform BFS instead of DFS for any size database
 * TODO: Explore the idea of using a massively parallel BFS with infrequent pushes to the server and continuous atomic updates on the server
 * TODO: Use Apache Kafka? Hadoop?
 *
 * For now, we must split the edge database into multiple pattern databases each considering k edges.
 * Choosing the positions of k out of 12 edges is equivalent to choosing the order of the k fastest runners in a race of 12 contestants.
 * We then have a full k degrees of choice for choosing the edge orientations.
 * So we have (2^k) * 12!/(12-k)! possible configurations of k particular edges, which evaluates to:
 * 1961990600000 possible configurations of the full 12-edge database, which is equivalent to ~22250 corner databases,
 * 980995276800 possible configurations of an 11-edge database, which is equivalent to ~11125 corner databases,
 * 245248819200 possible configurations of a 10-edge database, which is equivalent to ~2781 corner databases,
 * 40874803200 possible configurations of a 9-edge database, which is equivalent to ~463.5 corner databases,
 * 5109350400 possible configurations of an 8-edge database, which is equivalent to ~58 corner databases,
 * 510935040 possible configurations of a 7-edge database, which is equivalent to ~5.8 corner databases,
 * 42577920 possible configurations of a 6-edge database, which is equivalent to ~0.5 corner databases.
 *
 * Assuming that each entry in a pattern database takes up 9 bytes (64-bit hash-key, 8-bit value), we get:
 * ~0.739GiB space for the corner database,
 * ~16TiB space for the full 12-edge database,
 * ~8TiB space for an 11-edge database,
 * ~2TiB space for a 10-edge database,
 * ~343GiB space for a 9-edge database,
 * ~42.9GiB space for an 8-edge database,
 * ~4.29GiB space for a 7-edge database,
 * ~0.37GiB space for a 6-edge database.
 *
 * Assuming each machine in a cluster can dedicate 8GiB of RAM to an in-memory database, we would need:
 * a cluster of ~2048 machines to implement the full 12-edge database,
 * a cluster of ~1024 machines to implement an 11-edge database,
 * a cluster of ~256 machines to implement a 10-edge database,
 * a cluster of ~43 machines to implement a 9-edge database,
 * a cluster of ~6 machines to implement an 8-edge database,
 * and only a single machine for smaller databases.
 *
 * It requires 33 bits to index an 8-edge database. If we implemented a custom 33-bit key, 4-bit value data structure, the 8-edge
 * database would require only 19.6GiB, which is practical on a small cluster or on a single machine with 32 Gigs of RAM.
 */
object PatternDatabaseUtils {
    /**
     * Computes the lehmer code of a full or partial permutation in linear time.
     *
     * A full permutation is one where the set of values is equal to the set of indices (e.g. in a race with persons 1-9,
     * which place did each person finish in?). A partial permutation is one where the set of values differs. (e.g. in a race
     * with persons 1-9, which place did persons 1-4 finish in?)
     *
     * @param permutation the full or partial permutation.
     * @param n the size of the full permutation if $permutation$ is only a partial ranking.
     *
     * @return The lehmer encoding of [permutation].
     *
     * For each index i in [permutation], lehmer_i is equal to permutation_i minus the number of values occurring to the
     * left of i which are greater than permutation_i.
     */
    fun getLehmerCode(permutation: IntArray, n: Int = permutation.size): IntArray {
        val lehmerSequence = permutation.copyOf()
        //a lookup for whether a given value has already been encountered in the permutation
        val seen = BooleanArray(n) { false }
        for(i in permutation.indices) {
            //marks that we have seen this value
            seen[permutation[i]] = true
            //subtracts the number of inversions, which is equal to the number of already seen values less than the current item's value
            lehmerSequence[i] -= onesInFirstKBits(seen, permutation[i])
        }
        return lehmerSequence
    }
    /**
     * Computes the lehmer code of a full or partial permutation in linear time.
     *
     * A full permutation is one where the set of values is equal to the set of indices (e.g. in a race with persons 1-9,
     * which place did each person finish in?). A partial permutation is one where the set of values differs. (e.g. in a race
     * with persons 1-9, which place did persons 1-4 finish in?)
     *
     * @param permutation the full or partial permutation.
     * @param n the size of the full permutation if $permutation$ is only a partial ranking.
     *
     * @return The lehmer encoding of [permutation].
     *
     * For each index i in [permutation], lehmer_i is equal to permutation_i minus the number of values occurring to the
     * left of i which are greater than permutation_i.
     */
    fun getLehmerCode(permutation: List<Int>, n: Int = permutation.size): IntArray {
        val lehmerSequence = permutation.toIntArray()
        //a lookup for whether a given value has already been encountered in the permutation
        val seen = BooleanArray(n) { false }
        for(i in permutation.indices) {
            //marks that we have seen this value
            seen[permutation[i]] = true
            //subtracts the number of inversions, which is equal to the number of already seen values less than the current item's value
            lehmerSequence[i] -= onesInFirstKBits(seen, permutation[i])
        }
        return lehmerSequence
    }

    /*
    /** Computes the lehmer code of the permutation by counting the number of inversions at each index */
    fun getLehmerCode(permutation: IntArray): IntArray {
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
    /** Computes the lehmer code of the permutation by counting the number of inversions at each index */
    fun getLehmerCode(permutation: List<Int>): IntArray {
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
     */

    /** Gets the number of entries in the pattern database */
    fun getPopulation(table: ByteArray): Int {
        return table.fold(0) { total, item -> if(item.toInt() == -1) total else total + 1 }
    }

    /** Generates the pattern database to completion via a DP-optimized BFS */
    fun populateDatabaseBFS(table: ByteArray, patternDB: AbstractPatternDatabase, factory: SmartCubeFactory) {
        //constructs the queue for the BFS and enqueues the solved cube
        val queue: Queue<PathWithBack> = ArrayDeque()
        queue.add(PathWithBack(ArrayList(), factory.getSolvedCube()))
        addCost(factory.getSolvedCube(), 0, table, patternDB)
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
                if(!databaseContains(nextCube, table, patternDB)) {
                    queue.add(current.add(twist))
                    addCost(nextCube, (current.size() + 1).toByte(), table, patternDB)
                }
            }
        }
    }

    fun populateDatabaseIDDFS(path: PathWithBack, table: ByteArray, patternDB: AbstractPatternDatabase) {
        for(depthLimit in 0 until 11) {
            val fakeTable = table.copyOf()
            populateDatabaseDFS(path, depthLimit, fakeTable, patternDB)
            val population = getPopulation(fakeTable)
            if(population == patternDB.getCardinality()) {
                for(i in 0 until population) table[i] = fakeTable[i]
                break
            }
        }
    }
    /** Performs a recursive DP-optimized DFS up to the given depth limit. */
    fun populateDatabaseDFS(path: PathWithBack, depthLimit: Int, table: ByteArray, patternDB: AbstractPatternDatabase) {

        val current = path.back
        val currentDepth = path.size()
        val index = patternDB.getIndex(current)
        //short circuits if we've already encountered a cube with this corner configuration at this low a depth
        if(table[index].toInt() != -1 && table[index].toInt() <= currentDepth) return
        //adds this configuration to the database
        addCost(index, currentDepth.toByte(), table)

        //if we're not at the depth limit, try more twists
        if(currentDepth < depthLimit) {
            //uses a 2-move history to prune some twists resulting in cubes that can be generated in fewer moves
            val face1Previous = if(path.size() >= 1) Twist.getFace(path.path[path.size() - 1]) else null
            val face2Previous = if(path.size() >= 2) Twist.getFace(path.path[path.size() - 2]) else null
            //tries to expand off of each potentially viable twist
            for (twist in SolverUtils.getOptions(face1Previous, face2Previous)) {
                populateDatabaseDFS(path.add(twist), depthLimit, table, patternDB)
            }
        }
    }

    /** Checks whether this configuration is already in the pattern database */
    private fun databaseContains(cube: AnalyzableStandardCube, table: ByteArray, patternDB: AbstractPatternDatabase): Boolean {
        return table[patternDB.getIndex(cube)].toInt() != -1
    }
    /** Adds the cost of this cube to the table */
    private fun addCost(cube: AnalyzableStandardCube, cost: Byte, table: ByteArray, patternDB: AbstractPatternDatabase) {
        table[patternDB.getIndex(cube)] = cost
    }
    /** Adds this cost to the table */
    private fun addCost(index: Int, cost: Byte, table: ByteArray) {
        table[index] = cost
    }
    /** Returns the number of true values in the first k bits of this boolean array */
    private fun onesInFirstKBits(seen: BooleanArray, k: Int): Int {
        var sum = 0
        for(i in 0 until k) if(seen[i]) sum++
        return sum
    }
}