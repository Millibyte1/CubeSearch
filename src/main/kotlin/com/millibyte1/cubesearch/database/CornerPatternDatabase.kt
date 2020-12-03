package com.millibyte1.cubesearch.database

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

    override fun populateDatabase() {
        TODO("Not yet implemented")
    }
}