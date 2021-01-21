package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.AnalyzableStandardCube
import com.millibyte1.cubesearch.cube.SmartCube
import com.millibyte1.cubesearch.cube.SmartCubeFactory
import com.millibyte1.cubesearch.util.CubeGenerator
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import redis.clients.jedis.Jedis
import java.io.File

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
        private fun cornerPatternDatabase(): CornerPatternDatabase {
            val generalConfig: Config = ConfigFactory.load("patterndb.conf").getConfig("patterndb")
            val cornerConfig = generalConfig.getConfig("corners-full")

            val searchMode = cornerConfig.getString("search-mode")
            val persistenceMode = generalConfig.getString("persistence-mode")

            val jedis = Jedis()
            val key = cornerConfig.getString("redis-key")

            val file = File("data/corners-full.db")

            val core = when(persistenceMode) {
                "file" -> FileCore(file, 88179840)
                else -> RedisCore(jedis, key, 88179840)
            }
            return CornerPatternDatabase(core, searchMode)
        }

        @JvmStatic
        fun evaluators(): List<CostEvaluator> {
            return listOf(
                ManhattanDistanceCostEvaluator(),
                cornerPatternDatabase()
            )
        }
    }
}