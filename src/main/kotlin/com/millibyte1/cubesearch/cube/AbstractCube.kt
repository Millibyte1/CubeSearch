package com.millibyte1.cubesearch.cube

/**
 * Abstract class with a single abstract method, twist, which takes a Twist and applies it to this cube.
 * @param T the implementation class
 *
 * @property data a minimal representation of the state of the cube
 *
 * @constructor constructs cube from a copy of data
 * @param data the 6x9 array representing the desired cube. Format: (front, back, left, right, up, down)
 */
abstract class AbstractCube<T : AbstractCube<T>>(data: Array<IntArray>) {

    val data: Array<IntArray> = data.copy()

    /**
     * copy constructor
     * @param cube the cube to copy
     */
    public constructor(cube: Cube) : this(cube.data)
    
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