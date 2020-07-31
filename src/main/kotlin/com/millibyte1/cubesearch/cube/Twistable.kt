package com.millibyte1.cubesearch.cube

import com.millibyte1.cubesearch.cube.Twist

/**
 * Interface with a single function, twist, which takes a Twist and applies it to this object
 */
interface Twistable {
    /**
     * returns the cube resulting from applying the given twist
     *
     * @param twist the twist we are applying to this cube
     * @return the cube resulting from the applying given twist
     */
    fun twist(twist: Twist): Twistable
}