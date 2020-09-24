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
 * Determines whether the tiles are on the same edge cubie
 * @param tile1 the first tile
 * @param tile2 the second tile
 * @return whether the provided tiles are on the same edge cubie
 */
fun isOnSameEdgeCubie(tile1: Tile, tile2: Tile): Boolean {
    return isOnEdgeCubie(tile1) && isOnSameCubie(tile1, tile2)
}

/**
 * Determines whether the tiles are on the same corner cubie
 * @param tile1 the first tile
 * @param tile2 the second tile
 * @return whether the provided tiles are on the same corner cubie
 */
fun isOnSameCornerCubie(tile1: Tile, tile2: Tile, tile3: Tile): Boolean {
    return isOnCornerCubie(tile1) && isOnSameCubie(tile1, tile2) && isOnSameCubie(tile1, tile3)
}
/** Returns whether this tile is on an edge cubie */
fun isOnEdgeCubie(tile: Tile): Boolean {
    return when(tile.index) {
        1, 3, 5, 7 -> true
        else -> false
    }
}
/** Returns whether this tile is on a corner cubie */
fun isOnCornerCubie(tile: Tile): Boolean {
    return when(tile.index) {
        0, 2, 6, 8 -> true
        else -> false
    }
}
/**
 * Determines whether the tiles are on the same cubie
 * TODO: clean up this monster of a function
 */
