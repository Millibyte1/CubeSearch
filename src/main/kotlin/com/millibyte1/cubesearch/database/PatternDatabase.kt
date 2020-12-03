package com.millibyte1.cubesearch.database

interface PatternDatabase {
    fun getCost(index: Long): Int
}