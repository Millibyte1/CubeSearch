package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.AnalyzableStandardCube
import com.millibyte1.cubesearch.cube.SmartCubeFactory

import com.millibyte1.cubesearch.util.PathWithBack
import com.millibyte1.cubesearch.util.PatternDatabaseUtils
import org.jetbrains.annotations.TestOnly

import kotlin.collections.ArrayList

/*
 * When we consider a database of n edges and m edges,
 *
 * edgeIndex = (2^n * edgePositionIndex) + edgeOrientationIndex
 * = 2^n * (12!/(12-n)!) unique edge orientations for n edges
 * edgePositionIndex = <lehmer encoding of position permutation> * <reverse factorial sequence from 12 down to 12-n> (just lehmer code converted to base 10)
 * = 12!/(12-n)! unique position orientations
 * edgeOrientationIndex = <edge permutation> * <reverse 2^i sequence from 0 up to n-1>
 * = 2^n unique orientation permutations
 *
 * edgeIndex = (3^7 * edgePositionIndex) + edgeOrientationIndex
 * = 3^m * (8!/(8-m)!)
 * edgePositionIndex = <lehmer encoding of position permutation> * <reverse factorial sequence from 8 down to 8-m>
 * = 8!/(8-m)! unique position orientations
 * edgeOrientationIndex = <edge permutation> * <reverse 3^i sequence from 0 up to m-1>
 * = 3^m unique orientation permutations
 *
 * index = ((3^m * (8!/(8-m)!)) * edgeIndex) + edgeIndex
 * = (3^m * (8!/(8-m)!)) * (2^n * (12!/(12-n)!)) unique orientations?
 * No. This hash is collisionless but not perfect --- the edge and edge orientation parities must match for a cube to be solvable so
 * there are significant gaps in the index hits. We must either resort to a mapping structure or incur significant space costs.
 *
 * The memory costs of a flat combined edge and edge database, incurred by the imperfection of the combined hash, likely mean
 * larger edge-only databases would be more useful within reasonable memory constraints than any combined database.
 */

/**
 * A pattern database that considers the positions and orientations of some arbitrary subset of cubies on a solvable cube.
 */
