package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.Cube
import com.millibyte1.cubesearch.cube.CubeFactory
import com.millibyte1.cubesearch.util.CubeGenerator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class CostEvaluatorTest {

    @ParameterizedTest
    @MethodSource("evaluators")
    fun testZero(evaluator: CostEvaluator<Cube>) {
        assertEquals(evaluator.getCost(solved()), 0)
    }
    /** Only actually verifies consistency if the generator walk length perfectly estimates solution depth */
    @ParameterizedTest
    @MethodSource("evaluators")
    fun testConsistency(evaluator: CostEvaluator<Cube>) {
        var cube: Cube
        for(depth in 1..20) {
            generator.reset()
            generator.setWalkLength(depth)
            for(i in 0 until 100) {
                assertTrue(evaluator.getCost(generator.nextCube()) <= depth)
            }
        }
    }

    companion object {

        private val factory = CubeFactory()
        private val generator = CubeGenerator(factory)

        private fun solved(): Cube {
            return factory.getSolvedCube()
        }

        @JvmStatic
        fun evaluators(): List<CostEvaluator<Cube>> {
            return listOf(ManhattanDistanceCostEvaluator)
        }
    }
}