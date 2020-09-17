package com.millibyte1.cubesearch.util

import com.millibyte1.cubesearch.cube.AbstractCube
import com.millibyte1.cubesearch.cube.AbstractCubeFactory
import com.millibyte1.cubesearch.cube.Twist
import kotlin.random.Random

/**
 * Generates random cubes of a specific difficulty to solve, defined by minimum solution depth.
 * Works for any Cube implementation with a factory. Can reset the RNG and change the generated cube difficulty.
 * Thread-safe.
 * @param T the cube implementation to be generated
 *
 * @property factory the factory to use to create cubes
 * @property random the random number generator being used internally. can be reset.
 * @property seed the seed of the internal RNG
 * @property difficulty the approximate solution length of cubes being generated. if null, random difficulties will be chosen each time nextCube() is called. can be changed.
 *
 * @constructor constructs a new CubeGenerator with the given seed and initial difficulty
 * @param factory the factory to use to create cubes
 * @param seed the seed to create the internal RNG from. if none is provided, a random seed will be used.
 * @param difficulty the approximate solution length of cubes being generated. if null or not provided, random difficulties will be chosen each time nextCube() is called.
 */
//TODO(factories) AbstractCubeFactory that can build a solved cube
class CubeGenerator<T : AbstractCube<T>>(
    private val factory: AbstractCubeFactory<T>,
    private val seed: Int = Random.nextInt(),
    private var difficulty: Int? = null) {

    private var random: Random = Random(seed)

    /**
     * Returns a new randomly generated cube.
     * Uses proper move pruning to attempt to make the difficulty accurate.
     *
     * @return a new randomly generated cube. fully predictable based on seed and iteration.
     */
    @Synchronized
    public fun nextCube(): T {
        //TODO: implement advanced move pruning. determine if difficulty is or could be 100% accurate
        var cube = factory.getSolvedCube()
        val solutionDepth = when(difficulty) {
            null -> Random.nextInt(20)
            else -> difficulty!!
        }

        var options: Array<Twist>
        var previousMove: Twist? = null
        var previousFace: Twist.Face? = null
        for(i in 0..solutionDepth) {
            //performs simple move-pruning on options
            options = when(previousFace) {
                null -> Twist.values()
                        .filter { twist -> Twist.getFace(twist) != previousFace }
                        .toTypedArray()
                else -> Twist.values()
            }
            previousMove = options[random.nextInt(options.size)]
            cube = cube.twist(previousMove)
            previousFace = Twist.getFace(previousMove)
        }
        return cube
    }

    /** Resets the internal RNG. Uses the seed this CubeGenerator was created with. */
    @Synchronized
    public fun reset() {
        this.random = Random(seed)
    }
    /** Changes the difficulty of future cubes generated by this CubeGenerator. */
    @Synchronized
    public fun setDifficulty(difficulty: Int) {
        this.difficulty = difficulty
    }
}