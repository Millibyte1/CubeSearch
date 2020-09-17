package com.millibyte1.cubesearch.cube

/**
 * AbstractFactory for AbstractCube implementations
 * @param T the cube implementation being used
 */
abstract class AbstractCubeFactory<T : AbstractCube<T>>() {
    /**
     * Constructs a cube from the contents of [data]
     * @param data the 6xN array representing the faces of the cube
     * @return the constructed cube
     */
    abstract fun getCube(data: Array<IntArray>): T
    /** Returns a copy of this cube
     * @param cube the cube to copy
     * @return the constructed cube
     */
    abstract fun getCube(cube: T): T
    /**
     * Returns the solved cube
     * @return the solved cube
     */
    abstract fun getSolvedCube(): T
}