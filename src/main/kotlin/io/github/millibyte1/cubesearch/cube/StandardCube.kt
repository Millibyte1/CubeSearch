package io.github.millibyte1.cubesearch.cube

/**
 * Marker interface for a standard 3x3 Rubik's cube.
 */
interface StandardCube : Cube {
    override fun twist(twist: Twist): StandardCube
    override fun twistNoCopy(twist: Twist): StandardCube
}