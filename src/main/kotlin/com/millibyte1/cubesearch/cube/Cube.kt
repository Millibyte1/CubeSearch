package com.millibyte1.cubesearch.cube

/**
 * A simple interface wrapping a single function, twist, which takes a twist and returns the cube that results from it
 * without modifying the original.
 * @param T the implementation class
 */
interface Cube<T : Cube<T>> {
    /**
     * returns the cube resulting from applying the given twist, without modifying this cube.
     * @param twist the twist we are applying to this cube
     * @return the cube resulting from applying the given twist
     */
    fun twist(twist: Twist): T
}