package com.millibyte1.cubesearch.util

import com.millibyte1.cubesearch.cube.Cube
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
    fun getPosition(): TilePosition {
        return TilePosition(face, index)
    }
}
data class TilePosition(val face: Face, val index: Int) : Comparable<TilePosition> {
    /** A comparison based on the natural ordering of Twist.Face */
    override fun compareTo(other: TilePosition): Int {
        return when {
            face < other.face -> -1
            face > other.face -> 1
            else -> 0
        }
    }
}

/**
 * A simple algebraic sum type to represent the variant cubies (edge, corner, and center cubies)
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
/** Returns whether this tile's position is on a center cubie */
fun isOnCenterCubie(tile: Tile): Boolean {
    return (tile.index == 4)
}
/** Returns whether this tile's position is on an edge cubie */
fun isOnEdgeCubie(tile: Tile): Boolean {
    return when(tile.index) {
        1, 3, 5, 7 -> true
        else -> false
    }
}
/** Returns whether this tile's position is on a corner cubie */
fun isOnCornerCubie(tile: Tile): Boolean {
    return when(tile.index) {
        0, 2, 6, 8 -> true
        else -> false
    }
}
/**
 * Determines whether the tiles are on the same cubie
 * @param tile1 the first tile
 * @param tile2 the second tile
 * @return whether the tiles are positioned on the same cubie
 */
internal fun isOnSameCubie(tile1: Tile, tile2: Tile): Boolean {
    return when(tile1.index) {
        1, 3, 5, 7 -> (tile2.getPosition() == getOtherTilePositionOnEdgeCubie(tile1.getPosition()))
        0, 2, 6, 8 -> (tile2.getPosition() == getOtherTilePositionsOnCornerCubie(tile1.getPosition()).first) ||
                      (tile2.getPosition() == getOtherTilePositionsOnCornerCubie(tile1.getPosition()).second)
        else -> false
    }
}

internal fun tileExists(cube: Cube, tile: Tile): Boolean {
    return (cube.data[tile.face.ordinal][tile.index] == tile.color)
}
/**
 * Gets the tile position of the other tile on this edge cubie
 * @param tile the tile position in question
 * @return the position of the other tile on this edge
 * @throws IllegalArgumentException if this tile position is not on an edge
 */
@Throws(IllegalArgumentException::class)
fun getOtherTilePositionOnEdgeCubie(tile: TilePosition): TilePosition {
    return when(tile.face) {
        Face.FRONT -> {
            when(tile.index) {
                1 -> TilePosition(Face.UP, 7)
                5 -> TilePosition(Face.RIGHT, 3)
                7 -> TilePosition(Face.DOWN, 1)
                3 -> TilePosition(Face.LEFT, 5)
                else -> throw failNotEdge()
            }
        }
        Face.BACK -> {
            when(tile.index) {
                1 -> TilePosition(Face.UP, 1)
                5 -> TilePosition(Face.LEFT, 3)
                7 -> TilePosition(Face.DOWN, 7)
                3 -> TilePosition(Face.RIGHT, 5)
                else -> throw failNotEdge()
            }
        }
        Face.LEFT -> {
            when(tile.index) {
                1 -> TilePosition(Face.UP, 3)
                5 -> TilePosition(Face.FRONT, 3)
                7 -> TilePosition(Face.DOWN, 3)
                3 -> TilePosition(Face.BACK, 5)
                else -> throw failNotEdge()
            }
        }
        Face.RIGHT -> {
            when(tile.index) {
                1 -> TilePosition(Face.UP, 5)
                5 -> TilePosition(Face.BACK, 3)
                7 -> TilePosition(Face.DOWN, 5)
                3 -> TilePosition(Face.FRONT, 5)
                else -> throw failNotEdge()
            }
        }
        Face.UP -> {
            when(tile.index) {
                1 -> TilePosition(Face.BACK, 1)
                5 -> TilePosition(Face.RIGHT, 1)
                7 -> TilePosition(Face.FRONT, 1)
                3 -> TilePosition(Face.LEFT, 1)
                else -> throw failNotEdge()
            }
        }
        Face.DOWN -> {
            when(tile.index) {
                1 -> TilePosition(Face.FRONT, 7)
                5 -> TilePosition(Face.RIGHT, 7)
                7 -> TilePosition(Face.BACK, 7)
                3 -> TilePosition(Face.LEFT, 7)
                else -> throw failNotEdge()
            }
        }
    }
}
/**
 * Gets the tile positions of the other tiles on this corner cubie
 * @param tile the tile position in question
 * @return the position of the other tiles on this corner
 * @throws IllegalArgumentException if this tile position is not on a corner
 */
