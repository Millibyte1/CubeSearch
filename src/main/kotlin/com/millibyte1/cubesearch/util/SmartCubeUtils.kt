package com.millibyte1.cubesearch.util

import com.millibyte1.cubesearch.cube.ArrayCube
import com.millibyte1.cubesearch.cube.Twist

//Typealiases for variants of sum types nested inside sealed classes
typealias CenterTileSet = OrientedTileSet.CenterTileSet
typealias EdgeTileSet = OrientedTileSet.EdgeTileSet
typealias CornerTileSet = OrientedTileSet.CornerTileSet
typealias OrientedCenterCubie = OrientedCubie.OrientedCenterCubie
typealias OrientedEdgeCubie = OrientedCubie.OrientedEdgeCubie
typealias OrientedCornerCubie = OrientedCubie.OrientedCornerCubie

/** Data class wrapping a tile */
data class OrientedTile(val color: Int, val face: Twist.Face)
/** Algebraic sum type representing the coloration and position of a cubie on a 3x3 Rubik's cube */
sealed class OrientedTileSet {
    data class CenterTileSet(val tile1: OrientedTile) : OrientedTileSet() {
        override fun positionIsValid(): Boolean {
            return true
        }
        override fun colorIsValid(): Boolean {
            return tile1.color in 0..5
        }
        override fun containsFace(face: Twist.Face): Boolean {
            return tile1.face == face
        }
        override fun containsColor(color: Int): Boolean {
            return tile1.color == color
        }
        override fun colorEquals(other: OrientedTileSet): Boolean {
            if(this === other) return true
            if(other !is CenterTileSet) return false
            return tile1.color == other.tile1.color
        }
        override fun positionEquals(other: OrientedTileSet): Boolean {
            if(this === other) return true
            if(other !is CenterTileSet) return false
            return tile1.face == other.tile1.face
        }
        override fun equals(other: Any?): Boolean {
            if(this === other) return true
            if(other !is CenterTileSet) return false
            return tile1.color == other.tile1.color && tile1.face == other.tile1.face
        }
        override fun hashCode(): Int {
            return tile1.hashCode()
        }
    }
    data class EdgeTileSet(val tile1: OrientedTile, val tile2: OrientedTile) : OrientedTileSet() {
        override fun positionIsValid(): Boolean {
            return containsFaces(Twist.Face.UP, Twist.Face.FRONT) ||
                   containsFaces(Twist.Face.UP, Twist.Face.BACK) ||
                   containsFaces(Twist.Face.UP, Twist.Face.LEFT) ||
                   containsFaces(Twist.Face.UP, Twist.Face.RIGHT) ||
                   containsFaces(Twist.Face.DOWN, Twist.Face.FRONT) ||
                   containsFaces(Twist.Face.DOWN, Twist.Face.BACK) ||
                   containsFaces(Twist.Face.DOWN, Twist.Face.LEFT) ||
                   containsFaces(Twist.Face.DOWN, Twist.Face.RIGHT) ||
                   containsFaces(Twist.Face.FRONT, Twist.Face.LEFT) ||
                   containsFaces(Twist.Face.FRONT, Twist.Face.RIGHT) ||
                   containsFaces(Twist.Face.BACK, Twist.Face.LEFT) ||
                   containsFaces(Twist.Face.BACK, Twist.Face.RIGHT)
        }
        override fun colorIsValid(): Boolean {
            return containsColors(4, 0) ||
                   containsColors(4, 1) ||
                   containsColors(4, 2) ||
                   containsColors(4, 3) ||
                   containsColors(5, 0) ||
                   containsColors(5, 1) ||
                   containsColors(5, 2) ||
                   containsColors(5, 3) ||
                   containsColors(0, 2) ||
                   containsColors(0, 3) ||
                   containsColors(1, 2) ||
                   containsColors(1, 3)
        }
        override fun containsFace(face: Twist.Face): Boolean {
            return tile1.face == face || tile2.face == face
        }
        override fun containsColor(color: Int): Boolean {
            return tile1.color == color || tile2.color == color
        }
        override fun positionEquals(other: OrientedTileSet): Boolean {
            if(this === other) return true
            if(other !is EdgeTileSet) return false
            return (tile1.face == other.tile1.face && tile2.face == other.tile2.face) ||
                   (tile1.face == other.tile2.face && tile2.face == other.tile1.face)
        }
        override fun colorEquals(other: OrientedTileSet): Boolean {
            if(this === other) return true
            if(other !is EdgeTileSet) return false
            return (tile1.color == other.tile1.color && tile2.color == other.tile2.color) ||
                   (tile1.color == other.tile2.color && tile2.color == other.tile1.color)
        }
        override fun equals(other: Any?): Boolean {
            if(this === other) return true
            if(other !is EdgeTileSet) return false
            return ((tile1.face == other.tile1.face && tile2.face == other.tile2.face) &&
                           (tile1.color == other.tile1.color && tile2.color == other.tile2.color)) ||
                   ((tile1.face == other.tile2.face && tile2.face == other.tile1.face) &&
                           (tile1.color == other.tile2.color && tile2.color == other.tile1.color))
        }
        override fun hashCode(): Int {
            var result = tile1.hashCode()
            result = 31 * result + tile2.hashCode()
            return result
        }
    }
    data class CornerTileSet(val tile1: OrientedTile, val tile2: OrientedTile, val tile3: OrientedTile) : OrientedTileSet() {
        override fun positionIsValid(): Boolean {
            return containsFaces(Twist.Face.UP, Twist.Face.FRONT, Twist.Face.LEFT) ||
                   containsFaces(Twist.Face.UP, Twist.Face.FRONT, Twist.Face.RIGHT) ||
                   containsFaces(Twist.Face.UP, Twist.Face.BACK, Twist.Face.LEFT) ||
                   containsFaces(Twist.Face.UP, Twist.Face.BACK, Twist.Face.RIGHT) ||
                   containsFaces(Twist.Face.DOWN, Twist.Face.FRONT, Twist.Face.LEFT) ||
                   containsFaces(Twist.Face.DOWN, Twist.Face.FRONT, Twist.Face.RIGHT) ||
                   containsFaces(Twist.Face.DOWN, Twist.Face.BACK, Twist.Face.LEFT) ||
                   containsFaces(Twist.Face.DOWN, Twist.Face.BACK, Twist.Face.RIGHT)
        }
        override fun colorIsValid(): Boolean {
            return containsColors(4, 0, 2) ||
                   containsColors(4, 0, 3) ||
                   containsColors(4, 1, 2) ||
                   containsColors(4, 1, 3) ||
                   containsColors(5, 0, 2) ||
                   containsColors(5, 0, 3) ||
                   containsColors(5, 1, 2) ||
                   containsColors(5, 1, 3)
        }
        override fun containsFace(face: Twist.Face): Boolean {
            return tile1.face == face || tile2.face == face || tile3.face == face
        }
        override fun containsColor(color: Int): Boolean {
            return tile1.color == color || tile2.color == color || tile3.color == color
        }
        override fun positionEquals(other: OrientedTileSet): Boolean {
            if(this === other) return true
            if(other !is CornerTileSet) return false
            return (tile1.face == other.tile1.face && tile2.face == other.tile2.face && tile3.face == other.tile3.face) ||
                   (tile1.face == other.tile1.face && tile2.face == other.tile3.face && tile3.face == other.tile2.face) ||
                   (tile1.face == other.tile2.face && tile2.face == other.tile1.face && tile3.face == other.tile3.face) ||
                   (tile1.face == other.tile2.face && tile2.face == other.tile3.face && tile3.face == other.tile1.face) ||
                   (tile1.face == other.tile3.face && tile2.face == other.tile2.face && tile3.face == other.tile1.face) ||
                   (tile1.face == other.tile3.face && tile2.face == other.tile1.face && tile3.face == other.tile2.face)
        }
        override fun colorEquals(other: OrientedTileSet): Boolean {
            if(this === other) return true
            if(other !is CornerTileSet) return false
            return (tile1.color == other.tile1.color && tile2.color == other.tile2.color && tile3.color == other.tile3.color) ||
                   (tile1.color == other.tile1.color && tile2.color == other.tile3.color && tile3.color == other.tile2.color) ||
                   (tile1.color == other.tile2.color && tile2.color == other.tile1.color && tile3.color == other.tile3.color) ||
                   (tile1.color == other.tile2.color && tile2.color == other.tile3.color && tile3.color == other.tile1.color) ||
                   (tile1.color == other.tile3.color && tile2.color == other.tile2.color && tile3.color == other.tile1.color) ||
                   (tile1.color == other.tile3.color && tile2.color == other.tile1.color && tile3.color == other.tile2.color)
        }
        override fun equals(other: Any?): Boolean {
            if(this === other) return true
            if(other !is CornerTileSet) return false
            // U-G-L-Y, you ain't got no alibi, YOU UGLY!
            return ((tile1.color == other.tile1.color && tile2.color == other.tile2.color && tile3.color == other.tile3.color) &&
                            (tile1.face == other.tile1.face && tile2.face == other.tile2.face && tile3.face == other.tile3.face)) ||
                    ((tile1.color == other.tile1.color && tile2.color == other.tile3.color && tile3.color == other.tile2.color) &&
                            (tile1.face == other.tile1.face && tile2.face == other.tile3.face && tile3.face == other.tile2.face)) ||
                    ((tile1.color == other.tile2.color && tile2.color == other.tile1.color && tile3.color == other.tile3.color) &&
                            (tile1.face == other.tile2.face && tile2.face == other.tile1.face && tile3.face == other.tile3.face)) ||
                    ((tile1.color == other.tile2.color && tile2.color == other.tile3.color && tile3.color == other.tile1.color) &&
                            (tile1.face == other.tile2.face && tile2.face == other.tile3.face && tile3.face == other.tile1.face)) ||
                    ((tile1.color == other.tile3.color && tile2.color == other.tile2.color && tile3.color == other.tile1.color) &&
                            (tile1.face == other.tile3.face && tile2.face == other.tile2.face && tile3.face == other.tile1.face)) ||
                    ((tile1.color == other.tile3.color && tile2.color == other.tile1.color && tile3.color == other.tile2.color) &&
                            (tile1.face == other.tile3.face && tile2.face == other.tile1.face && tile3.face == other.tile2.face))
        }
        override fun hashCode(): Int {
            var result = tile1.hashCode()
            result = 31 * result + tile2.hashCode()
            result = 31 * result + tile3.hashCode()
            return result
        }
    }
    /** Returns whether this is a valid cubie for a standard cube */
    fun isValid(): Boolean { return positionIsValid() && colorIsValid() }
    /** Returns whether this position is actually geometrically possible */
    abstract fun positionIsValid(): Boolean
    /** Returns whether a cubie with this coloration could exist on a standard cube */
    abstract fun colorIsValid(): Boolean
    /** Returns whether this position lies on the provided face */
    abstract fun containsFace(face: Twist.Face): Boolean
    /** Returns whether this position lies on all of the provided faces */
    fun containsFaces(vararg faces: Twist.Face): Boolean { return faces.all { face -> containsFace(face) } }
    /** Returns whether this cubie has a tile with the provided color */
    abstract fun containsColor(color: Int): Boolean
    /** Returns whether this cubie has tiles with each of the provided colors */
    fun containsColors(vararg colors: Int): Boolean { return colors.all { color -> containsColor(color) } }
    /** Returns whether the position of this tileset equals the position of the given tileset */
    abstract fun positionEquals(other: OrientedTileSet): Boolean
    /** Returns whether the color of this tileset equals the color of the given tileset */
    abstract fun colorEquals(other: OrientedTileSet): Boolean
    companion object {
        /** Factory fun for all variants of OrientedTileSet */
        fun makeTileSet(vararg tiles: OrientedTile): OrientedTileSet {
            tiles.sort()
            return when(tiles.size) {
                1 -> CenterTileSet(tiles[0])
                2 -> EdgeTileSet(tiles[0], tiles[1])
                3 -> CornerTileSet(tiles[0], tiles[1], tiles[2])
                else -> throw failInvalidNumberOfTiles()
            }
        }
    }
}

