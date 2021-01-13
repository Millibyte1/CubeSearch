package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.AnalyzableStandardCube
import com.millibyte1.cubesearch.cube.StandardCube

/**
 * A CostEvaluator that uses multiple pattern databases to produce better heuristic values for the cost function.
 * @param T the type of cube on which this MultiPatternDatabase operates
 * @constructor constructs a MultiPatternDatabase that considers the costs from all of the provided databases
 * @param databases the set of databases to consider when evaluating the cost of a given cube
 */
class MultiPatternDatabase(private vararg val databases: AbstractPatternDatabase) : CostEvaluator {
    /**
     * Gets the cost of this cube based off the values in the pattern databases.
     * Gets the maximum of the costs for this cube from each pattern database this MultiPatternDatabase considers.
     * @param cube the cube in question
     * @return a lower bound on the number of moves it might take to solve this cube
     */
    override fun getCost(cube: AnalyzableStandardCube): Byte {
        var cost: Byte = 0
        for(database in databases) cost = maxOf(cost, database.getCost(cube))
        return cost
    }
}