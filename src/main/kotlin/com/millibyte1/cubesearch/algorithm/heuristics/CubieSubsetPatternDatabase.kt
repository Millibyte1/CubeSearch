package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.cubesearch.cube.AnalyzableStandardCube

class CubieSubsetPatternDatabase(
    val cardinality: Int,
    val searchMode: String,
    val persistenceMode: String
) : AbstractPatternDatabase() {
    override fun getCost(index: Int): Byte {
        TODO("Not yet implemented")
    }

    override fun getIndex(cube: AnalyzableStandardCube): Int {
        TODO("Not yet implemented")
    }
}