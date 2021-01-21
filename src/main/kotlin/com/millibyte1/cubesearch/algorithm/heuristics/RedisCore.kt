package com.millibyte1.cubesearch.algorithm.heuristics

import redis.clients.jedis.Jedis

/**
 * A persistent redis-based store for a pattern database.
 *
 * @constructor
 * @param jedis the Jedis instance to use for storing the pattern database
 * @param key the key to store this database at
 * @param cardinality the number of byte entries in the fully populated database
 */
//TODO: Implement some sort of sharding for improved lookup times
class RedisCore(
    private val jedis: Jedis = Jedis(),
    private val key: String,
    cardinality: Int
) : PatternDatabaseCore(cardinality) {

    /**
     * Constructs a RedisCore using the specified hash key with a Jedis instance at the specified host address.
     * @param host the listening address of the Redis instance
     * @param port the listening port of the Redis instance
     * @param key the key to store this database at
     * @param cardinality the number of byte entries in the fully populated database
     */
    constructor(host: String, port: Int, key: String, cardinality: Int) : this(Jedis(host, port), key, cardinality)

    /**
     * Writes/overwrites the contents of the pattern database to the Redis store.
     * @param bytes the byte array representation of the pattern database
     * @throws IllegalArgumentException if the size of the byte array is not equal to the cardinality of the database
     */
    @Throws(IllegalArgumentException::class)
    override fun writeDatabase(bytes: ByteArray) {
        if(bytes.size != cardinality) throw IllegalArgumentException("Error: size of byte array does not match the cardinality of the database.")
        for(i in 0 until cardinality) jedis.hset(key, i.toString(), bytes[i].toString())
    }
    /**
     * Reads the pattern database from the Redis store.
     * @return the stored database as a byte array, or null if there's no complete database stored
     */
    override fun readDatabase(): ByteArray? {
        if(jedis.hlen(key).toInt() == cardinality) {
            val bytes = ByteArray(cardinality)
            val map = jedis.hgetAll(key)
            for(pair in map) bytes[pair.key.toInt()] = pair.value.toByte()
            return bytes
        }
        return null
    }

    /** Returns whether the core currently contains a complete database.
     * @return false if there's a complete and accessible database stored in the core, else true
     */
    override fun isEmpty(): Boolean {
        return jedis.hlen(key).toInt() != cardinality
    }
}