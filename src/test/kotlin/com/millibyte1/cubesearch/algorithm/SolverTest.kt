package com.millibyte1.cubesearch.algorithm

import com.millibyte1.cubesearch.algorithm.heuristics.*
import com.millibyte1.cubesearch.cube.*
import com.millibyte1.cubesearch.util.CubeGenerator

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import redis.clients.jedis.Jedis
import java.io.File

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class SolverTest {

    init { }
    /* =============================================== UNIT TESTS =================================================== */

    @ParameterizedTest
    @MethodSource("solvers")
    @Order(1)
    fun testLengthZeroSolutions(solver: Solver) {
        assertEquals(solver.getSolution(solved()).size, 0)
    }
    @ParameterizedTest
    @MethodSource("solvers")
    @Order(2)
    fun testLengthOneSolutions(solver: Solver) {
        for(solution in lengthOneSolutions()) {
            assertEquals(solver.getSolution(solution.first)[0], Twist.getReverse(solution.second))
        }
    }
    /*
    @ParameterizedTest
    @MethodSource("solvers")
    @Order(3)
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
    */
    @ParameterizedTest
    @MethodSource("solvers")
    @Order(4)
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
    @Order(5)
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

    companion object {

        private val factory = SmartCubeFactory()
        private val generator = CubeGenerator<SmartCube>(factory, -483132)

        private val sevenEdgesStartZero = EdgePatternDatabase.create(FileCore("data/edges-0123456.db"), "dfs", mutableListOf(0, 1, 2, 3, 4, 5, 6))
        //private val sevenEdgesStartTwo = EdgePatternDatabase.create(FileCore("data/edges-2345678.db"), "dfs", mutableListOf(2, 3, 4, 5, 6, 7, 8))
        //private val sevenEdgesStartFour = EdgePatternDatabase.create(FileCore("data/edges-456789A.db"), "dfs", mutableListOf(4, 5, 6, 7, 8, 9, 10))
        private val sevenEdgesStartSix = EdgePatternDatabase.create(FileCore("data/edges-6789AB0.db"), "dfs", mutableListOf(6, 7, 8, 9, 10, 11, 0))
        //private val sevenEdgesStartEight = EdgePatternDatabase.create(FileCore("data/edges-89AB012.db"), "dfs", mutableListOf(8, 9, 10, 11, 0, 1, 2))
        //private val sevenEdgesStartTen = EdgePatternDatabase.create(FileCore("data/edges-AB01234.db"), "dfs", mutableListOf(10, 11, 0, 1, 2, 3, 4))
        private val cornerPatternDatabase = CornerPatternDatabase.create(FileCore("data/corners-full.db"), "dfs", mutableListOf(0, 1, 2, 3, 4, 5, 6, 7))

        //private val twinSevenEdgePatternDatabase = MultiHeuristic(sevenEdgesStartZero, sevenEdgesStartSix)
        private val twinSevenEdgeAndCornerPatternDatabase = MultiHeuristic(sevenEdgesStartZero, sevenEdgesStartSix, cornerPatternDatabase)
        /*private val manyEdgeDatabase = MultiHeuristic(
                sevenEdgesStartZero, sevenEdgesStartTwo, sevenEdgesStartFour,
                sevenEdgesStartSix, sevenEdgesStartEight, sevenEdgesStartTen
        )
        private val manyEdgeDatabaseWithCorners = MultiHeuristic(
                sevenEdgesStartZero, sevenEdgesStartTwo, sevenEdgesStartFour,
                sevenEdgesStartSix, sevenEdgesStartEight, sevenEdgesStartTen,
                cornerPatternDatabase
        )*/
        
        /* ====================================== TEST FIXTURES ===================================================== */

        private fun solved(): AnalyzableStandardCube {
            return factory.getSolvedCube()
        }

        private fun standardCostFunction(): CostEvaluator {
            //return cornerPatternDatabase()
            /*
            return MultiPatternDatabase(
                EdgePatternDatabase.create(FileCore("data/edges-012345.db"), "dfs", mutableListOf(0, 1, 2, 3, 4, 5)),
                CornerPatternDatabase.create(FileCore("data/corners-full.db"), "dfs", mutableListOf(0, 1, 2, 3, 4, 5, 6, 7))
            )

             */
            //return manyEdgeDatabaseWithCorners
            return twinSevenEdgeAndCornerPatternDatabase
        }
        @JvmStatic
        /** Returns a list of CostEvaulators to test the solvers with */
        private fun costEvaluators(): List<CostEvaluator> {
            return listOf(
                //cornerPatternDatabase,
                //manyEdgeDatabaseWithCorners,
                twinSevenEdgeAndCornerPatternDatabase
            )
        }

        @JvmStatic
        /** Returns a list of solvers using an already tested CostEvaluator */
        private fun solvers(): List<Solver> {
            val solvers = ArrayList<Solver>()
            for(evaluator in costEvaluators()) solvers.add(IterativeDeepeningAStarSolver(evaluator))
            return solvers
            //return listOf(IterativeDeepeningAStarSolver(standardCostFunction()))
            /*return listOf(ClassicalAStarSolver(standardCostFunction()),
                          FrontierSearchSolver(standardCostFunction()),
                          IterativeDeepeningAStarSolver(standardCostFunction()))
             */
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
                is ClassicalAStarSolver -> 8
                is FrontierSearchSolver -> 8
                is IterativeDeepeningAStarSolver -> 15
                else -> 8
            }
        }
        @JvmStatic
        private fun singleRunDepthRating(solver: Solver): Int {
            return when(solver) {
                is ClassicalAStarSolver -> 8
                is FrontierSearchSolver -> 10
                is IterativeDeepeningAStarSolver -> 15
                else -> 10
            }
        }
    }
}