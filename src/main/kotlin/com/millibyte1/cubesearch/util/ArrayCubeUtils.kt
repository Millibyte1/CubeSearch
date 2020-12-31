package com.millibyte1.cubesearch.util

import com.millibyte1.cubesearch.cube.ArrayCube
import com.millibyte1.cubesearch.cube.ArrayCubeFactory
import com.millibyte1.cubesearch.cube.Twist.Face

/* ============================================= TYPE ABSTRACTIONS ================================================== */

typealias CenterCubie = Cubie.CenterCubie
typealias EdgeCubie = Cubie.EdgeCubie
typealias CornerCubie = Cubie.CornerCubie

/**
 * A simple algebraic sum type to represent the variant cubies (edge, corner, and center cubies).
 * Important for validation and search algorithms.
 */
sealed class Cubie {
    data class CenterCubie(val tile1: Tile) : Cubie() {
        override fun equals(other: Any?): Boolean {
            if(this === other) return true
            if(other !is CenterCubie) return false
            return other.tile1 == this.tile1
        }
        override fun colorEquals(other: Cubie): Boolean {
            if(this === other) return true
            if(other !is CenterCubie) return false
            return other.tile1.color == this.tile1.color
        }
        override fun positionEquals(other: Cubie): Boolean {
            if(this === other) return true
            if(other !is CenterCubie) return false
            return tile1.pos == other.tile1.pos
        }

        override fun hashCode(): Int {
            return tile1.hashCode()
        }
    }
    data class EdgeCubie(val tile1: Tile, val tile2: Tile) : Cubie() {
        override fun equals(other: Any?): Boolean {
            if(this === other) return true
            if(other !is EdgeCubie) return false
            val sortedThis = arrayOf(tile1, tile2).sorted()
            val sortedOther = arrayOf(other.tile1, other.tile2).sorted()
            return sortedThis == sortedOther
        }
        override fun colorEquals(other: Cubie): Boolean {
            if(this === other) return true
            if(other !is EdgeCubie) return false
            return ((tile1.color == other.tile1.color && tile2.color == other.tile2.color) ||
                    (tile1.color == other.tile2.color && tile2.color == other.tile1.color))
        }
        override fun positionEquals(other: Cubie): Boolean {
            if(this === other) return true
            if(other !is EdgeCubie) return false
            return tile1.pos == other.tile1.pos && tile2.pos == other.tile2.pos
        }

        override fun hashCode(): Int {
            var result = tile1.hashCode()
            result = 31 * result + tile2.hashCode()
            return result
        }
    }
    data class CornerCubie(val tile1: Tile, val tile2: Tile, val tile3: Tile) : Cubie() {
        override fun equals(other: Any?): Boolean {
            if(this === other) return true
            if(other !is CornerCubie) return false
            val sortedThis = arrayOf(tile1, tile2, tile3).sorted()
            val sortedOther = arrayOf(other.tile1, other.tile2, tile3).sorted()
            return sortedThis == sortedOther
        }
        override fun colorEquals(other: Cubie): Boolean {
            if(this === other) return true
            if(other !is CornerCubie) return false
            return ((tile1.color == other.tile1.color && tile2.color == other.tile2.color && tile3.color == other.tile3.color) ||
                    (tile1.color == other.tile1.color && tile2.color == other.tile3.color && tile3.color == other.tile2.color) ||
                    (tile1.color == other.tile2.color && tile2.color == other.tile1.color && tile3.color == other.tile3.color) ||
                    (tile1.color == other.tile2.color && tile2.color == other.tile3.color && tile3.color == other.tile1.color) ||
                    (tile1.color == other.tile3.color && tile2.color == other.tile1.color && tile3.color == other.tile2.color) ||
                    (tile1.color == other.tile3.color && tile2.color == other.tile2.color && tile3.color == other.tile1.color))
        }
        override fun positionEquals(other: Cubie): Boolean {
            if(this === other) return true
            if(other !is CornerCubie) return false
            return tile1.pos == other.tile1.pos && tile2.pos == other.tile2.pos && tile3.pos == other.tile3.pos
        }

