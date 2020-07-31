package com.millibyte1.cubesearch.cube

import java.io.Serializable

import com.millibyte1.cubesearch.cube.Twist

/**
 * Immutable class representing the configuration of a 3x3 Rubik's cube
 *
 * @property data a minimal representation of the state of the cube
 *
 * @constructor constructs cube from a copy of [data]
 * @param data the 6x9 array representing the desired cube. Format: (front, back, left, right, up, down)
 */
//TODO(tuples): switch from arrays to tuples, would be inefficient to check validity with built-in arrays
class Cube constructor(data: Array<IntArray>) : Twistable, Serializable {

    val data: Array<IntArray>

    init {
        //extension function to deep copy array
        fun Array<IntArray>.copy() = Array(size) { it -> get(it).clone() }
        this.data = data.copy()
    }

    /**
     * copy constructor
     * @param cube the cube to copy
     */
    constructor(cube: Cube) : this(cube.data) { }

    /**
     * takes a twist and returns the cube that results from it
     * pre: N/A
     * post: this cube is unchanged
     * @param twist the twist to be performed
     * @return the cube that results from applying this twist
     */
    override fun twist(twist: Twist): Twistable {
        //TODO(tuples): switch to tuples then implement twists
    }

    /**
     * overridden equality to check whether the cubes have the same configuration
     * pre: cube is
     */

}

