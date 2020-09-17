package com.millibyte1.cubesearch.cube

/**
 * Abstract class with a single abstract method, twist, which takes a Twist and applies it to this cube.
 * @param T the implementation class
 *
 * @property data a minimal representation of the state of the cube
 *
 * @constructor constructs cube from a copy of the provided data
 * @param data the 6xN array representing the desired cube. Format: (front, back, left, right, up, down)
 */
abstract class AbstractCube<T : AbstractCube<T>> internal constructor(data: Array<IntArray>) {

    var data: Array<IntArray> = data.copy()
        private set

    /**
     * returns the cube resulting from applying the given twist.
     * Depending on implementation, may modify this object or return a new object
     *
     * @param twist the twist we are applying to this cube
     * @return the cube resulting from the applying given twist
     */
    abstract fun twist(twist: Twist): T

}

//extension function to deep copy array
fun Array<IntArray>.copy() = Array(size) { i -> get(i).clone() }