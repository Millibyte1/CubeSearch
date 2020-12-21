package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.AbstractCube

/**
 * Abstract interface for pattern databases. A pattern database records the costs associated with solving any
 * configuration of some subsection of a Rubik's cube, e.g. just the corners. Depending on the size and implementation
 * of the pattern database, this can yield much better values for the heuristic while still allowing fast lookup.
 * @param T the type of cube this pattern database handles
 */
abstract class AbstractPatternDatabase<T : AbstractCube<T>> : CostEvaluator<T> {
    /**
     * Gets the cost of this cube as stored in the pattern database.
     * Gets the number of moves it takes to solve the most easily solved cube with this configuration of some subset of cubies.
     * @param  cube the cube in question
     * @return a lower bound on the number of moves it might take to solve this cube
     */
    override fun getCost(cube: T): Byte {
        return getCost(getIndex(cube))
    }
    /**
     * Gets the cost of the cube with this index stored in the pattern database.
     * Gets the number of moves it takes to solve the most easily solved cube with this configuration of some subset of cubies.
     * @param  index the index of the cube in question
     * @return a lower bound on the number of moves it might take to solve a cube with this index
     */
    abstract fun getCost(index: Long): Byte
    /**
     * Gets the index of this cube in the pattern database.
     * Produces an integer representation of the configuration of the appropriate subsection of this cube to use
     * as an index for the pattern database.
     * @param cube the cube in question
     * @return the index of this cube in the pattern database.
     */
    abstract fun getIndex(cube: T): Long
}