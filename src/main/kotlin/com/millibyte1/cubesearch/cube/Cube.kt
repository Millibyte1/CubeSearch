package com.millibyte1.cubesearch.cube

/**
 * A simple interface wrapping two functions:
 *
 * twist, which takes a twist and returns the cube that results from it without modifying the original;
 *
 * and twistNoCopy, which takes a twist and applies it to the cube, modifying it but also returning it.
 *
 * @param T the implementation class
 */
interface Cube<T : Cube<T>> {
    /**
     * returns the cube resulting from applying the given twist, without modifying this cube.
     * @param twist the twist we are applying to this cube
     * @return the cube resulting from applying the given twist
     */
    fun twist(twist: Twist): T

    /**
     * Applies the given twist to this cube. Modifies this, but returns this as well.
     * @param twist the twist we are applying to this cube
     * @return this cube
     */
    fun twistNoCopy(twist: Twist): T
}