@Throws(IllegalArgumentException::class)
fun getOtherTilePositionsOnCornerCubie(tile: TilePosition): Pair<TilePosition, TilePosition> {
    //TODO make sure the tile positions are in the appropriate order
    return when(tile.face) {
        Face.FRONT -> {
            when(tile.index) {
                0 -> Pair(TilePosition(Face.LEFT, 2), TilePosition(Face.UP, 6))
                2 -> Pair(TilePosition(Face.RIGHT, 0), TilePosition(Face.UP, 8))
                8 -> Pair(TilePosition(Face.RIGHT, 6), TilePosition(Face.DOWN, 2))
                6 -> Pair(TilePosition(Face.LEFT, 8), TilePosition(Face.DOWN, 0))
                else -> throw failNotCorner()
            }
        }
        Face.BACK -> {
            when(tile.index) {
                0 -> Pair(TilePosition(Face.RIGHT, 2), TilePosition(Face.UP, 2))
                2 -> Pair(TilePosition(Face.LEFT, 0), TilePosition(Face.UP, 0))
                8 -> Pair(TilePosition(Face.LEFT, 6), TilePosition(Face.DOWN, 6))
                6 -> Pair(TilePosition(Face.RIGHT, 8), TilePosition(Face.DOWN, 8))
                else -> throw failNotCorner()
            }
        }
        Face.LEFT -> {
            when(tile.index) {
                0 -> Pair(TilePosition(Face.BACK, 2), TilePosition(Face.UP, 0))
                2 -> Pair(TilePosition(Face.FRONT, 0), TilePosition(Face.UP, 6))
                8 -> Pair(TilePosition(Face.FRONT, 6), TilePosition(Face.DOWN, 0))
                6 -> Pair(TilePosition(Face.BACK, 8), TilePosition(Face.DOWN, 6))
                else -> throw failNotCorner()
            }
        }
        Face.RIGHT -> {
            when(tile.index) {
                0 -> Pair(TilePosition(Face.FRONT, 2), TilePosition(Face.UP, 8))
                2 -> Pair(TilePosition(Face.BACK, 0), TilePosition(Face.UP, 2))
                8 -> Pair(TilePosition(Face.RIGHT, 6), TilePosition(Face.DOWN, 8))
                6 -> Pair(TilePosition(Face.FRONT, 8), TilePosition(Face.DOWN, 2))
                else -> throw failNotCorner()
            }
        }
        Face.UP -> {
            when(tile.index) {
                0 -> Pair(TilePosition(Face.BACK, 2), TilePosition(Face.LEFT, 0))
                2 -> Pair(TilePosition(Face.BACK, 0), TilePosition(Face.RIGHT, 2))
                8 -> Pair(TilePosition(Face.FRONT, 2), TilePosition(Face.RIGHT, 0))
                6 -> Pair(TilePosition(Face.FRONT, 0), TilePosition(Face.LEFT, 2))
                else -> throw failNotCorner()
            }
        }
        Face.DOWN -> {
            when(tile.index) {
                0 -> Pair(TilePosition(Face.FRONT, 6), TilePosition(Face.LEFT, 8))
                2 -> Pair(TilePosition(Face.FRONT, 8), TilePosition(Face.RIGHT, 6))
                8 -> Pair(TilePosition(Face.BACK, 6), TilePosition(Face.RIGHT, 8))
                6 -> Pair(TilePosition(Face.BACK, 8), TilePosition(Face.LEFT, 6))
                else -> throw failNotCorner()
            }
        }
    }
}

/**
 * Takes a cube and an edge tile and returns the other tile on the edge
 * @param cube the cube in question
 * @param tile the tile in question
 * @return the appropriate tile
 * @throws IllegalArgumentException if the tile isn't a real edge tile on this cube
 */
@Throws(IllegalArgumentException::class)
fun getOtherTileOnEdgeCubie(cube: Cube, tile: Tile): Tile {
    if(!tileExists(cube, tile)) throw IllegalArgumentException("invalid args: tile does not exist on provided cube")
    val pos = getOtherTilePositionOnEdgeCubie(tile.getPosition())
    return Tile(pos.face, pos.index, cube.data[pos.face.ordinal][pos.index])
}
/**
 * Takes a cube and a corner tile and returns the other tiles on the corner
 * @param cube the cube in question
 * @param tile the tile in question
 * @return a pair of the appropriate tiles
 * @throws IllegalArgumentException if the tile isn't a real corner tile on this cube
 */
@Throws(IllegalArgumentException::class)
fun getOtherTilesOnCornerCubie(cube: Cube, tile: Tile): Pair<Tile, Tile> {
    if(!tileExists(cube, tile)) throw IllegalArgumentException("invalid args: tile does not exist on provided cube")
    val pos = getOtherTilePositionsOnCornerCubie(tile.getPosition())
    return Pair(Tile(pos.first.face, pos.first.index, cube.data[pos.first.face.ordinal][pos.first.index]),
                Tile(pos.second.face, pos.second.index, cube.data[pos.second.face.ordinal][pos.second.index]))
}
/**
 * Takes a cube and a tile and returns the cubie associated with the tile
 * Effectively a factory function for Cubies.
 * @param tile the tile in question
 * @return the appropriate cubie
 * @throws IllegalArgumentException if the given tile does not exist on this cube
 */
@Throws(IllegalArgumentException::class)
fun getCubie(cube: Cube, tile: Tile): Cubie {
    if(isOnCenterCubie(tile) && tile.color == tile.face.ordinal) return getCubie(tile)
    if(isOnEdgeCubie(tile)) return getCubie(tile, getOtherTileOnEdgeCubie(cube, tile))
    if(isOnCornerCubie(tile)) {
        val tiles = getOtherTilesOnCornerCubie(cube, tile)
        return getCubie(tile, tiles.first, tiles.second)
    }
    throw failNotAdjacent()
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

private fun failNotEdge(): IllegalArgumentException {
    return IllegalArgumentException("invalid args: provided tile is not on an edge cubie")
}
private fun failNotCorner(): IllegalArgumentException {
    return IllegalArgumentException("invalid args: provided tile is not on a corner cubie")
}