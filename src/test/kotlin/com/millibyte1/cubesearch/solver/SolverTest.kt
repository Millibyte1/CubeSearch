package com.millibyte1.cubesearch.solver

import com.millibyte1.cubesearch.cube.Cube
import com.millibyte1.cubesearch.cube.CubeFactory
import com.millibyte1.cubesearch.cube.Twist
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
    fun testLengthZeroSolutions(solver: Solver<Cube>) {
        assertEquals(solver.getSolution(solved()).size, 0)
    }
    @ParameterizedTest
    @MethodSource("solvers")
    fun testLengthOneSolutions(solver: Solver<Cube>) {
        for(solution in lengthOneSolutions()) {
            assertEquals(solver.getSolution(solution.first)[0], Twist.getReverse(solution.second))
        }
    }
    @ParameterizedTest
    @MethodSource("solvers")
    fun testRandomSolutions(solver: Solver<Cube>) {
        for(depth in 1..depthRating(solver)) {
            generator.reset()
            generator.setWalkLength(depth)
            for(i in 0 until 100) {
                var cube = generator.nextCube()
                val solution = solver.getSolution(cube)
                val visited = HashSet<Cube>()
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
    fun testSingleDeepSolution(solver: Solver<Cube>) {
        generator.reset()
        generator.setWalkLength(singleRunDepthRating(solver))
        var cube = generator.nextCube()
        val solution = solver.getSolution(cube)
        val visited = HashSet<Cube>()
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
    fun testConsistencyWithHeuristic(solver: Solver<Cube>) {
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

        private val factory = CubeFactory()
        private val generator = CubeGenerator(factory, -483132)

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
            //return listOf(IterativeDeepeningAStarSolver(standardCostFunction()))
            return listOf(ClassicalAStarSolver(standardCostFunction()),
                          FrontierSearchSolver(standardCostFunction()),
                          IterativeDeepeningAStarSolver(standardCostFunction()))
        }

        @JvmStatic
        private fun lengthOneSolutions(): List<Pair<Cube, Twist>> {
            val solutions = ArrayList<Pair<Cube, Twist>>()
            for(twist in Twist.values()) solutions.add(Pair(solved().twist(twist), twist))
            return solutions
        }

        /** The maximum depth of solution this solver can handle for many repeated runs */
        @JvmStatic
        private fun depthRating(solver: Solver<Cube>): Int {
            return when(solver) {
                is ClassicalAStarSolver -> 5
                is FrontierSearchSolver -> 5
                is IterativeDeepeningAStarSolver -> 6
                else -> 6
            }
        }
        @JvmStatic
        private fun singleRunDepthRating(solver: Solver<Cube>): Int {
            return when(solver) {
                is ClassicalAStarSolver -> 7
                is FrontierSearchSolver -> 7
                is IterativeDeepeningAStarSolver -> 7
                else -> 7
            }
        }
    }
}