package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.AnalyzableStandardCube

/**
 * A CostEvaluator that uses multiple heuristics to produce better heuristic values for the cost function.
 * @constructor constructs a MultiCostEvaluator that considers the costs from all of the provided databases
 * @param evaluators the set of heuristics to consider when evaluating the cost of a given cube
 */
class MultiHeuristic(private vararg val evaluators: CostEvaluator) : CostEvaluator {
    /**
     * Gets the cost of this cube based off the values of the considered heuristics.
     * Gets the maximum of the costs for this cube from each heuristic this MultiPatternDatabase considers.
     * @param cube the cube in question
     * @return a lower bound on the number of moves it might take to solve this cube
     */
    override fun getCost(cube: AnalyzableStandardCube): Byte {
        var cost: Byte = 0
        for(evaluator in evaluators) cost = maxOf(cost, evaluator.getCost(cube))
        return cost
    }

    override fun toString(): String {
        var retVal = "{ ${evaluators[0]}"
        for(i in 1 until evaluators.size) retVal = "$retVal, ${evaluators[i]}"
        return "$retVal }"
    }
}