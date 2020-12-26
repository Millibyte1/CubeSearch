package com.millibyte1.cubesearch.cube

/**
 * Marker interface for a standard 3x3 Rubik's cube that implements both mutable and immutable twists.
 * @param T the implementation class
 */
interface MutableStandardCube<T : MutableStandardCube<T>> : StandardCube<T>, MutableCube<T>