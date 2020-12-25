package com.millibyte1.cubesearch.cube

/**
 * A simple mutable interface wrapping a single function, twistNoCopy, which takes a twist and applies it directly to this cube, modifying it.
 * Inherits a single function twist from the interface Cube, which returns a copy instead of modifying this cube.
 */
interface MutableCube<T : MutableCube<T>> : Cube<T> {
    /**
     * Applies the given twist to this cube. Modifies this, but returns this as well.
     * @param twist the twist we are applying to this cube
     * @return this cube
     */
    fun twistNoCopy(twist: Twist): T
}