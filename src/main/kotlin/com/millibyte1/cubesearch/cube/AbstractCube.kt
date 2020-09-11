package com.millibyte1.cubesearch.cube

/**
 * Interface with a single function, twist, which takes a Twist and applies it to this object
 */
abstract class AbstractCube<T : AbstractCube<T>>(data: Array<IntArray>) {

    val data: Array<IntArray>

    init {
        this.data = data.copy()
    }
    
    /**
     * returns the cube resulting from applying the given twist
     *
     * @param twist the twist we are applying to this cube
     * @return the cube resulting from the applying given twist
     */
    abstract fun twist(twist: Twist): AbstractCube<T>
}

//extension function to deep copy array
fun Array<IntArray>.copy() = Array(size) { i -> get(i).clone() }