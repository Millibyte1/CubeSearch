package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.AnalyzableStandardCube
import com.millibyte1.cubesearch.cube.CubeFactoryProducer
import com.millibyte1.cubesearch.cube.SmartCube
import com.millibyte1.cubesearch.cube.SmartCubeFactory
import com.millibyte1.cubesearch.util.CubeGenerator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class CostEvaluatorTest {

    @ParameterizedTest
    @MethodSource("evaluators")
    fun testZero(evaluator: CostEvaluator) {
        assertEquals(evaluator.getCost(solved()), 0)
    }
    /** Only actually verifies consistency if the generator walk length perfectly estimates solution depth */
    @ParameterizedTest
    @MethodSource("evaluators")
    fun testConsistency(evaluator: CostEvaluator) {
        for(depth in 1..20) {
            generator.reset()
            generator.setWalkLength(depth)
            for(i in 0 until 100) {
                assertTrue(evaluator.getCost(generator.nextCube()) <= depth)
            }
        }
    }

    @ParameterizedTest
    @MethodSource("evaluators")
    fun testEvaluatorStatistics(evaluator: CostEvaluator) {
        val NUM_TRIALS = 1000000
        val generator = CubeGenerator<SmartCube>(CubeFactoryProducer.getFactory("SmartCube"))
        val costs = ByteArray(NUM_TRIALS)
        val costCounts = IntArray(15) { 0 }
        val costProbabilities = DoubleArray(15) { 0.0 }
        var costSum = 0
        for(i in 0 until NUM_TRIALS) {
            val cube = generator.nextCube()
            val cost = evaluator.getCost(cube)
            costs[i] = cost
            costCounts[cost.toInt()]++
            costSum += cost
        }
        println("Cost evaluator: $evaluator")
        println("Performing analysis via a Monte-Carlo simulation of $NUM_TRIALS cubes.")
        for(cost in costProbabilities.indices) {
            costProbabilities[cost] = costCounts[cost] / NUM_TRIALS.toDouble()
            println("# of cubes with cost $cost: " + costCounts[cost])
        }
        var expected = 0.0
        for(cost in costProbabilities.indices) expected += (cost * costProbabilities[cost])
        println("Average cost: $expected")
    }

    companion object {


        private val factory = SmartCubeFactory()
        private val generator = CubeGenerator<AnalyzableStandardCube>(factory)

        //private val firstSixEdgeDatabase = EdgePatternDatabase.create(FileCore("data/edges-012345.db"), "dfs", mutableListOf(0, 1, 2, 3, 4, 5))
        //private val lastSixEdgeDatabase = EdgePatternDatabase.create(FileCore("data/edges/6789AB.db"), "dfs", mutableListOf(6, 7, 8, 9, 10, 11))
        private val sevenEdgesStartZero = EdgePatternDatabase.create(FileCore("data/edges-0123456.db"), "dfs", mutableListOf(0, 1, 2, 3, 4, 5, 6))
        private val sevenEdgesStartTwo = EdgePatternDatabase.create(FileCore("data/edges-2345678.db"), "dfs", mutableListOf(2, 3, 4, 5, 6, 7, 8))
        private val sevenEdgesStartFour = EdgePatternDatabase.create(FileCore("data/edges-456789A.db"), "dfs", mutableListOf(4, 5, 6, 7, 8, 9, 10))
        private val sevenEdgesStartSix = EdgePatternDatabase.create(FileCore("data/edges-6789AB0.db"), "dfs", mutableListOf(6, 7, 8, 9, 10, 11, 0))
        private val sevenEdgesStartEight = EdgePatternDatabase.create(FileCore("data/edges-89AB012.db"), "dfs", mutableListOf(8, 9, 10, 11, 0, 1, 2))
        private val sevenEdgesStartTen = EdgePatternDatabase.create(FileCore("data/edges-AB01234.db"), "dfs", mutableListOf(10, 11, 0, 1, 2, 3, 4))

        private val cornerPatternDatabase = CornerPatternDatabase.create(FileCore("data/corners-full.db"), "dfs", mutableListOf(0, 1, 2, 3, 4, 5, 6, 7))


        //private val twinSixEdgePatternDatabase = MultiHeuristic(firstSixEdgeDatabase, lastSixEdgeDatabase)
        private val twinSevenEdgePatternDatabase = MultiHeuristic(sevenEdgesStartZero, sevenEdgesStartSix)
        //private val twinSixEdgeAndCornerPatternDatabase = MultiHeuristic(firstSixEdgeDatabase, lastSixEdgeDatabase, cornerPatternDatabase)
        private val twinSevenEdgeAndCornerPatternDatabase = MultiHeuristic(sevenEdgesStartZero, sevenEdgesStartSix, cornerPatternDatabase)
        private val manyEdgeDatabase = MultiHeuristic(
            sevenEdgesStartZero, sevenEdgesStartTwo, sevenEdgesStartFour,
            sevenEdgesStartSix, sevenEdgesStartEight, sevenEdgesStartTen
        )
        private val manyEdgeDatabaseWithCorners = MultiHeuristic(
            sevenEdgesStartZero, sevenEdgesStartTwo, sevenEdgesStartFour,
            sevenEdgesStartSix, sevenEdgesStartEight, sevenEdgesStartTen,
            cornerPatternDatabase
        )

        private fun solved(): SmartCube {
            return factory.getSolvedCube()
        }

        @JvmStatic
        fun evaluators(): List<CostEvaluator> {
            return listOf(
                //twinSixEdgePatternDatabase,
                twinSevenEdgePatternDatabase,
                //twinSixEdgeAndCornerPatternDatabase,
                twinSevenEdgeAndCornerPatternDatabase,
                manyEdgeDatabase,
                manyEdgeDatabaseWithCorners,
                cornerPatternDatabase
            )
        }
    }
}