package com.millibyte1.cubesearch.cube

import com.millibyte1.cubesearch.cube.Twist

/**
 * Immutable class representing the configuration of a Rubik's cube
 *
 * @property data a minimal representation of the state of the cube
 */
class Cube {

    private val data: Array<IntArray>

    /**
     * literally just copies the data into this cube, TODO make private and add factory methods
     */
    constructor(data: Array<IntArray>) {
        this.data = data
    }

    /**
     * returns the cube resulting from applying the given twist
     *
     * @param twist the twist we are applying to this cube
     * @return the cube resulting from the applying given twist
     */
    fun move(twist: Twist): Cube {
        //TODO implement the twists
        return this
    }
}