//TODO: eliminate code duplication by giving OrientedCubie base class the values
/** Algebraic sum type representing a cubie on a 3x3 Rubik's cube */
sealed class OrientedCubie {
    /** A center cubie has a tileset but no orientation since center cubies have only one tile */
    data class OrientedCenterCubie(val tileSet: CenterTileSet) : OrientedCubie() {
        override fun getTileSet(): OrientedTileSet {
            return tileSet
        }
        override fun getOrientationValue(): Int {
            return 0
        }
        override fun positionEquals(other: OrientedCubie): Boolean {
            return tileSet.positionEquals(other.getTileSet())
        }
        override fun colorEquals(other: OrientedCubie): Boolean {
            return tileSet.colorEquals(other.getTileSet())
        }
        override fun orientationEquals(other: OrientedCubie): Boolean {
            return true
        }
        override fun positionIsValid(): Boolean {
            return tileSet.positionIsValid()
        }
        override fun colorIsValid(): Boolean {
            return tileSet.colorIsValid()
        }
        override fun orientationIsValid(): Boolean {
            return true
        }
    }
    data class OrientedEdgeCubie(val tileSet: EdgeTileSet, val orientation: Int) : OrientedCubie() {
        override fun getTileSet(): OrientedTileSet {
            return tileSet
        }
        override fun getOrientationValue(): Int {
            return orientation
        }
        override fun positionEquals(other: OrientedCubie): Boolean {
            return tileSet.positionEquals(other.getTileSet())
        }
        override fun colorEquals(other: OrientedCubie): Boolean {
            return tileSet.colorEquals(other.getTileSet())
        }
        override fun orientationEquals(other: OrientedCubie): Boolean {
            return orientation == other.getOrientationValue()
        }
        override fun positionIsValid(): Boolean {
            return tileSet.positionIsValid()
        }
        override fun colorIsValid(): Boolean {
            return tileSet.colorIsValid()
        }
        override fun orientationIsValid(): Boolean {
            return orientation in 0..1
        }
    }
    data class OrientedCornerCubie(val tileSet: CornerTileSet, val orientation: Int) : OrientedCubie() {
        override fun getTileSet(): OrientedTileSet {
            return tileSet
        }
        override fun getOrientationValue(): Int {
            return orientation
        }
        override fun positionEquals(other: OrientedCubie): Boolean {
            return tileSet.positionEquals(other.getTileSet())
        }
        override fun colorEquals(other: OrientedCubie): Boolean {
            return tileSet.colorEquals(other.getTileSet())
        }
        override fun orientationEquals(other: OrientedCubie): Boolean {
            return orientation == other.getOrientationValue()
        }
        override fun positionIsValid(): Boolean {
            return tileSet.positionIsValid()
        }
        override fun colorIsValid(): Boolean {
            return tileSet.colorIsValid()
        }
        override fun orientationIsValid(): Boolean {
            return orientation in 0..2
        }
    }

