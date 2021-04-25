package com.millibyte1.cubesearch.algorithm.heuristics

import com.millibyte1.array64.ByteArray64

/**
 * The "core" of a pattern database - persistently stores a byte array representation of a pattern database
 */
interface PatternDatabaseCore {
    /**
     * Writes/overwrites the contents of the pattern database to the persistent store.
     * @param bytes the [ByteArray] representation of a pattern database
     */
    fun writeDatabase(bytes: ByteArray)
    /**
     * Reads the pattern database from the persistent store.
     * @return the stored database as a [ByteArray], or null if there's nothing stored
     */
    fun readDatabase(): ByteArray?
    /**
     * Writes/overwrites the contents of the pattern database to the persistent store.
     * @param bytes the [ByteArray64] representation of a pattern database
     */
    fun writeDatabase64(bytes: ByteArray64)
    /**
     * Reads the pattern database from the persistent store.
     * @return the stored database as a [ByteArray64], or null if there's nothing stored
     */
    fun readDatabase64(): ByteArray64?
}