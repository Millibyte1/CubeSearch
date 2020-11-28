package com.millibyte1.cubesearch.util

import com.millibyte1.cubesearch.cube.AbstractCube
import com.millibyte1.cubesearch.cube.Cube
import com.millibyte1.cubesearch.cube.CubeFactory
import com.millibyte1.cubesearch.cube.Twist
import com.millibyte1.cubesearch.solver.CostEvaluator

typealias Path = MutableList<Twist>

data class PathWithBack(val path: Path, val back: Cube)

object SolverUtils {

    /**
     * Returns the list of twists that could be productive.
     * If this is the first move, branching factor is 18.
     * If there have been two moves in a row on the same axis, branching factor is 12.
     * Else, branching factor is 15.
     * After the first two moves, the average branching factor will be 14.4
     */
    fun getOptions(face1Previous: Twist.Face?, face2Previous: Twist.Face?): Array<Twist> {
        //TODO: find a way to improve branching factor
        return when {
            (face1Previous == null) && (face2Previous == null) -> Twist.values()
            (face2Previous == null) ->
                Twist.values()
                        .filter { twist -> Twist.getFace(twist) != face1Previous }
                        .toTypedArray()
            (face1Previous == Twist.getOppositeFace(face2Previous)) ->
                Twist.values()
                        .filter { twist -> Twist.getFace(twist) != face1Previous &&
                                Twist.getFace(twist) != face2Previous }
                        .toTypedArray()
            else -> Twist.values()
        }
    }

    /**
     * Gets the cube that this path of twists results in, starting from the given initial cube
     * @param initial the cube to start from
     * @param path the sequence of twists to apply to this cube
     * @return the cube that this path yields (the "back" of the path, if it were cubes instead of twists)
     */
    fun <T : AbstractCube<T>> realizePath(initial: T, path: Path): T {
        var cube = initial
        for(twist in path) cube = cube.twist(twist)
        return cube
    }
}