    /** gets the tileset of this cubie */
    abstract fun getTileSet(): OrientedTileSet
    /** gets the orientation value of this cubie */
    abstract fun getOrientationValue(): Int

    /** returns whether the position of this cubie equals that of the provided cubie */
    abstract fun positionEquals(other: OrientedCubie): Boolean
    /** returns whether the coloration of this cubie equals that of the provided cubie */
    abstract fun colorEquals(other: OrientedCubie): Boolean
    /** returns whether the orientation of this cubie equals that of the provided cubie */
    abstract fun orientationEquals(other: OrientedCubie): Boolean

    /** Returns whether this position lies on the provided face */
    fun containsFace(face: Twist.Face): Boolean { return getTileSet().containsFace(face) }
    /** Returns whether this position lies on all of the provided faces */
    fun containsFaces(vararg faces: Twist.Face): Boolean { return faces.all { face -> containsFace(face) } }
    /** Returns whether this cubie has a tile with the provided color */
    fun containsColor(color: Int): Boolean { return getTileSet().containsColor(color) }
    /** Returns whether this cubie has tiles with each of the provided colors */
    fun containsColors(vararg colors: Int): Boolean { return colors.all { color -> containsColor(color) } }

    /** Returns whether this is a valid cubie */
    fun isValid(): Boolean { return positionIsValid() && colorIsValid() && orientationIsValid() }
    /** Returns whether the position and orientation of this cubie are valid */
    fun isValidIgnoreColor(): Boolean { return positionIsValid() && orientationIsValid() }
    /** Returns whether this position is actually geometrically possible */
    abstract fun positionIsValid(): Boolean
    /** Returns whether a cubie with this coloration could exist on a standard cube */
    abstract fun colorIsValid(): Boolean
    /** Returns whether the orientation value for this cubie is valid */
    abstract fun orientationIsValid(): Boolean
}

