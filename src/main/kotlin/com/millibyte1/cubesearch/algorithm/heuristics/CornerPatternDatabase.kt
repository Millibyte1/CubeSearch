package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.AnalyzableStandardCube
import com.millibyte1.cubesearch.cube.SmartCubeFactory

import com.millibyte1.cubesearch.util.PathWithBack
import com.millibyte1.cubesearch.util.PatternDatabaseUtils
import org.jetbrains.annotations.TestOnly

import kotlin.collections.ArrayList

/*
 * When we consider a database of n edges and m corners,
 *
 * edgeIndex = (2^n * edgePositionIndex) + edgeOrientationIndex
 * = 2^n * (12!/(12-n)!) unique edge orientations for n edges
 * edgePositionIndex = <lehmer encoding of position permutation> * <reverse factorial sequence from 12 down to 12-n> (just lehmer code converted to base 10)
 * = 12!/(12-n)! unique position orientations
 * edgeOrientationIndex = <edge permutation> * <reverse 2^i sequence from 0 up to n-1>
 * = 2^n unique orientation permutations
 *
 * cornerIndex = (3^7 * cornerPositionIndex) + cornerOrientationIndex
 * = 3^m * (8!/(8-m)!)
 * cornerPositionIndex = <lehmer encoding of position permutation> * <reverse factorial sequence from 8 down to 8-m>
 * = 8!/(8-m)! unique position orientations
 * cornerOrientationIndex = <corner permutation> * <reverse 3^i sequence from 0 up to m-1>
 * = 3^m unique orientation permutations
 *
 * index = ((3^m * (8!/(8-m)!)) * edgeIndex) + cornerIndex
 * = (3^m * (8!/(8-m)!)) * (2^n * (12!/(12-n)!)) unique orientations?
 * No. This hash is collisionless but not perfect --- the edge and corner orientation parities must match for a cube to be solvable so
 * there are significant gaps in the index hits. We must either resort to a mapping structure or incur significant space costs.
 *
 * The memory costs of a flat combined edge and corner database, incurred by the imperfection of the combined hash, likely mean
 * larger edge-only databases would be more useful within reasonable memory constraints than any combinegetPopulationd database.
 */

/**
 * A pattern database that considers the positions and orientations of some arbitrary subset of cubies on a solvable cube.
 */
