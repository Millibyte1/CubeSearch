package com.millibyte1.cubesearch.util

import com.leansoft.bigqueue.BigQueueImpl
import com.millibyte1.cubesearch.algorithm.heuristics.AbstractPatternDatabase
import com.millibyte1.cubesearch.cube.AnalyzableStandardCube
import com.millibyte1.cubesearch.cube.SmartCube
import com.millibyte1.cubesearch.cube.SmartCubeFactory
import com.millibyte1.cubesearch.cube.Twist
import org.apache.commons.lang3.SerializationUtils
import java.util.*
import kotlin.collections.ArrayList

//TODO idea: bidirectional search using IDA* w/ big pattern database in the towards solved direction and frontier search w/ smaller DB in the from solved
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
 * Assuming that each entry in a pattern database takes up a single byte, we get:
 * ~88.18 MiB space for the corner database,
 * ~1.78 TiB space for the full 12-edge database,
 * ~913.6 GiB space for an 11-edge database,
 * ~228.4 GiB space for a 10-edge database,
 * ~38.07 GiB space for a 9-edge database,
 * ~4.76 GiB space for an 8-edge database,
 * ~487.3 MiB space for a 7-edge database,
 * ~40.6 MiB space for a 6-edge database.
 *
 * Assuming each machine in a cluster can dedicate 8GiB of RAM to an in-memory database, we would need:
 * a cluster of 229 machines to implement the full 12-edge database,
 * a cluster of 115 machines to implement an 11-edge database,
 * a cluster of 29 machines to implement a 10-edge database,
 * a cluster of 5 machines to implement a 9-edge database,
 * and a single machine for smaller databases.
 *
 * These numbers and the database sizes could be cut in half by storing entries as nibbles.
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

    /** Gets the number of entries in the pattern database */
    fun getPopulation(table: ByteArray): Int {
        return table.fold(0) { total, item -> if(item == (-1).toByte()) total else total + 1 }
    }

    /**
     * Generates the pattern database via a DP-optimized BFS. Whether this can actually finish is limited by available disk space.
     * @param table a reference to the target table of costs
     * @param patternDB the pattern database to use for indexing
     * @param factory the cube factory to use
     * @param maxCost the maximum length of the shortest path from the solved cube to any configuration. Enables significant optimizations. Optional.
     * @param MAX_SIZE the size threshold at which to switch from an in-memory to an on-disk queue. Optional.
     */
    //TODO: find max physical size of a naturally occurring PathWithBack via random sample and use that + config values to inform MAX_SIZE
    fun populateDatabaseBFS(
        table: ByteArray,
        patternDB: AbstractPatternDatabase,
        factory: SmartCubeFactory,
        maxCost: Int = 20,
        MAX_SIZE: Int = 1000000
    ) {
        val depthLimit = maxCost - 1
        //constructs the queue for the BFS and enqueues the solved cube
        var queue: PopulatorQueue = PopulatorQueue.MemoryQueue()
        queue.enqueue(PathWithBack(ArrayList(), factory.getSolvedCube()))
        addCost(factory.getSolvedCube(), 0, table, patternDB)
        //generates every possible corner configuration via a breadth-first traversal
        while (!queue.isEmpty()) {
            //if we've gone over the size limit for the in-memory queue, move to an on-disk queue
            if (queue.effectiveSize() > MAX_SIZE) queue = siphonQueue(queue, PopulatorQueue.DiskQueue("temp", "populate-db-queue"))
            //dequeues the cube
            val current: PathWithBack = queue.dequeue()

            //if we haven't reached the depth limit, expand the nodes out
            if(current.size() < depthLimit) {
                //uses a 2-move history to prune some twists resulting in cubes that can be generated in fewer moves
                val face1Previous = if (current.size() >= 1) Twist.getFace(current.path[current.size() - 1]) else null
                val face2Previous = if (current.size() >= 2) Twist.getFace(current.path[current.size() - 2]) else null
                //tries to expand off of each potentially viable twist
                for (twist in SolverUtils.getOptions(face1Previous, face2Previous)) {
                    val nextCube = current.back.twist(twist)
                    //if we haven't already encountered the cube, add to the database and to the expansion queue
                    if (!databaseContains(nextCube, table, patternDB)) {
                        queue.enqueue(current.add(twist))
                        addCost(nextCube, (current.size() + 1).toByte(), table, patternDB)
                    }
                }
            }
        }
        //sets any remaining uninitialized costs to the max cost
        for(i in table.indices) if(table[i] == (-1).toByte()) table[i] = maxCost.toByte()
    }

    /**
     * Tries to generate the database with depth-first searches to various depths.
     * Strictly slower than plain DFS if you already know the necessary depth, but faster if it's unknown.
     * @param table a reference to the target table of costs
     * @param patternDB the pattern database to use for indexing
     * @param factory the cube factory to use
     */
    fun populateDatabaseIDDFS(table: ByteArray, patternDB: AbstractPatternDatabase, factory: SmartCubeFactory = SmartCubeFactory()) {
        //tries to generate the database at every depth up to 20, breaking off when it's done
        for(depthLimit in 0 until 20) {
            //populateDatabaseDFS(path, depthLimit, table, patternDB)
            populateDatabaseDFS(table, patternDB, depthLimit)
            val population = getPopulation(table)
            //if the database is fully generated, we can stop
            if(population == patternDB.getCardinality()) break
            //otherwise reset the database and repeat with a deeper search
            for(i in table.indices) table[i] = -1
        }
    }

    /**
     * Performs a depth-limited, DP-optimized DFS to generate the pattern database.
     * Takes advantage of the max cost being known by cutting off at the depth level before it and setting all
     * remaining uninitialized entries in the database to the max cost.
     * @param maxCost the maximum length of the shortest path from the solved cube to any configuration.
     * @param table a reference to the target table of costs
     * @param patternDB the pattern database to use for indexing
     * @param factory the cube factory to use
     */
    fun populateDatabaseDFS(
        maxCost: Int,
        table: ByteArray,
        patternDB: AbstractPatternDatabase,
        factory: SmartCubeFactory = SmartCubeFactory()
    ) {
        populateDatabaseDFS(table, patternDB, maxCost - 1, factory)
        for(i in table.indices) if(table[i] == (-1).toByte()) table[i] = maxCost.toByte()
    }

    /** Performs a depth-limited, DP-optimized DFS up to the given depth limit */
    private fun populateDatabaseDFS(
        table: ByteArray,
        patternDB: AbstractPatternDatabase,
        depthLimit: Int,
        factory: SmartCubeFactory = SmartCubeFactory()
    ) {
        //initializes the search queue and enqueues the solved cube
        val pathQueue: Deque<PathWithBack> = ArrayDeque()
        pathQueue.addFirst(PathWithBack(ArrayList(), factory.getSolvedCube()))

        //performs an iterative DFS using an explicit queue
        while(pathQueue.isNotEmpty()) {

            val path = pathQueue.removeFirst()
            val current = path.back
            val currentDepth = path.size()
            val index = patternDB.getIndex(current)

            //short circuits if we've already encountered a cube with this corner configuration at this low a depth
            if(table[index] != (-1).toByte() && table[index] <= currentDepth.toByte()) continue
            //adds this configuration to the database
            addCost(index, currentDepth.toByte(), table)

            //if we're not at the depth limit, try more twists
            if(currentDepth < depthLimit) {
                //uses a 2-move history to prune some twists resulting in cubes that can be generated in fewer moves
                val face1Previous = if(path.size() >= 1) Twist.getFace(path.path[path.size() - 1]) else null
                val face2Previous = if(path.size() >= 2) Twist.getFace(path.path[path.size() - 2]) else null
                //enqueues all of the candidate twists
                for(twist in SolverUtils.getOptions(face1Previous, face2Previous)) pathQueue.addFirst(path.add(twist))
            }
        }
    }

    /** Checks whether this configuration is already in the pattern database */
    private fun databaseContains(cube: AnalyzableStandardCube, table: ByteArray, patternDB: AbstractPatternDatabase): Boolean {
        return table[patternDB.getIndex(cube)] != (-1).toByte()
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

/** Algebraic sum type for queues that might get used for the BFS populator algorithm. */
private sealed class PopulatorQueue() {
    /** Wrapper for a regular old ArrayDeque */
    class MemoryQueue() : PopulatorQueue() {
        private val queue: Queue<PathWithBack> = ArrayDeque()
        override fun enqueue(path: PathWithBack) {
            queue.add(path)
        }
        override fun dequeue(): PathWithBack {
            return queue.remove()
        }
        override fun isEmpty(): Boolean {
            return queue.isEmpty()
        }
        override fun effectiveSize(): Int {
            return queue.size
        }
    }
    /** Wrapper for a BigQueue that lives on disk. Thanks bulldog2011! (src: https://github.com/bulldog2011/bigqueue) */
    class DiskQueue(filePath: String, fileName: String) : PopulatorQueue() {
        private val queue = BigQueueImpl(filePath, fileName)
        override fun enqueue(path: PathWithBack) {
            queue.enqueue(SerializationUtils.serialize(path))
        }
        override fun dequeue(): PathWithBack {
            return SerializationUtils.deserialize(queue.dequeue())
        }
        override fun isEmpty(): Boolean {
            return queue.isEmpty
        }
        override fun effectiveSize(): Int {
            return 0
        }
    }
    /** Enqueues an item onto the queue */
    abstract fun enqueue(path: PathWithBack)
    /** Dequeues an item from the queue */
    abstract fun dequeue(): PathWithBack
    /** Checks whether the queue is empty */
    abstract fun isEmpty(): Boolean
    /** Returns the number of elements in the queue if the queue is in memory, else 0 */
    abstract fun effectiveSize(): Int
}

/** Siphons all the elements of the old queue into the new queue */
private fun siphonQueue(oldQueue: PopulatorQueue, newQueue: PopulatorQueue): PopulatorQueue {
    while (!oldQueue.isEmpty()) newQueue.enqueue(oldQueue.dequeue())
    return newQueue
}