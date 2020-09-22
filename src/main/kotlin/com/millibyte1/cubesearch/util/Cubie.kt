package com.millibyte1.cubesearch.util

import com.millibyte1.cubesearch.cube.Cube
import com.millibyte1.cubesearch.cube.Twist

/**
 * A simple data class representing a snapshot of a tile (a sticker on a cubie)
 * @property face the face of the cube this tile is currently on
 * @property color the color of the tile
 */
data class Tile(val face: Twist.Face, val color: Int)

/**
 * A simple algebraic data type to represent the variant cubies (edge, corner, and center cubies)
 * Important for CubeUtils functions (validating a cube, heuristic for search)
 */
sealed class Cubie {
    data class CenterCubie(val tile1: Tile) : Cubie()
    data class EdgeCubie(val tile1: Tile, val tile2: Tile) : Cubie()
    data class CornerCubie(val tile1: Tile, val tile2: Tile, val tile3: Tile) : Cubie()
}