        override fun hashCode(): Int {
            var result = tile1.hashCode()
            result = 31 * result + tile2.hashCode()
            result = 31 * result + tile3.hashCode()
            return result
        }
    }
    /** Returns whether these cubies have the same colors */
    abstract fun colorEquals(other: Cubie): Boolean
    /** Returns whether these cubies are in the same position */
    abstract fun positionEquals(other: Cubie): Boolean

    companion object {
        /**
         * Factory function for all variants of cubie
         * @param tiles the tiles that are on the desired cubie
         * @return the desired cube
         * @throws IllegalArgumentException if an invalid number of tiles are provided
         */
        @Throws(IllegalArgumentException::class)
        fun makeCubie(vararg tiles: Tile): Cubie {
            tiles.sort()
            return when(tiles.size) {
                1 -> CenterCubie(tiles[0])
                2 -> EdgeCubie(tiles[0], tiles[1])
                3 -> CornerCubie(tiles[0], tiles[1], tiles[2])
                else -> throw failInvalidNumberOfTiles()
            }
        }
        /**
         * Factory function for all variants of cubie
         * @param tiles the tiles that are on the desired cubie
         * @return the desired cubie
         * @throws IllegalArgumentException if an invalid number of tiles are provided
         */
        @Throws(IllegalArgumentException::class)
        fun makeCubie(cube: ArrayCube, vararg tiles: TilePosition): Cubie {
            tiles.sort()
            return when(tiles.size) {
                1 -> CenterCubie(Tile(cube, tiles[0]))
                2 -> EdgeCubie(Tile(cube, tiles[0]), Tile(cube, tiles[1]))
                3 -> CornerCubie(Tile(cube, tiles[0]), Tile(cube, tiles[1]), Tile(cube, tiles[2]))
                else -> throw failInvalidNumberOfTiles()
            }
        }
    }
}

/** A simple data class representing the position of a tile on a cube */
data class TilePosition(val face: Face, val index: Int) : Comparable<TilePosition> {
    /** A comparison based on the natural ordering of Twist.Face and then the index */
    override fun compareTo(other: TilePosition): Int {
        return when {
            face < other.face -> -1
            face > other.face -> 1
            index < other.index -> -1
            index > other.index -> 1
            else -> 0
        }
    }
}
/** A simple data class representing a single tile on a single cubie on a cube */
data class Tile(val pos: TilePosition, val color: Int) : Comparable<Tile> {
    constructor(cube: ArrayCube, pos: TilePosition) : this(pos, cube.data[pos.face.ordinal][pos.index])
    override fun compareTo(other: Tile): Int {
        return when {
            pos < other.pos -> -1
            pos > other.pos -> 1
            color < other.color -> -1
            color > other.color -> 1
            else -> 0
        }
    }
}

object ArrayCubeUtils {
/* ========================================= SOLVED CUBIE ACCESS FUNCTIONS ========================================== */

    private val solved = ArrayCubeFactory().getSolvedCube()

    /** Returns whether the given cube is solved */
    fun isSolved(cube: ArrayCube): Boolean {
        return cube == solved
    }

    /** Gets the list of cubies on a solved cube */
    fun getSolvedCubies(): List<Cubie> {
        return getCubies(solved)
    }

    /** Gets the list of corners on a solved cube */
    fun getSolvedCorners(): List<CornerCubie> {
        return getCorners(solved)
    }

    /** Gets the list of edges on a solved cube */
    fun getSolvedEdges(): List<EdgeCubie> {
        return getEdges(solved)
    }

    /** Gets the list of centers on a solved cube */
    fun getSolvedCenters(): List<CenterCubie> {
        return getCenters(solved)
    }

    /** Gets the cubie at the specified position on a solved cube */
    fun getSolvedCubieAt(tile: TilePosition): Cubie {
        return getCubieAt(solved, tile)
    }

    /** Gets the cubie that lies on all of the given faces on a solved cube */
    fun getSolvedCubieOnFaces(vararg faces: Face): Cubie {
        return getCubieOnFaces(solved, *faces)
    }

