package com.millibyte1.cubesearch.cube

/**
 * Abstract representation of a standard 3x3 Rubik's cube implementing both mutable and immutable twists.
 * @param T the implementation class
 *
 * @constructor constructs cube from the provided data
 * @param data the 6x9 array representing the desired cube. Format: (front, back, left, right, up, down)
 *
 * Required layout of data:
 *  . . . 0 1 2 . . . . . .
 *  . . . 3 U 5 . . . . . .
 *  . . . 6 7 8 . . . . . .
 *  0 1 2 0 1 2 0 1 2 0 1 2
 *  3 L 5 3 F 5 3 R 5 3 B 5
 *  6 7 8 6 7 8 6 7 8 6 7 8
 *  . . . 0 1 2 . . . . . .
 *  . . . 3 D 5 . . . . . .
 *  . . . 6 7 8 . . . . . .
 */
abstract class AbstractMutableStandardCube<T : AbstractMutableStandardCube<T>> internal constructor(
    data: Array<IntArray>
) : AbstractStandardCube<T>(data),
    MutableCube<T> { }