package com.millibyte1.cubesearch.algorithm.heuristics

import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * A persistent file-based store for a pattern database.
 * @constructor
 * @param file the file to use as the store
 */
class FileCore(private val file: File) : PatternDatabaseCore {
    /**
     * Constructs a FileCore with a file at the specified path
     * @param pathname the pathname of the file to use as the store
     */
    constructor(pathname: String) : this(File(pathname))

    /**
     * Writes/overwrites the contents of the pattern database to the file.
     * @param bytes the byte array representation of the pattern database
     * @throws IOException if an IOException occurs while trying to write the database to the file
     */
    @Throws(IOException::class)
    override fun writeDatabase(bytes: ByteArray) {
        val fileStream = FileOutputStream(file)
        fileStream.write(bytes)
        fileStream.close()
    }

    /**
     * Reads the pattern database from the file.
     * @return the stored database as a byte array, or null if there's nothing
     * @throws IOException if an IOException occurs while trying to read the database from the file
     */
    @Throws(IOException::class)
    override fun readDatabase(): ByteArray? {
        if (file.exists()) return FileUtils.readFileToByteArray(file)
        return null
    }
}