    /**
     * Gets the correctly positioned and oriented version of this cubie
     * @param cubie the cubie in question
     * @return the cubie on the solved cube with this cubie's colors
     * @throws IllegalArgumentException if this cubie does not exist on a solvable cube
     */
    @Throws(IllegalArgumentException::class)
    fun getSolvedCubie(cubie: Cubie): Cubie {
        return getCubieOnCube(solved, cubie)
    }

/* ============================================ CUBIE ACCESS FUNCTIONS ============================================== */

    /** Gets all cubies on this cube */
    fun getCubies(cube: ArrayCube): List<Cubie> {
        return getCorners(cube) + getEdges(cube) + getCenters(cube)
    }

    /** Gets all corner cubies on this cube */
    fun getCorners(cube: ArrayCube): List<CornerCubie> {
        val corners = ArrayList<CornerCubie>()
        corners.add(getCubieOnFaces(cube, Face.UP, Face.FRONT, Face.LEFT) as CornerCubie)
        corners.add(getCubieOnFaces(cube, Face.UP, Face.FRONT, Face.RIGHT) as CornerCubie)
        corners.add(getCubieOnFaces(cube, Face.UP, Face.BACK, Face.LEFT) as CornerCubie)
        corners.add(getCubieOnFaces(cube, Face.UP, Face.BACK, Face.RIGHT) as CornerCubie)
        corners.add(getCubieOnFaces(cube, Face.DOWN, Face.FRONT, Face.LEFT) as CornerCubie)
        corners.add(getCubieOnFaces(cube, Face.DOWN, Face.FRONT, Face.RIGHT) as CornerCubie)
        corners.add(getCubieOnFaces(cube, Face.DOWN, Face.BACK, Face.LEFT) as CornerCubie)
        corners.add(getCubieOnFaces(cube, Face.DOWN, Face.BACK, Face.RIGHT) as CornerCubie)
        return corners
    }

    /** Gets all edge cubies on this cube */
    fun getEdges(cube: ArrayCube): List<EdgeCubie> {
        val edges = ArrayList<EdgeCubie>()
        edges.add(getCubieOnFaces(cube, Face.UP, Face.FRONT) as EdgeCubie)
        edges.add(getCubieOnFaces(cube, Face.UP, Face.BACK) as EdgeCubie)
        edges.add(getCubieOnFaces(cube, Face.UP, Face.LEFT) as EdgeCubie)
        edges.add(getCubieOnFaces(cube, Face.UP, Face.RIGHT) as EdgeCubie)
        edges.add(getCubieOnFaces(cube, Face.DOWN, Face.FRONT) as EdgeCubie)
        edges.add(getCubieOnFaces(cube, Face.DOWN, Face.BACK) as EdgeCubie)
        edges.add(getCubieOnFaces(cube, Face.DOWN, Face.LEFT) as EdgeCubie)
        edges.add(getCubieOnFaces(cube, Face.DOWN, Face.RIGHT) as EdgeCubie)
        edges.add(getCubieOnFaces(cube, Face.FRONT, Face.LEFT) as EdgeCubie)
        edges.add(getCubieOnFaces(cube, Face.FRONT, Face.RIGHT) as EdgeCubie)
        edges.add(getCubieOnFaces(cube, Face.BACK, Face.LEFT) as EdgeCubie)
        edges.add(getCubieOnFaces(cube, Face.BACK, Face.RIGHT) as EdgeCubie)
        return edges
    }

    /** Gets all center cubies on this cube */
    fun getCenters(cube: ArrayCube): List<CenterCubie> {
        val centers = ArrayList<CenterCubie>()
        centers.add(getCubieOnFaces(cube, Face.FRONT) as CenterCubie)
        centers.add(getCubieOnFaces(cube, Face.BACK) as CenterCubie)
        centers.add(getCubieOnFaces(cube, Face.LEFT) as CenterCubie)
        centers.add(getCubieOnFaces(cube, Face.RIGHT) as CenterCubie)
        centers.add(getCubieOnFaces(cube, Face.UP) as CenterCubie)
        centers.add(getCubieOnFaces(cube, Face.DOWN) as CenterCubie)
        return centers
    }