class EdgePatternDatabase private constructor(
    val core: PatternDatabaseCore,
    private val searchMode: String,
    private val consideredEdges: List<Int>
) : AbstractPatternDatabase() {

    internal val cardinality = POWERS_OF_TWO[consideredEdges.size] * (FACTORIALS[12] / FACTORIALS[12 - consideredEdges.size])

    private val factory = SmartCubeFactory()
    private var table = ByteArray(cardinality) { -1 }

    init {
        val bytes = core.readDatabase()
        //if the core is empty, populates the database and stores it in the core
        if(bytes == null) {
            //performs the search to populate the database
            when(searchMode) {
                "bfs" -> PatternDatabaseUtils.populateDatabaseBFS(table, this, factory)
                "iddfs" -> PatternDatabaseUtils.populateDatabaseIDDFS(table, this)
                "dfs" -> {
                    val depthLimit = when(consideredEdges.size) {
                        1 -> 2
                        2 -> 5
                        3 -> 7
                        4 -> 8
                        5 -> 9
                        6 -> 10
                        7 -> 10
                        //don't know what the depth limit is beyond 7 edges and I have no clue how to analytically derive it.
                        else -> 13
                    }
                    PatternDatabaseUtils.populateDatabaseDFS(depthLimit, table, this)
                }
            }
            //stores the database in the core for persistent storage
            core.writeDatabase(table)
        }
        //otherwise reads the database from the core
        else table = bytes
    }

    //gets the minimum cost of a cube with this index
    override fun getCost(index: Int): Byte {
        return table[index]
    }
    //gets the index of this cube
    override fun getIndex(cube: AnalyzableStandardCube): Int {
        //(maxOrientationIndex * positionIndex) + orientationIndex
        val power = POWERS_OF_TWO[consideredEdges.size]
        val posIndex = getPositionIndex(cube)
        val orIndex = getOrientationIndex(cube)
        return (power * posIndex) + orIndex
    }
    /**
     * Computes the position index by converting the lehmer encoding of the position string to a base 10 number.
     *
     * For a partial permutation of k out of n items, the factoradic base of the lehmer code at index i is
     * P((n-1-i)!, (k-1-i)!). It's clear that this is equivalent to just (n-1-i)! for a full permutation (k=n).
     *
     * @param cube the cube we're indexing
     * @return the position index of the partial configuration of this cube defined by consideredEdges
     */
    internal fun getPositionIndex(cube: AnalyzableStandardCube): Int {
        val positions = cube.getEdgePositionPermutation().filterIndexed { index, _ -> index in consideredEdges }
        val lehmer = PatternDatabaseUtils.getLehmerCode(positions, 12)
        //For a partial permutation of k out of n items, the factoradic base of the lehmer code at index i is:
        //P( (n-1-i)!, (k-1-i)! ). It's clear that this is equivalent to just (n-1-i)! for a full permutation (k=n).
        return consideredEdges.foldIndexed(0) { index, sum, _ -> sum + (pick(11 - index, consideredEdges.size - 1 - index) * lehmer[index]) }
    }
    /** Computes the orientation index by converting the orientation string to a base 10 number */
    internal fun getOrientationIndex(cube: AnalyzableStandardCube): Int {
        val orientations = cube.getEdgeOrientationPermutation().filterIndexed { index, _ ->  index in consideredEdges }
        //multiplies the value at each index by its exponential place value
        return consideredEdges.foldIndexed(0) { index, sum, _ -> sum + (orientations[index] * POWERS_OF_TWO[consideredEdges.size - 1 - index]) }
    }

    internal override fun getPopulation(): Int {
        return table.fold(0) { total, item -> if(item == (-1).toByte()) total else total + 1 }
    }

    override fun getCardinality(): Int {
        return cardinality
    }
    /** Gets an array of the edges considered by this pattern database */
    fun getConsideredEdges(): IntArray {
        return consideredEdges.toIntArray()
    }

    override fun toString(): String {
        return core.toString()
    }

    companion object {

        //pregenerates some mathematical values for efficiency purposes
        private val POWERS_OF_TWO = arrayOf(1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096)
        private val FACTORIALS = IntArray(1000)

        init {
            FACTORIALS[0] = 1
            for(i in 1 until 1000) FACTORIALS[i] = i * FACTORIALS[i - 1]
        }

        //returns nPk aka P(n, k) aka etc.
        private fun pick(n: Int, k: Int): Int {
            return FACTORIALS[n] / FACTORIALS[n - k]
        }

        /**
         * Factory function for EdgePatternDatabases.
         * If this pattern database hasn't already been generated and stored in an identical core, then this function will
         * block for a long time.
         * @param core the PatternDatabaseCore to use for database persistence.
         * @param searchMode the search algorithm to use to generate the database - "dfs" or "bfs"
         * @param consideredEdges the cubie numbers of the edges to consider in this pattern database
         * @return a fully constructed EdgePatternDatabase.
         * @throws IllegalArgumentException if the search mode isn't recognized or if consideredEdges is an illegal configuration.
         */
        @Throws(IllegalArgumentException::class)
        fun create(core: PatternDatabaseCore, searchMode: String = "dfs", consideredEdges: MutableList<Int>): EdgePatternDatabase {
            consideredEdges.sort()
            //tests that the arguments are valid and throws if they aren't
            if(searchMode != "dfs" && searchMode != "bfs" && searchMode != "iddfs") throw failInvalidSearchMode()
            if(consideredEdges.size > 8 || consideredEdges.any { item -> item !in 0..11 } || containsDuplicates(consideredEdges)) throw failInvalidEdges()
            //the position and orientation of 11 edges determines the last, so we can remove one redundant cubie from consideration
            if(consideredEdges.size == 8) consideredEdges.removeAt(11)
            //constructs and returns the object
            return EdgePatternDatabase(core, searchMode, consideredEdges)
        }

        /**
         * Factory function for EdgePatternDatabases.
         * If this pattern database hasn't already been generated and stored in an identical core, then this function will
         * block for a long time.
         * @param core the PatternDatabaseCore to use for database persistence.
         * @param searchMode the search algorithm to use to generate the database - "dfs" or "bfs"
         * @param numConsideredEdges the number of edges to consider in this pattern database
         * @throws IllegalArgumentException if the search mode isn't recognized or if there's an illegal number of edges
         */
        @Throws(IllegalArgumentException::class)
        fun create(core: PatternDatabaseCore, searchMode: String = "dfs", numConsideredEdges: Int): EdgePatternDatabase {
            val consideredEdges = ArrayList<Int>(numConsideredEdges)
            for(i in 0 until numConsideredEdges) consideredEdges[i] = i
            return create(core, searchMode, consideredEdges)
        }
    }
}

private fun containsDuplicates(list: List<Int>): Boolean {
    val seen = BooleanArray(12) { false }
    for(item in list) {
        if(seen[item]) return true
        seen[item] = true
    }
    return false
}

private fun failInvalidSearchMode(): IllegalArgumentException {
    return IllegalArgumentException("Invalid search mode provided.")
}
private fun failInvalidEdges(): IllegalArgumentException {
    return IllegalArgumentException("Invalid considered edges list provided")
}