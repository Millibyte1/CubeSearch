package com.millibyte1.cubesearch.algorithm

import com.millibyte1.cubesearch.algorithm.heuristics.CostEvaluator
import com.millibyte1.cubesearch.algorithm.heuristics.ManhattanDistanceCostEvaluator
import com.millibyte1.cubesearch.cube.*
import com.millibyte1.cubesearch.util.CubeGenerator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class SolverTest {

    /* =============================================== UNIT TESTS =================================================== */

    @ParameterizedTest
    @MethodSource("solvers")
    fun testLengthZeroSolutions(solver: Solver) {
        assertEquals(solver.getSolution(solved()).size, 0)
    }
    @ParameterizedTest
    @MethodSource("solvers")
    fun testLengthOneSolutions(solver: Solver) {
        for(solution in lengthOneSolutions()) {
            assertEquals(solver.getSolution(solution.first)[0], Twist.getReverse(solution.second))
        }
    }
    @ParameterizedTest
    @MethodSource("solvers")
    fun testRandomSolutions(solver: Solver) {
        for(depth in 1..depthRating(solver)) {
            generator.reset()
            generator.setWalkLength(depth)
            for(i in 0 until 100) {
                var cube = generator.nextCube()
                val solution = solver.getSolution(cube)
                val visited = HashSet<AnalyzableStandardCube>()
                //checks that the solution actually works, and that there's no duplicate states
                for(twist in solution) {
                    assertFalse(visited.contains(cube))
                    visited.add(cube)
                    cube = cube.twist(twist)
                }
                assertEquals(cube, solved())
            }
        }
    }

    @ParameterizedTest
    @MethodSource("solvers")
    fun testSingleDeepSolution(solver: Solver) {
        generator.reset()
        generator.setWalkLength(singleRunDepthRating(solver))
        var cube = generator.nextCube()
        val solution = solver.getSolution(cube)
        val visited = HashSet<AnalyzableStandardCube>()
        //checks that the solution actually works, and that there's no duplicate states
        for(twist in solution) {
            assertFalse(visited.contains(cube))
            visited.add(cube)
            cube = cube.twist(twist)
        }
        assertEquals(cube, solved())
    }
    @ParameterizedTest
    @MethodSource("solvers")
    fun testConsistencyWithHeuristic(solver: Solver) {
        for(depth in 1..depthRating(solver)) {
            generator.reset()
            generator.setWalkLength(depth)
            for(i in 0 until 100) {
                val cube = generator.nextCube()
                assertTrue(solver.getSolution(cube).size >= standardCostFunction().getCost(cube))
            }
        }
    }

    companion object {

        private val factory = SmartCubeFactory()
        private val generator = CubeGenerator<SmartCube>(factory, -483132)

        /* ====================================== TEST FIXTURES ===================================================== */

        private fun solved(): AnalyzableStandardCube {
            return factory.getSolvedCube()
        }

        private fun standardCostFunction(): CostEvaluator {
            return ManhattanDistanceCostEvaluator()
        }

        @JvmStatic
        /** Returns a list of solvers using an already tested CostEvaluator */
        private fun solvers(): List<Solver> {
            //return listOf(IterativeDeepeningAStarSolver(standardCostFunction()))
            return listOf(ClassicalAStarSolver(standardCostFunction()),
                          FrontierSearchSolver(standardCostFunction()),
                          IterativeDeepeningAStarSolver(standardCostFunction()))
        }

        @JvmStatic
        private fun lengthOneSolutions(): List<Pair<AnalyzableStandardCube, Twist>> {
            val solutions = ArrayList<Pair<AnalyzableStandardCube, Twist>>()
            for(twist in Twist.values()) solutions.add(Pair(solved().twist(twist), twist))
            return solutions
        }

        /** The maximum depth of solution this solver can handle for many repeated runs */
        @JvmStatic
        private fun depthRating(solver: Solver): Int {
            return when(solver) {
                is ClassicalAStarSolver -> 5
                is FrontierSearchSolver -> 5
                is IterativeDeepeningAStarSolver -> 6
                else -> 6
            }
        }
        @JvmStatic
        private fun singleRunDepthRating(solver: Solver): Int {
            return when(solver) {
                is ClassicalAStarSolver -> 7
                is FrontierSearchSolver -> 7
                is IterativeDeepeningAStarSolver -> 7
                else -> 7
            }
        }
    }
}