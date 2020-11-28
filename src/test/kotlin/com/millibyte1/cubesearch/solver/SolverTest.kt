package com.millibyte1.cubesearch.solver

import com.millibyte1.cubesearch.cube.Cube
import com.millibyte1.cubesearch.cube.CubeFactory
import com.millibyte1.cubesearch.cube.Twist
import com.millibyte1.cubesearch.util.CubeGenerator

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Tag

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class SolverTest {

    /* =============================================== UNIT TESTS =================================================== */

    @ParameterizedTest
    @MethodSource("solvers")
    fun testLengthZeroSolutions(solver: Solver<Cube>) {
        assertEquals(solver.getSolution(solved()).size, 0)
    }
    @ParameterizedTest
    @MethodSource("solvers")
    fun testLengthOneSolutions(solver: Solver<Cube>) {
        for(solution in lengthOneSolutions()) assertEquals(solver.getSolution(solution.cube), solution.path)
    }
    @ParameterizedTest
    @MethodSource("solvers")
    fun testLengthTwoSolutions(solver: Solver<Cube>) {
        for(solution in lengthTwoSolutions()) {
            assertTrue(solver.getSolution(solution.cube) == solution.path ||
                       solver.getSolution(solution.cube) == solution.path.reversed())
        }
    }
    @ParameterizedTest
    @MethodSource("solvers")
    fun testConsistencyWithHeuristic(solver: Solver<Cube>) {
        for(depth in 1 until depthRating(solver)) {
            generator.reset()
            generator.setDifficulty(depth)
            for(i in 0 until 100) {
                val cube = generator.nextCube()
                assertTrue(solver.getSolution(cube).size >= standardCostFunction().getCost(cube))
            }
        }
    }

    companion object {

        private val factory = CubeFactory()
        private val generator = CubeGenerator(factory, -483132)

        private data class SolutionCase(val cube: Cube, val path: ArrayList<Twist>)

        /* ====================================== TEST FIXTURES ===================================================== */

        private fun solved(): Cube {
            return factory.getSolvedCube()
        }

        private fun standardCostFunction(): CostEvaluator<Cube> {
            return ManhattanDistanceCostEvaluator
        }

        @JvmStatic
        /** Returns a list of solvers using an already tested CostEvaluator */
        private fun solvers(): List<Solver<Cube>> {
            return listOf(ClassicalAStarSolver(standardCostFunction()),
                    IterativeDeepeningAStarSolver(standardCostFunction()))
        }

        @JvmStatic
        private fun lengthOneSolutions(): List<SolutionCase> {
            val solutions = ArrayList<SolutionCase>()
            for(twist in Twist.values()) solutions.add(SolutionCase(solved().twist(twist), arrayListOf(twist)))
            return solutions
        }

        @JvmStatic
        private fun lengthTwoSolutions(): List<SolutionCase> {
            TODO()
        }

        /** The maximum depth of solution this solver can handle */
        @JvmStatic
        private fun depthRating(solver: Solver<Cube>): Int {
            return when(solver) {
                is ClassicalAStarSolver -> 8
                is IterativeDeepeningAStarSolver -> 10
                else -> 10
            }
        }
    }
}