package com.millibyte1.cubesearch.util

import com.millibyte1.cubesearch.cube.Cube
import com.millibyte1.cubesearch.cube.Twist
import com.millibyte1.cubesearch.cube.Twist.Face

internal typealias CenterCubie = Cubie.CenterCubie
internal typealias EdgeCubie = Cubie.EdgeCubie
internal typealias CornerCubie = Cubie.CornerCubie

/**
 * A simple data class representing a snapshot of a tile (a sticker on a cubie)
 * @property face the face of the cube this tile is currently on
 * @property index the index of the tile within the face
 * @property color the color of the tile
 */
data class Tile(val face: Face, val index: Int, val color: Int) : Comparable<Tile> {
    /** A comparison based on the natural ordering of Twist.Face */
    override fun compareTo(other: Tile): Int {
        return when {
            face < other.face -> -1
            face > other.face -> 1
            else -> 0
        }
    }
}

/**
 * A simple algebraic data type to represent the variant cubies (edge, corner, and center cubies)
 * Important for CubeUtils functions (validating a cube, heuristic for search)
 */
sealed class Cubie {
    data class CenterCubie(val tile1: Tile) : Cubie()
    data class EdgeCubie(val tile1: Tile, val tile2: Tile) : Cubie()
    data class CornerCubie(val tile1: Tile, val tile2: Tile, val tile3: Tile) : Cubie()
}

/**
 * Takes a cube and a tile and returns the cubie associated with the tile
 * Effectively a factory function for Cubies. TODO: refactor Cubie stuff
 * @return the appropriate cubie, if one exists
 * @return the appropriate cubie
 * @throws IllegalArgumentException if the given tile does not exist on this cube
 */
@Throws(IllegalArgumentException::class)
fun getCubie(cube: Cube, tile: Tile): Cubie {

}

/**
 * Takes a list of tiles and returns the cubie associated with these tiles
 * Effectively a factory function for Cubies.
 * @param tiles the tiles the desired cubie consists of
 * @return the appropriate cubie
 * @throws IllegalArgumentException if the tiles passed in do not form a valid cubie
 */
@Throws(IllegalArgumentException::class)
fun getCubie(vararg tiles: Tile): Cubie {
    //sorts the tiles to reduce the size of any databases
    tiles.sort()
    //returns the variant
    return when(tiles.size) {
        1 -> getCenterCubie(tiles[0])
        2 -> getEdgeCubie(tiles[0], tiles[1])
        3 -> getCornerCubie(tiles[0], tiles[1], tiles[2])
        else -> throw IllegalArgumentException("invalid number of args: cubies must have 1, 2, or 3 tiles")
    }
}

/**
 * Takes three tiles and returns the appropriate corner cubie
 * Precondition: tiles are already in sorted order
 * @param tile1 the first tile
 * @param tile2 the second tile
 * @param tile3 the third tile
 * @return the appropriate corner cubie
 * @throws IllegalArgumentException if the tiles passed in do not form a valid corner cubie
 */
@Throws(IllegalArgumentException::class)
internal fun getCornerCubie(tile1: Tile, tile2: Tile, tile3: Tile): CornerCubie {
    if(isOnSameCornerCubie(tile1, tile2, tile3)) return CornerCubie(tile1, tile2, tile3)
    throw failNotAdjacent()
}
/**
 * Returns whether the tiles are on the same edge cubie
 */
fun isOnSameCornerCubie(tile1: Tile, tile2: Tile, tile3: Tile): Boolean {
    return false //TODO
}
/**
 * Takes two tiles and returns the appropriate edge cubie
 * Precondition: tiles are already in sorted order
 * @param tile1 the first tile
 * @param tile2 the second tile
 * @return the appropriate edge cubie
 * @throws IllegalArgumentException if the tiles passed in do not form a valid edge cubie
 */
@Throws(IllegalArgumentException::class)
internal fun getEdgeCubie(tile1: Tile, tile2: Tile): EdgeCubie {
    if(isOnSameEdgeCubie(tile1, tile2)) return EdgeCubie(tile1, tile2)
    throw failNotAdjacent()
}
/**
 * Returns whether the tiles are on the same edge cubie
 */
fun isOnSameEdgeCubie(tile1: Tile, tile2: Tile): Boolean {
    return false //TODO
}
/**
 * Takes a center tile and returns the appropriate center cubie
 * @param tile the tile of the desired cubie
 * @return the appropriate center cubie
 * @throws IllegalArgumentException if the tile passed in is not at the center of its face
 */
@Throws(IllegalArgumentException::class)
internal fun getCenterCubie(tile: Tile): CenterCubie {
    if(tile.index == 4) return Cubie.CenterCubie(tile)
    throw IllegalArgumentException("invalid args: provided tile is not at the center of its face")
}

/** returns an exception for non-adjacent tiles as parameters */
private fun failNotAdjacent(): IllegalArgumentException {
    return IllegalArgumentException("invalid args: provided tiles are not adjacent")
}