package com.millibyte1.cubesearch.cube

//TODO: describe the format of the tile array
/**
 * A simple interface wrapping three functions:
 *
 * twist, which takes a twist and returns the cube that results from it without modifying the original;
 *
 * twistNoCopy, which takes a twist and applies it to the cube, modifying it but also returning it;
 *
 * and getTiles, which returns a 2D array representation of the tiles on the cube
 */
interface Cube {
    /**
     * returns the cube resulting from applying the given twist, without modifying this cube.
     * @param twist the twist we are applying to this cube
     * @return the cube resulting from applying the given twist
     */
    fun twist(twist: Twist): Cube

    /**
     * Applies the given twist to this cube. Modifies this, but returns this as well.
     * @param twist the twist we are applying to this cube
     * @return this cube
     */
    fun twistNoCopy(twist: Twist): Cube

    /**
     * Returns a 2D array representation of the tiles on this cube. Depending on the implementation,
     * modifying this could invalidate the cube
     * @return a 2D array representation of the tiles on this cube.
     */
    fun getTiles(): Array<IntArray>
}