    /**
     * Gets the cubie that lies on all of the given faces on this cube
     * @param cube the cube in question
     * @param faces the faces the desired cubie is on
     * @return the appropriate cubie
     * @throws IllegalArgumentException if an invalid set of faces is provided
     */
    @Throws(IllegalArgumentException::class)
    fun getCubieOnFaces(cube: ArrayCube, vararg faces: Face): Cubie {
        if (faces.isEmpty() || faces.size > 3) throw failInvalidNumberOfFaces()
        return getCubieAt(cube, getTilePositionOnFaces(*faces))
    }

    /**
     * Gets the cubie at the specified position on this cube
     * @param cube the cube in question
     * @param tile the position of one of the tiles on the desired cubie
     * @return the appropriate cube
     * @throws IllegalArgumentException if an invalid tile position is provided
     */
    @Throws(IllegalArgumentException::class)
    fun getCubieAt(cube: ArrayCube, tile: TilePosition): Cubie {
        return when {
            isOnCenterCubie(tile) -> Cubie.makeCubie(Tile(cube, tile))
            isOnEdgeCubie(tile) -> Cubie.makeCubie(Tile(cube, tile), Tile(cube, getOtherTilePositionOnEdgeCubie(tile)))
            isOnCornerCubie(tile) -> Cubie.makeCubie(Tile(cube, tile),
                    Tile(cube, getOtherTilePositionsOnCornerCubie(tile).first),
                    Tile(cube, getOtherTilePositionsOnCornerCubie(tile).second))
            else -> throw failInvalidTilePosition()
        }
    }

    /** Gets the cubie on the given cube with the same colors as the provided cubie
     * @param cube the cube we want to search on
     * @param cubie the cubie that we want to find on [cube]
     * @return the cubie on [cube] with the same colors as [cubie]
     * @throws IllegalArgumentException if the provided cubie does not exist on the given cube
     */
    @Throws(IllegalArgumentException::class)
    fun getCubieOnCube(cube: ArrayCube, cubie: Cubie): Cubie {
        return getCubieWithColors(cube, *getColorsOnCubie(cubie))
    }

    /**
     * Gets the cubie with the specified tile colors
     * @param cube the cube in question
     * @param colors the colors we want to find on a cubie
     * @return the cubie that matches all of the given colors
     * @throws IllegalArgumentException if no cubie on this cube has all of the given colors
     */
    @Throws(IllegalArgumentException::class)
    fun getCubieWithColors(cube: ArrayCube, vararg colors: Int): Cubie {
        for (cubie in getCubies(cube)) {
            val cubieColors = getColorsOnCubie(cubie)
            if (colors.all { color1 -> cubieColors.any { color2 -> color1 == color2 } } &&
                    cubieColors.all { color1 -> colors.any { color2 -> color1 == color2 } }) return cubie
        }
        throw IllegalArgumentException("Error: No cubie on the given cube has the listed colors")
    }

    /**
     * Gets the colors of the tiles on this cubie (works for both valid and invalid cubies)
     * @param cubie the cubie in question
     * @return the colors on this cube
     */
    fun getColorsOnCubie(cubie: Cubie): IntArray {
        return when (cubie) {
            is CenterCubie -> intArrayOf(cubie.tile1.color)
            is EdgeCubie -> intArrayOf(cubie.tile1.color, cubie.tile2.color)
            is CornerCubie -> intArrayOf(cubie.tile1.color, cubie.tile2.color, cubie.tile3.color)
        }
    }

/* =============================================== HELPER FUNCTIONS ================================================= */

    /** Determines whether this cubie lies on all the provided faces */
    fun isOnFaces(cubie: Cubie, vararg faces: Face): Boolean {
        return faces.all { face -> isOnFace(cubie, face) }
    }

