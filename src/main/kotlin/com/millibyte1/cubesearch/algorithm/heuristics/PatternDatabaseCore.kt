package com.millibyte1.cubesearch.algorithm.heuristics

/**
 * The "core" of a pattern database - persistently stores a byte array representation of a pattern database
 */
abstract class PatternDatabaseCore(val cardinality: Int) {
    /**
     * Writes/overwrites the contents of the pattern database to the persistent store.
     * @param bytes the byte array representation of a pattern database
     */
    abstract fun writeDatabase(bytes: ByteArray)
    /**
     * Reads the pattern database from the persistent store.
     * @return the stored database as a byte array, or null if there's no complete database stored
     */
    abstract fun readDatabase(): ByteArray?

    /** Returns whether the core currently contains a complete database.
     * @return false if there's a complete and accessible database stored in the core, else true
     */
    abstract fun isEmpty(): Boolean
}