internal fun isOnSameCubie(tile1: Tile, tile2: Tile): Boolean {
    return when(tile1.face) {
        Face.FRONT -> {
            when(tile1.index) {
                //Edges
                1 -> (tile2.face == Face.UP && tile2.index == 7)
                5 -> (tile2.face == Face.RIGHT && tile2.index == 3)
                7 -> (tile2.face == Face.DOWN && tile2.index == 1)
                3 -> (tile2.face == Face.LEFT && tile2.index == 5)
                //Corners
                0 -> (tile2.face == Face.UP && tile2.index == 6) ||
                        (tile2.face == Face.LEFT && tile2.index == 2)
                2 -> (tile2.face == Face.UP && tile2.index == 8) ||
                        (tile2.face == Face.RIGHT && tile2.index == 0)
                8 -> (tile2.face == Face.DOWN && tile2.index == 2) ||
                        (tile2.face == Face.RIGHT && tile2.index == 6)
                6 -> (tile2.face == Face.DOWN && tile2.index == 0) ||
                        (tile2.face == Face.LEFT && tile2.index == 8)
                else -> false
            }
        }
        Face.BACK -> {
            when(tile1.index) {
                //Edges
                1 -> (tile2.face == Face.UP && tile2.index == 1)
                5 -> (tile2.face == Face.LEFT && tile2.index == 3)
                7 -> (tile2.face == Face.DOWN && tile2.index == 7)
                3 -> (tile2.face == Face.RIGHT && tile2.index == 5)
                //Corners
                0 -> (tile2.face == Face.RIGHT && tile2.index == 2) ||
                        (tile2.face == Face.UP && tile2.index == 2)
                2 -> (tile2.face == Face.LEFT && tile2.index == 0) ||
                        (tile2.face == Face.UP && tile2.index == 0)
                8 -> (tile2.face == Face.LEFT && tile2.index == 6) ||
                        (tile2.face == Face.DOWN && tile2.index == 6)
                6 -> (tile2.face == Face.RIGHT && tile2.index == 8) ||
                        (tile2.face == Face.DOWN && tile2.index == 8)
                else -> false
            }
        }
        Face.LEFT -> {
            when(tile1.index) {
                //Edges
                1 -> (tile2.face == Face.UP && tile2.index == 3)
                5 -> (tile2.face == Face.FRONT && tile2.index == 3)
                7 -> (tile2.face == Face.DOWN && tile2.index == 3)
                3 -> (tile2.face == Face.BACK && tile2.index == 5)
                //Corners
                0 -> (tile2.face == Face.UP && tile2.index == 0) ||
                        (tile2.face == Face.BACK && tile2.index == 2)
                2 -> (tile2.face == Face.UP && tile2.index == 6) ||
                        (tile2.face == Face.FRONT && tile2.index == 0)
                8 -> (tile2.face == Face.FRONT && tile2.index == 6) ||
                        (tile2.face == Face.DOWN && tile2.index == 0)
                6 -> (tile2.face == Face.BACK && tile2.index == 8) ||
                        (tile2.face == Face.DOWN && tile2.index == 6)
                else -> false
            }
        }
        Face.RIGHT -> {
            when(tile1.index) {
                //Edges
                1 -> (tile2.face == Face.UP && tile2.index == 5)
                5 -> (tile2.face == Face.BACK && tile2.index == 3)
                7 -> (tile2.face == Face.DOWN && tile2.index == 5)
                3 -> (tile2.face == Face.FRONT && tile2.index == 5)
                //Corners
                0 -> (tile2.face == Face.FRONT && tile2.index == 2) ||
                        (tile2.face == Face.UP && tile2.index == 8)
                2 -> (tile2.face == Face.BACK && tile2.index == 0) ||
                        (tile2.face == Face.UP && tile2.index == 2)
                8 -> (tile2.face == Face.RIGHT && tile2.index == 6) ||
                        (tile2.face == Face.DOWN && tile2.index == 8)
                6 -> (tile2.face == Face.FRONT && tile2.index == 8) ||
                        (tile2.face == Face.DOWN && tile2.index == 2)
                else -> false
            }
        }
        Face.UP -> {
            when(tile1.index) {
                //Edges
                1 -> (tile2.face == Face.BACK && tile2.index == 1)
                5 -> (tile2.face == Face.RIGHT && tile2.index == 1)
                7 -> (tile2.face == Face.FRONT && tile2.index == 1)
                3 -> (tile2.face == Face.LEFT && tile2.index == 1)
                //Corners
                0 -> (tile2.face == Face.LEFT && tile2.index == 0) ||
                        (tile2.face == Face.BACK && tile2.index == 2)
                2 -> (tile2.face == Face.RIGHT && tile2.index == 2) ||
                        (tile2.face == Face.BACK && tile2.index == 0)
                8 -> (tile2.face == Face.FRONT && tile2.index == 2) ||
                        (tile2.face == Face.RIGHT && tile2.index == 0)
                6 -> (tile2.face == Face.FRONT && tile2.index == 0) ||
                        (tile2.face == Face.LEFT && tile2.index == 2)
                else -> false
            }
        }
        Face.DOWN -> {
            when(tile1.index) {
                //Edges
                1 -> (tile2.face == Face.FRONT && tile2.index == 7)
                5 -> (tile2.face == Face.RIGHT && tile2.index == 7)
                7 -> (tile2.face == Face.BACK && tile2.index == 7)
                3 -> (tile2.face == Face.LEFT && tile2.index == 7)
                //Corners
                0 -> (tile2.face == Face.FRONT && tile2.index == 6) ||
                        (tile2.face == Face.LEFT && tile2.index == 8)
                2 -> (tile2.face == Face.FRONT && tile2.index == 8) ||
                        (tile2.face == Face.RIGHT && tile2.index == 6)
                8 -> (tile2.face == Face.RIGHT && tile2.index == 8) ||
                        (tile2.face == Face.BACK && tile2.index == 6)
                6 -> (tile2.face == Face.LEFT && tile2.index == 6) ||
                        (tile2.face == Face.BACK && tile2.index == 8)
                else -> false
            }
        }
    }
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

//Functions for frequently thrown exceptions
private fun failNotAdjacent(): IllegalArgumentException {
    return IllegalArgumentException("invalid args: provided tiles are not adjacent")
}
/*
private fun failNotEdge(): IllegalArgumentException {
    return IllegalArgumentException("invalid args: provided tiles are not on an edge cubie")
}
private fun failNotCorner(): IllegalArgumentException {
    return IllegalArgumentException("invalid args: provided tiles are not on a corner cubie")
}*/