    /** Determines whether this cubie lies on the provided face */
    fun isOnFace(cubie: Cubie, face: Face): Boolean {
        return when (cubie) {
            is CenterCubie ->
                (cubie.tile1.pos.face == face)
            is EdgeCubie ->
                (cubie.tile1.pos.face == face) ||
                        (cubie.tile2.pos.face == face)
            is CornerCubie ->
                (cubie.tile1.pos.face == face) ||
                        (cubie.tile2.pos.face == face) ||
                        (cubie.tile3.pos.face == face)
        }
    }

    /** Determines whether this cubie has a tile with the given color */
    fun containsColor(cubie: Cubie, color: Int): Boolean {
        return when (cubie) {
            is CenterCubie -> (cubie.tile1.color == color)
            is EdgeCubie -> (cubie.tile1.color == color || cubie.tile2.color == color)
            is CornerCubie -> (cubie.tile1.color == color || cubie.tile2.color == color || cubie.tile3.color == color)
        }
    }

    /**
     * Gets the position of one of the tiles on the cubie that lies on these faces
     * @param faces the faces the desired cubie is on
     * @return an appropriate tile position
     * @throws IllegalArgumentException if an invalid set of faces is provided
     */
    @Throws(IllegalArgumentException::class)
    fun getTilePositionOnFaces(vararg faces: Face): TilePosition {
        return when (faces.size) {
            1 -> TilePosition(faces[0], 4)
            2 -> {
                when {
                    faces.contains(Face.UP) && faces.contains(Face.FRONT) -> TilePosition(Face.UP, 7)
                    faces.contains(Face.UP) && faces.contains(Face.BACK) -> TilePosition(Face.UP, 1)
                    faces.contains(Face.UP) && faces.contains(Face.LEFT) -> TilePosition(Face.UP, 3)
                    faces.contains(Face.UP) && faces.contains(Face.RIGHT) -> TilePosition(Face.UP, 5)
                    faces.contains(Face.DOWN) && faces.contains(Face.FRONT) -> TilePosition(Face.DOWN, 1)
                    faces.contains(Face.DOWN) && faces.contains(Face.BACK) -> TilePosition(Face.DOWN, 7)
                    faces.contains(Face.DOWN) && faces.contains(Face.LEFT) -> TilePosition(Face.DOWN, 3)
                    faces.contains(Face.DOWN) && faces.contains(Face.RIGHT) -> TilePosition(Face.DOWN, 5)
                    faces.contains(Face.FRONT) && faces.contains(Face.LEFT) -> TilePosition(Face.FRONT, 3)
                    faces.contains(Face.FRONT) && faces.contains(Face.RIGHT) -> TilePosition(Face.FRONT, 5)
                    faces.contains(Face.BACK) && faces.contains(Face.LEFT) -> TilePosition(Face.BACK, 5)
                    faces.contains(Face.BACK) && faces.contains(Face.RIGHT) -> TilePosition(Face.BACK, 3)
                    else -> throw failFacesNotAdjacent()
                }
            }
            3 -> {
                when {
                    faces.contains(Face.UP) && faces.contains(Face.FRONT) && faces.contains(Face.LEFT) ->
                        TilePosition(Face.UP, 6)
                    faces.contains(Face.UP) && faces.contains(Face.FRONT) && faces.contains(Face.RIGHT) ->
                        TilePosition(Face.UP, 8)
                    faces.contains(Face.UP) && faces.contains(Face.BACK) && faces.contains(Face.LEFT) ->
                        TilePosition(Face.UP, 0)
                    faces.contains(Face.UP) && faces.contains(Face.BACK) && faces.contains(Face.RIGHT) ->
                        TilePosition(Face.UP, 2)
                    faces.contains(Face.DOWN) && faces.contains(Face.FRONT) && faces.contains(Face.LEFT) ->
                        TilePosition(Face.DOWN, 0)
                    faces.contains(Face.DOWN) && faces.contains(Face.FRONT) && faces.contains(Face.RIGHT) ->
                        TilePosition(Face.DOWN, 2)
                    faces.contains(Face.DOWN) && faces.contains(Face.BACK) && faces.contains(Face.LEFT) ->
                        TilePosition(Face.DOWN, 6)
                    faces.contains(Face.DOWN) && faces.contains(Face.BACK) && faces.contains(Face.RIGHT) ->
                        TilePosition(Face.DOWN, 8)
                    else -> throw failFacesNotAdjacent()
                }
            }
            else -> throw failInvalidNumberOfFaces()
        }
    }

