package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.AnalyzableStandardCube
import com.millibyte1.cubesearch.util.copy

/*
 *
 */

/**
 * A pattern database that considers the positions and orientations of some arbitrary subset of cubies on a solvable cube.
 */
class CubieSubsetPatternDatabase private constructor(
    val core: PatternDatabaseCore,
    val searchMode: String,
    val consideredEdges: List<Int>,
    val consideredCorners: List<Int>
) : AbstractPatternDatabase() {

    override fun getCost(index: Int): Byte {
        TODO("Not yet implemented")
    }

    override fun getIndex(cube: AnalyzableStandardCube): Int {
        TODO("Not yet implemented")
    }

    companion object {
        /**
         * Factory function for CubieSubsetPatternDatabases.
         * If this pattern database hasn't already been generated and stored in an identical core, then this function will
         * block for a long time.
         * @param core the PatternDatabaseCore to use for database persistence.
         * @param searchMode the search algorithm to use to generate the database - "dfs" or "bfs"
         * @param consideredEdges the cubie numbers of the edges to consider in this pattern database
         * @param consideredCorners the cubie numbers of the corners to consider in this pattern database
         * @return a fully constructed CubieSubsetPatternDatabase.
         * @throws IllegalArgumentException if the search mode isn't recognized or if consideredEdges or consideredCorners is an illegal configuration.
         */
        @Throws(IllegalArgumentException::class)
        fun create(
            core: PatternDatabaseCore,
            searchMode: String = "dfs",
            consideredEdges: MutableList<Int>,
            consideredCorners: MutableList<Int>
        ): CubieSubsetPatternDatabase {
            //sorts the edges and corners
            consideredEdges.sort()
            consideredCorners.sort()
            //tests that the arguments are valid and throws if they aren't
            if(searchMode != "dfs" && searchMode != "bfs") throw failInvalidSearchMode()
            if(consideredEdges.size > 12 || consideredEdges.any { item -> item !in 0..11 } || containsDuplicates(consideredEdges)) throw failInvalidEdges()
            if(consideredCorners.size > 8 || consideredCorners.any { item -> item !in 0..7 } || containsDuplicates(consideredCorners)) throw failInvalidCorners()
            //the position and orientation of 11 edges or 8 corners determines the last, so we can remove one redundant cubie from consideration
            if(consideredEdges.size == 12) consideredEdges.removeAt(11)
            if(consideredCorners.size == 8) consideredCorners.removeAt(7)
            //constructs and returns the object
            return CubieSubsetPatternDatabase(core, searchMode, consideredEdges, consideredCorners)
        }

        /**
         * Factory function for CubieSubsetPatternDatabases.
         * If this pattern database hasn't already been generated and stored in an identical core, then this function will
         * block for a long time.
         * @param core the PatternDatabaseCore to use for database persistence.
         * @param searchMode the search algorithm to use to generate the database - "dfs" or "bfs"
         * @param numConsideredEdges the number of edges to consider in this pattern database
         * @param numConsideredCorners the number of corners to consider in this pattern database
         * @return a fully constructed CubieSubsetPatternDatabase.
         * @throws IllegalArgumentException if the search mode isn't recognized or if there's an illegal number of edges or corners
         */
        @Throws(IllegalArgumentException::class)
        fun create(
            core: PatternDatabaseCore,
            searchMode: String = "dfs",
            numConsideredEdges: Int,
            numConsideredCorners: Int
        ): CubieSubsetPatternDatabase {
            val consideredEdges = ArrayList<Int>(numConsideredEdges)
            val consideredCorners = ArrayList<Int>(numConsideredCorners)
            for(i in 0 until numConsideredEdges) consideredEdges[i] = i
            for(i in 0 until numConsideredCorners) consideredCorners[i] = i
            return create(core, searchMode, consideredEdges, consideredCorners)
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
private fun failInvalidCorners(): IllegalArgumentException {
    return IllegalArgumentException("invalid considered corners list provided")
}