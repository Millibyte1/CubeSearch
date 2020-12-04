package com.millibyte1.cubesearch.database

import com.millibyte1.cubesearch.cube.Cube
import com.millibyte1.cubesearch.cube.CubeFactory
import java.util.Queue
import java.util.concurrent.ArrayBlockingQueue

object CornerPatternDatabase : RedisPatternDatabase() {

    private const val CARDINALITY = 88179840

    override fun getCost(index: Long): Int {
        TODO("Not yet implemented")
    }

    override fun setCost(index: Long, value: Int) {
        TODO("Not yet implemented")
    }

    override fun isPopulated(): Boolean {
        TODO("Not yet implemented")
    }
    //should only require a BFS to depth 10 to generate all possible corner configurations
    override fun populateDatabase() {
        val candidates: Queue<Cube> = ArrayBlockingQueue(1000)
        candidates.add(CubeFactory().getSolvedCube())


    }
}