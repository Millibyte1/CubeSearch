package com.millibyte1.cubesearch.cube

/**
 * Marker interface for a standard 3x3 Rubik's cube that implements immutable twists.
 * @param T the implementation class
 */
interface StandardCube<T : StandardCube<T>> : Cube<T>