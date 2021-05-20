package io.github.millibyte1.cubesearch.algorithm.heuristics

import io.github.millibyte1.array64.ByteArray64
import io.github.millibyte1.array64.FastByteArray64
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
    private val key: String
) : PatternDatabaseCore {

    /**
     * Constructs a RedisCore using the specified hash key with a Jedis instance at the specified host address.
     * @param host the listening address of the Redis instance
     * @param port the listening port of the Redis instance
     * @param key the key to store this database at
     * @param cardinality the number of byte entries in the fully populated database
     */
    constructor(host: String, port: Int, key: String) : this(Jedis(host, port), key)

    /**
     * Writes/overwrites the contents of the pattern database to the Redis store.
     * @param bytes the byte array representation of the pattern database
     */
    override fun writeDatabase(bytes: ByteArray) {
        for(i in bytes.indices) jedis.hset(key, i.toString(), bytes[i].toString())
    }
    /**
     * Reads the pattern database from the Redis store.
     * @return the stored database as a byte array, or null if there's nothing stored
     */
    override fun readDatabase(): ByteArray? {
        val bytes = ByteArray(jedis.hlen(key).toInt())
        val map = jedis.hgetAll(key)
        for(pair in map) bytes[pair.key.toInt()] = pair.value.toByte()
        return bytes
    }

    override fun writeDatabase64(bytes: FastByteArray64) {
        TODO("Not yet implemented")
    }

    override fun readDatabase64(): FastByteArray64? {
        TODO("Not yet implemented")
    }
}