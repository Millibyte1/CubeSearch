package com.millibyte1.cubesearch.cube

/**
 * Abstract interface for factories of different cube implementation types
 */
interface CubeFactory {
    /**
     * Constructs a cube from the contents of [data]
     * @param data the 6xN array representing the faces of the cube
     * @return the constructed cube
     * @throws IllegalArgumentException if the format of the provided data array doesn't correspond to the target type
     */
    fun getCube(data: Array<IntArray>): Cube
    /** Returns a copy of this cube
     * @param cube the cube to copy
     * @return the constructed cube
     * @throws IllegalArgumentException if the factory cannot produce a cube of the target type from [cube]
     */
    fun getCube(cube: Cube): Cube
    /**
     * Returns the solved cube
     * @return the solved cube
     */
    fun getSolvedCube(): Cube
}