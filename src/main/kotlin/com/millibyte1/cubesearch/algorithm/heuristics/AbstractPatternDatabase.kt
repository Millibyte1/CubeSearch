package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.StandardCube

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
 * optimisticDepth = log_15(12! * 2^11) = ~10.2 for the full edge database
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

/**
 * Abstract interface for pattern databases. A pattern database records the costs associated with solving any
 * configuration of some subsection of a Rubik's cube, e.g. just the corners. Depending on the size and implementation
 * of the pattern database, this can yield much better values for the heuristic while still allowing fast lookup.
 * @param T the type of cube this pattern database handles
 */
abstract class AbstractPatternDatabase<T : StandardCube<T>> : CostEvaluator<T> {
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
    abstract fun getCost(index: Int): Byte
    /**
     * Gets the index of this cube in the pattern database.
     * Produces an integer representation of the configuration of the appropriate subsection of this cube to use
     * as an index for the pattern database.
     * @param cube the cube in question
     * @return the index of this cube in the pattern database.
     */
    abstract fun getIndex(cube: T): Int
}