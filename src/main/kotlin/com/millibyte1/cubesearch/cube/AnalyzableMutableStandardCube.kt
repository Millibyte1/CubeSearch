package com.millibyte1.cubesearch.cube

/**
 * Marker interface for a standard 3x3 Rubik's cube that implements both mutable and immutable twists and is analyzable.
 * @param T the implementation class
 */
interface AnalyzableMutableStandardCube<T : AnalyzableMutableStandardCube<T>> : MutableStandardCube<T>, Analyzable