class CornerPatternDatabase private constructor(
    val core: PatternDatabaseCore,
    private val searchMode: String,
    private val consideredCorners: List<Int>
) : AbstractPatternDatabase() {

    private val cardinality = POWERS_OF_THREE[consideredCorners.size] * (FACTORIALS[8] / FACTORIALS[8 - consideredCorners.size])

    private val factory = SmartCubeFactory()
    private var table = ByteArray(cardinality) { -1 }

    init {
        val bytes = core.readDatabase()
        //if the core is empty, populates the database and stores it in the core
        if(bytes == null) {
            val maxCost = when(consideredCorners.size) {
                1 -> 2
                2 -> 4
                3 -> 6
                4 -> 7
                5 -> 8
                6 -> 10
                else -> 11
            }
            //performs the search to populate the database
            when(searchMode) {
                "bfs" -> PatternDatabaseUtils.populateDatabaseBFS(table, this, factory, maxCost)
                "iddfs" -> PatternDatabaseUtils.populateDatabaseIDDFS(table, this)
                "dfs" -> {
                    PatternDatabaseUtils.populateDatabaseDFS(maxCost, table, this)
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
        val power = POWERS_OF_THREE[consideredCorners.size]
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
     * @return the position index of the partial configuration of this cube defined by consideredCorners
     */
    internal fun getPositionIndex(cube: AnalyzableStandardCube): Int {
        val positions = cube.getCornerPositionPermutation().filterIndexed { index, _ -> index in consideredCorners }
        val lehmer = PatternDatabaseUtils.getLehmerCode(positions, 8)
        //For a partial permutation of k out of n items, the factoradic base of the lehmer code at index i is:
        //P( (n-1-i)!, (k-1-i)! ). It's clear that this is equivalent to just (n-1-i)! for a full permutation (k=n).
        return consideredCorners.foldIndexed(0) { index, sum, _ -> sum + (pick(7 - index, consideredCorners.size - 1 - index) * lehmer[index]) }
    }
    /** Computes the orientation index by converting the orientation string to a base 10 number */
    internal fun getOrientationIndex(cube: AnalyzableStandardCube): Int {
        val orientations = cube.getCornerOrientationPermutation().filterIndexed { index, _ ->  index in consideredCorners }
        //multiplies the value at each index by its exponential place value
        return consideredCorners.foldIndexed(0) { index, sum, _ -> sum + (orientations[index] * POWERS_OF_THREE[consideredCorners.size - 1 - index]) }
    }

    internal override fun getPopulation(): Int {
        return table.fold(0) { total, item -> if(item == (-1).toByte()) total else total + 1 }
    }

    override fun getCardinality(): Int {
        return cardinality
    }
    /** Gets an array of the corners considered by this pattern database */
    fun getConsideredCorners(): IntArray {
        return consideredCorners.toIntArray()
    }

    override fun toString(): String {
        return core.toString()
    }

    companion object {

        //pregenerates some mathematical values for efficiency purposes
        private val POWERS_OF_THREE = arrayOf(1, 3, 9, 27, 81, 243, 729, 2187, 6561)
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
         * Factory function for CornerPatternDatabases.
         * If this pattern database hasn't already been generated and stored in an identical core, then this function will
         * block for a long time.
         * @param core the PatternDatabaseCore to use for database persistence.
         * @param searchMode the search algorithm to use to generate the database - "dfs" or "bfs"
         * @param consideredCorners the cubie numbers of the corners to consider in this pattern database
         * @return a fully constructed CornerPatternDatabase.
         * @throws IllegalArgumentException if the search mode isn't recognized or if consideredCorners is an illegal configuration.
         */
        @Throws(IllegalArgumentException::class)
        fun create(core: PatternDatabaseCore, searchMode: String = "dfs", consideredCorners: MutableList<Int>): CornerPatternDatabase {
            consideredCorners.sort()
            //tests that the arguments are valid and throws if they aren't
            if(searchMode != "dfs" && searchMode != "bfs" && searchMode != "iddfs") throw failInvalidSearchMode()
            if(consideredCorners.size > 8 || consideredCorners.any { item -> item !in 0..7 } || containsDuplicates(consideredCorners)) throw failInvalidCorners()
            //the position and orientation of 7 corners determines the last, so we can remove one redundant cubie from consideration
            if(consideredCorners.size == 8) consideredCorners.removeAt(7)
            //constructs and returns the object
            return CornerPatternDatabase(core, searchMode, consideredCorners)
        }

        /**
         * Factory function for CornerPatternDatabases.
         * If this pattern database hasn't already been generated and stored in an identical core, then this function will
         * block for a long time.
         * @param core the PatternDatabaseCore to use for database persistence.
         * @param searchMode the search algorithm to use to generate the database - "dfs" or "bfs"
         * @param numConsideredCorners the number of corners to consider in this pattern database
         * @throws IllegalArgumentException if the search mode isn't recognized or if there's an illegal number of corners
         */
        @Throws(IllegalArgumentException::class)
        fun create(core: PatternDatabaseCore, searchMode: String = "dfs", numConsideredCorners: Int): CornerPatternDatabase {
            val consideredCorners = ArrayList<Int>(numConsideredCorners)
            for(i in 0 until numConsideredCorners) consideredCorners[i] = i
            return create(core, searchMode, consideredCorners)
        }
    }
}

private fun containsDuplicates(list: List<Int>): Boolean {
    val seen = BooleanArray(8) { false }
    for(item in list) {
        if(seen[item]) return true
        seen[item] = true
    }
    return false
}

private fun failInvalidSearchMode(): IllegalArgumentException {
    return IllegalArgumentException("Invalid search mode provided.")
}
private fun failInvalidCorners(): IllegalArgumentException {
    return IllegalArgumentException("Invalid considered corners list provided")
}