object SmartCubeUtils {
    /** Gets an array of OrientedCubies from the data array */
    fun getCubies(data: Array<IntArray>): Array<OrientedCubie> {
        return getOrientedCubiesFromArrayCube(ArrayCube(data))
    }
    /** Gets an array of OrientedCubies from an ArrayCube */
    private fun getOrientedCubiesFromArrayCube(cube: ArrayCube): Array<OrientedCubie> {
        //gets the unoriented cubies of the array cubie
        val centers = ArrayCubeUtils.getCenters(cube)
        val edges = ArrayCubeUtils.getEdges(cube)
        val corners = ArrayCubeUtils.getCorners(cube)
        val retval = ArrayList<OrientedCubie>(26)

        //builds the oriented centers
        for(center in centers) {
            val tileSet = getTileSetFromUnorientedCubie(center) as CenterTileSet
            retval.add(OrientedCenterCubie(tileSet))
        }
        //builds the oriented edges
        for(edge in edges) {
            val tileSet = getTileSetFromUnorientedCubie(edge) as EdgeTileSet
            val orientation = SolvabilityUtils.getEdgeOrientation(edge, cube)
            retval.add(OrientedEdgeCubie(tileSet, orientation))
        }
        //builds the oriented corners
        for(corner in corners) {
            val tileSet = getTileSetFromUnorientedCubie(corner) as CornerTileSet
            val orientation = SolvabilityUtils.getCornerOrientation(corner)
            retval.add(OrientedCornerCubie(tileSet, orientation))
        }
        //returns the array of all the oriented cubies
        return retval.toTypedArray()
    }
    /** Gets the tileset of an OrientedCubie */
    fun getTileSetFromUnorientedCubie(cubie: Cubie): OrientedTileSet {
        return when(cubie) {
            is CenterCubie -> CenterTileSet(OrientedTile(cubie.tile1.color, cubie.tile1.pos.face))
            is EdgeCubie -> EdgeTileSet(OrientedTile(cubie.tile1.color, cubie.tile1.pos.face),
                OrientedTile(cubie.tile2.color, cubie.tile2.pos.face))
            is CornerCubie -> CornerTileSet(OrientedTile(cubie.tile1.color, cubie.tile1.pos.face),
                OrientedTile(cubie.tile2.color, cubie.tile2.pos.face),
                OrientedTile(cubie.tile3.color, cubie.tile3.pos.face))
        }
    }
}

private fun failInvalidNumberOfTiles(): IllegalArgumentException {
    return IllegalArgumentException("invalid args: invalid number of tiles provided")
}