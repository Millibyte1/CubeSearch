package com.millibyte1.cubesearch.algorithm.heuristics

import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * A persistent file-based store for a pattern database.
 *
 * @constructor
 * @param file the file to use as the store
 * @param cardinality the number of byte entries in the fully populated database
 */
class FileCore(
    private val file: File,
    cardinality: Int
) : PatternDatabaseCore(cardinality) {
    /**
     * Constructs a FileCore with a file at the specified path
     * @param pathname the pathname of the file to use as the store
     * @param cardinality the number of byte entries in the fully populated database
     */
    constructor(pathname: String, cardinality: Int) : this(File(pathname), cardinality)

    /**
     * Writes/overwrites the contents of the pattern database to the file.
     * @param bytes the byte array representation of the pattern database
     * @throws IOException if an IOException occurs while trying to write the database to the file
     * @throws IllegalArgumentException if the size of the byte array is not equal to the cardinality of the database
     */
    @Throws(IOException::class, IllegalArgumentException::class)
    override fun writeDatabase(bytes: ByteArray) {
        if(bytes.size != cardinality) throw IllegalArgumentException("Error: size of byte array does not match the cardinality of the database.")
        try {
            val fileStream = FileOutputStream(file)
            fileStream.write(bytes)
            fileStream.close()
        }
        catch (e: Exception) {
            throw e
        }
    }

    /**
     * Reads the pattern database from the file.
     * @return the stored database as a byte array, or null if there's no complete database stored
     * @throws IOException if an IOException occurs while trying to read the database from the file
     */
    @Throws(IOException::class)
    override fun readDatabase(): ByteArray? {
        try {
            if (file.exists()) {
                val bytes = FileUtils.readFileToByteArray(file)
                if(bytes.size == cardinality) return bytes
            }
        }
        catch(e: Exception) {
            throw e
        }
        return null
    }
    /** Returns whether the core currently contains a complete database.
     * @return false if there's a complete and accessible database stored in the core, else true
     * @throws IOException if an IOException occurs while trying to determine if the database is populated
     */
    override fun isEmpty(): Boolean {
        try {
            if(file.exists()) {
                val bytes = FileUtils.readFileToByteArray(file)
                if(bytes.size == cardinality) return false
            }
        }
        catch(e: Exception) {
            throw e
        }
        return true
    }
}