    /**
     * Gets the tile position of the other tile on this edge cubie
     * @param tile the tile position in question
     * @return the position of the other tile on this edge
     * @throws IllegalArgumentException if this tile position is not on an edge
     */
    @Throws(IllegalArgumentException::class)
    fun getOtherTilePositionOnEdgeCubie(tile: TilePosition): TilePosition {
        return when (tile.face) {
            Face.FRONT -> {
                when (tile.index) {
                    1 -> TilePosition(Face.UP, 7)
                    5 -> TilePosition(Face.RIGHT, 3)
                    7 -> TilePosition(Face.DOWN, 1)
                    3 -> TilePosition(Face.LEFT, 5)
                    else -> throw failNotEdge()
                }
            }
            Face.BACK -> {
                when (tile.index) {
                    1 -> TilePosition(Face.UP, 1)
                    5 -> TilePosition(Face.LEFT, 3)
                    7 -> TilePosition(Face.DOWN, 7)
                    3 -> TilePosition(Face.RIGHT, 5)
                    else -> throw failNotEdge()
                }
            }
            Face.LEFT -> {
                when (tile.index) {
                    1 -> TilePosition(Face.UP, 3)
                    5 -> TilePosition(Face.FRONT, 3)
                    7 -> TilePosition(Face.DOWN, 3)
                    3 -> TilePosition(Face.BACK, 5)
                    else -> throw failNotEdge()
                }
            }
            Face.RIGHT -> {
                when (tile.index) {
                    1 -> TilePosition(Face.UP, 5)
                    5 -> TilePosition(Face.BACK, 3)
                    7 -> TilePosition(Face.DOWN, 5)
                    3 -> TilePosition(Face.FRONT, 5)
                    else -> throw failNotEdge()
                }
            }
            Face.UP -> {
                when (tile.index) {
                    1 -> TilePosition(Face.BACK, 1)
                    5 -> TilePosition(Face.RIGHT, 1)
                    7 -> TilePosition(Face.FRONT, 1)
                    3 -> TilePosition(Face.LEFT, 1)
                    else -> throw failNotEdge()
                }
            }
            Face.DOWN -> {
                when (tile.index) {
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
        return when (tile.face) {
            Face.FRONT -> {
                when (tile.index) {
                    0 -> Pair(TilePosition(Face.LEFT, 2), TilePosition(Face.UP, 6))
                    2 -> Pair(TilePosition(Face.RIGHT, 0), TilePosition(Face.UP, 8))
                    8 -> Pair(TilePosition(Face.RIGHT, 6), TilePosition(Face.DOWN, 2))
                    6 -> Pair(TilePosition(Face.LEFT, 8), TilePosition(Face.DOWN, 0))
                    else -> throw failNotCorner()
                }
            }
            Face.BACK -> {
                when (tile.index) {
                    0 -> Pair(TilePosition(Face.RIGHT, 2), TilePosition(Face.UP, 2))
                    2 -> Pair(TilePosition(Face.LEFT, 0), TilePosition(Face.UP, 0))
                    8 -> Pair(TilePosition(Face.LEFT, 6), TilePosition(Face.DOWN, 6))
                    6 -> Pair(TilePosition(Face.RIGHT, 8), TilePosition(Face.DOWN, 8))
                    else -> throw failNotCorner()
                }
            }
            Face.LEFT -> {
                when (tile.index) {
                    0 -> Pair(TilePosition(Face.BACK, 2), TilePosition(Face.UP, 0))
                    2 -> Pair(TilePosition(Face.FRONT, 0), TilePosition(Face.UP, 6))
                    8 -> Pair(TilePosition(Face.FRONT, 6), TilePosition(Face.DOWN, 0))
                    6 -> Pair(TilePosition(Face.BACK, 8), TilePosition(Face.DOWN, 6))
                    else -> throw failNotCorner()
                }
            }
            Face.RIGHT -> {
                when (tile.index) {
                    0 -> Pair(TilePosition(Face.FRONT, 2), TilePosition(Face.UP, 8))
                    2 -> Pair(TilePosition(Face.BACK, 0), TilePosition(Face.UP, 2))
                    8 -> Pair(TilePosition(Face.BACK, 6), TilePosition(Face.DOWN, 8))
                    6 -> Pair(TilePosition(Face.FRONT, 8), TilePosition(Face.DOWN, 2))
                    else -> throw failNotCorner()
                }
            }
            Face.UP -> {
                when (tile.index) {
                    0 -> Pair(TilePosition(Face.BACK, 2), TilePosition(Face.LEFT, 0))
                    2 -> Pair(TilePosition(Face.BACK, 0), TilePosition(Face.RIGHT, 2))
                    8 -> Pair(TilePosition(Face.FRONT, 2), TilePosition(Face.RIGHT, 0))
                    6 -> Pair(TilePosition(Face.FRONT, 0), TilePosition(Face.LEFT, 2))
                    else -> throw failNotCorner()
                }
            }
            Face.DOWN -> {
                when (tile.index) {
                    0 -> Pair(TilePosition(Face.FRONT, 6), TilePosition(Face.LEFT, 8))
                    2 -> Pair(TilePosition(Face.FRONT, 8), TilePosition(Face.RIGHT, 6))
                    8 -> Pair(TilePosition(Face.BACK, 6), TilePosition(Face.RIGHT, 8))
                    6 -> Pair(TilePosition(Face.BACK, 8), TilePosition(Face.LEFT, 6))
                    else -> throw failNotCorner()
                }
            }
        }
    }

    /** Returns whether these three tile positions are on the same cubie */
    fun isOnSameCubie(tile1: TilePosition, tile2: TilePosition, tile3: TilePosition): Boolean {
        return isOnSameCubie(tile1, tile2) && isOnSameCubie(tile2, tile3)
    }

    /** Returns whether these two tile positions are on the same cubie */
    fun isOnSameCubie(tile1: TilePosition, tile2: TilePosition): Boolean {
        if (tile1 == tile2) return true
        return when (tile1.index) {
            1, 3, 5, 7 -> tile2 == getOtherTilePositionOnEdgeCubie(tile1)
            0, 2, 6, 8 -> (tile2 == getOtherTilePositionsOnCornerCubie(tile1).first) ||
                    (tile2 == getOtherTilePositionsOnCornerCubie(tile1).second)
            else -> false
        }
    }

    /** returns whether this tile position falls on a center cubie */
    fun isOnCenterCubie(tile: TilePosition): Boolean {
        return (tile.index == 4)
    }

    /** returns whether this position falls on an edge cubie */
    fun isOnEdgeCubie(tile: TilePosition): Boolean {
        return when (tile.index) {
            1, 3, 5, 7 -> true
            else -> false
        }
    }

    /** returns whether this position falls on a corner cubie */
    fun isOnCornerCubie(tile: TilePosition): Boolean {
        return when (tile.index) {
            0, 2, 6, 8 -> true
            else -> false
        }
    }
}

/* =============================================== ERROR FUNCTIONS =================================================  */

private fun failFacesNotAdjacent(): IllegalArgumentException {
    return IllegalArgumentException("invalid args: provided faces are not adjacent")
}
private fun failInvalidNumberOfFaces(): IllegalArgumentException {
    return IllegalArgumentException("invalid args: invalid number of faces provided")
}
private fun failInvalidNumberOfTiles(): IllegalArgumentException {
    return IllegalArgumentException("invalid args: invalid number of tiles provided")
}
private fun failNotEdge(): IllegalArgumentException {
    return IllegalArgumentException("invalid args: provided tile is not on an edge cubie")
}
private fun failNotCorner(): IllegalArgumentException {
    return IllegalArgumentException("invalid args: provided tile is not on a corner cubie")
}
private fun failInvalidTilePosition(): IllegalArgumentException {
    return IllegalArgumentException("invalid args: no such tile position on a standard cubie")
}