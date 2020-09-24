package com.millibyte1.cubesearch.solver

import com.millibyte1.cubesearch.cube.AbstractCube
import com.millibyte1.cubesearch.cube.Twist

typealias Solution = List<Twist>

interface Solver<T : AbstractCube<T>> {
    fun getSolution(cube: T): List<Twist>
    fun getSolutions(cube: T): Set<List<Twist>>
}