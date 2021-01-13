package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.AnalyzableStandardCube
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
        var cube: SmartCube
        for(depth in 1..20) {
            generator.reset()
            generator.setWalkLength(depth)
            for(i in 0 until 100) {
                assertTrue(evaluator.getCost(generator.nextCube()) <= depth)
            }
        }
    }

    companion object {

        private val factory = SmartCubeFactory()
        private val generator = CubeGenerator<AnalyzableStandardCube>(factory)

        private fun solved(): SmartCube {
            return factory.getSolvedCube()
        }

        @JvmStatic
        fun evaluators(): List<CostEvaluator> {
            return listOf(
                ManhattanDistanceCostEvaluator(),
                CornerPatternDatabase
            )
        }
    }
}