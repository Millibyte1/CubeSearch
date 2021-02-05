package com.millibyte1.cubesearch.algorithm.heuristics

/**
 * The "core" of a pattern database - persistently stores a byte array representation of a pattern database
 */
interface PatternDatabaseCore {
    /**
     * Writes/overwrites the contents of the pattern database to the persistent store.
     * @param bytes the byte array representation of a pattern database
     */
    fun writeDatabase(bytes: ByteArray)
    /**
     * Reads the pattern database from the persistent store.
     * @return the stored database as a byte array, or null if there's nothing stored
     */
    fun readDatabase(): ByteArray?
}