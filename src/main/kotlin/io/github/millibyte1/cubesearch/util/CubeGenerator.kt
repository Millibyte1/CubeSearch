package io.github.millibyte1.cubesearch.util

import io.github.millibyte1.cubesearch.cube.Cube
import io.github.millibyte1.cubesearch.cube.StandardCube
import io.github.millibyte1.cubesearch.cube.CubeFactory
import io.github.millibyte1.cubesearch.cube.Twist
import kotlin.random.Random

/**
 * Generates random cubes of a specific walkLength to solve, defined by minimum solution depth.
 * Works for any Cube implementation with a factory. Can reset the RNG and change the generated cube walkLength.
 * Thread-safe.
 * @param T the cube implementation to be generated
 *
 * @property factory the factory to use to create cubes
 * @property random the random number generator being used internally. can be reset.
 * @property seed the seed of the internal RNG
 * @property walkLength the number of random twists performed when generating the next random cube. can be changed.
 *
 */
class CubeGenerator<T : Cube> {

    private val factory: CubeFactory
    private var random: Random
    private val seed: Int

    private var walkLength: Int = 100

    /**
     * constructs a new CubeGenerator with the given seed and initial walkLength
     * @param factory the factory to use to create cubes
     * @param seed the seed to create the internal RNG from. if none is provided, a random seed will be used.
     * @property walkLength the number of random twists performed when generating the next random cube. can be changed.
     * @throws IllegalArgumentException if [walkLength] is negative.
     */
    @Throws(IllegalArgumentException::class)
    constructor(factory: CubeFactory, seed: Int = Random.nextInt(), walkLength: Int = 100) {
        if(walkLength < 0) throw IllegalArgumentException("Error: negative walk length provided")
        this.factory = factory
        this.seed = seed
        this.random = Random(seed)
        this.walkLength = walkLength
    }

    /**
     * Returns a new randomly generated cube.
     * Uses proper move pruning to attempt to make the walkLength accurate.
     *
     * @return a new randomly generated cube. fully predictable based on seed and iteration.
     */
    @Synchronized
    fun nextCube(): T {

        var cube = factory.getSolvedCube()
        var options: Array<Twist>
        var previousMove: Twist?
        var face1Previous: Twist.Face? = null
        var face2Previous: Twist.Face? = null

        //generates a random sequence of twists
        for(i in 1..walkLength) {
            //eliminates twists that would necessarily result in a cube that could be reached in fewer moves
            options = SolverUtils.getOptions(face1Previous, face2Previous)
            //performs the twist and updates move history
            face2Previous = face1Previous
            previousMove = options[random.nextInt(options.size)]
            face1Previous = Twist.getFace(previousMove)
            cube.twistNoCopy(previousMove)
        }

        return cube as T
    }

    /** Resets the internal RNG. Uses the seed this CubeGenerator was created with. */
    @Synchronized
    fun reset() {
        this.random = Random(seed)
        setWalkLength(100)
    }
    /** Changes the walkLength of future cubes generated by this CubeGenerator. */
    @Synchronized
    fun setWalkLength(walkLength: Int) {
        this.walkLength = walkLength
    }

}