package com.millibyte1.cubesearch.util

import com.millibyte1.cubesearch.cube.*

typealias Path = MutableList<Twist>

data class PathWithBack(val path: Path, val back: AnalyzableStandardCube) {
    fun size(): Int {
        return path.size
    }

    fun add(twist: Twist): PathWithBack {
        val newPath = path.copy()
        newPath.add(twist)
        return PathWithBack(newPath, back.twist(twist))
    }
}

object SolverUtils {

    /**
     * Returns the list of twists that could be productive.
     * If this is the first move, branching factor is 18.
     * If there have been two moves in a row on the same axis, branching factor is 12.
     * Else, branching factor is 15.
     * After the first two moves, the average branching factor will be 14.4
     */
    fun getOptions(face1Previous: Twist.Face?, face2Previous: Twist.Face?): Array<Twist> {
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
    fun <T : StandardCube<T>> realizePath(initial: T, path: Path): T {
        var cube = initial
        for(twist in path) cube = cube.twist(twist)
        return cube
    }
}

internal fun <E> MutableList<E>.copy(): MutableList<E> {
    val newList = ArrayList<E>()
    for(item in this) newList.add(item)
    return newList
}

internal fun failNotSolvable(): IllegalArgumentException {
    return IllegalArgumentException("Error: cube is not solvable")
}
internal fun failCouldNotSolve(): RuntimeException {
    return RuntimeException("Error: Could not solve cube that should be solvable")
}