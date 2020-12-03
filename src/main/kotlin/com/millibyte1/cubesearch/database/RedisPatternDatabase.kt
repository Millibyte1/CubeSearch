package com.millibyte1.cubesearch.database

abstract class RedisPatternDatabase : PatternDatabase {
    internal abstract fun setCost(index: Long, value: Int)
    internal abstract fun isPopulated(): Boolean
    internal abstract fun populateDatabase()
}