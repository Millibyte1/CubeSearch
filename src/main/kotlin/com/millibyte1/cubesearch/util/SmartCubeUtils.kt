package com.millibyte1.cubesearch.util

import com.millibyte1.cubesearch.cube.ArrayCube
import com.millibyte1.cubesearch.cube.Twist

//Typealiases for variants of sum types nested inside sealed classes
typealias CenterCubiePosition = CubiePosition.CenterCubiePosition
typealias EdgeCubiePosition = CubiePosition.EdgeCubiePosition
typealias CornerCubiePosition = CubiePosition.CornerCubiePosition
typealias CenterCubieColor = CubieColor.CenterCubieColor
typealias EdgeCubieColor = CubieColor.EdgeCubieColor
typealias CornerCubieColor = CubieColor.CornerCubieColor
typealias OrientedCenterCubie = OrientedCubie.OrientedCenterCubie
typealias OrientedEdgeCubie = OrientedCubie.OrientedEdgeCubie
typealias OrientedCornerCubie = OrientedCubie.OrientedCornerCubie

/** Algebraic sum type representing the position of a cubie on a 3x3 Rubik's cube */
sealed class CubiePosition {
    data class CenterCubiePosition(val face1: Twist.Face) : CubiePosition() {
        override fun isValid(): Boolean { return true }
        override fun containsFace(face: Twist.Face): Boolean { return face1 == face }
    }
    data class EdgeCubiePosition(val face1: Twist.Face, val face2: Twist.Face) : CubiePosition() {
        override fun isValid(): Boolean {
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
        override fun containsFace(face: Twist.Face): Boolean { return face1 == face || face2 == face }
        override fun equals(other: Any?): Boolean {
            if(this === other) return true
            if(other !is EdgeCubiePosition) return false
            return (face1 == other.face1 && face2 == other.face2) ||
                   (face1 == other.face2 && face2 == other.face1)
        }
        override fun hashCode(): Int {
            var result = face1.hashCode()
            result = 31 * result + face2.hashCode()
            return result
        }
    }
    data class CornerCubiePosition(val face1: Twist.Face, val face2: Twist.Face, val face3: Twist.Face) : CubiePosition() {
        override fun isValid(): Boolean {
            return containsFaces(Twist.Face.UP, Twist.Face.FRONT, Twist.Face.LEFT) ||
                   containsFaces(Twist.Face.UP, Twist.Face.FRONT, Twist.Face.RIGHT) ||
                   containsFaces(Twist.Face.UP, Twist.Face.BACK, Twist.Face.LEFT) ||
                   containsFaces(Twist.Face.UP, Twist.Face.BACK, Twist.Face.RIGHT) ||
                   containsFaces(Twist.Face.DOWN, Twist.Face.FRONT, Twist.Face.LEFT) ||
                   containsFaces(Twist.Face.DOWN, Twist.Face.FRONT, Twist.Face.RIGHT) ||
                   containsFaces(Twist.Face.DOWN, Twist.Face.BACK, Twist.Face.LEFT) ||
                   containsFaces(Twist.Face.DOWN, Twist.Face.BACK, Twist.Face.RIGHT)
        }
        override fun containsFace(face: Twist.Face): Boolean { return face1 == face || face2 == face || face3 == face }
        override fun equals(other: Any?): Boolean {
            if(this === other) return true
            if(other !is CornerCubiePosition) return false
            return (face1 == other.face1 && face2 == other.face2 && face3 == other.face3) ||
                   (face1 == other.face1 && face2 == other.face3 && face3 == other.face2) ||
                   (face1 == other.face2 && face2 == other.face1 && face3 == other.face3) ||
                   (face1 == other.face2 && face2 == other.face3 && face3 == other.face1) ||
                   (face1 == other.face3 && face2 == other.face2 && face3 == other.face1) ||
                   (face1 == other.face3 && face2 == other.face1 && face3 == other.face2)
        }
        override fun hashCode(): Int {
            var result = face1.hashCode()
            result = 31 * result + face2.hashCode()
            result = 31 * result + face3.hashCode()
            return result
        }
    }
    /** Returns whether this position is actually geometrically possible */
    abstract fun isValid(): Boolean
    /** Returns whether this position lies on the provided face */
    abstract fun containsFace(face: Twist.Face): Boolean
    /** Returns whether this position lies on all of the provided faces */
    fun containsFaces(vararg faces: Twist.Face): Boolean { return faces.all { face -> containsFace(face) } }

    companion object {
        /** Factory function for all variants of CubiePosition */
        fun makePosition(vararg faces: Twist.Face): CubiePosition {
            faces.sort()
            return when(faces.size) {
                1 -> CenterCubiePosition(faces[0])
                2 -> EdgeCubiePosition(faces[0], faces[1])
                3 -> CornerCubiePosition(faces[0], faces[1], faces[2])
                else -> throw failInvalidNumberOfFaces()
            }
        }
    }
}

/** Algebraic sum type representing the coloration of a cubie on a 3x3 Rubik's cube */
sealed class CubieColor {
    data class CenterCubieColor(val color1: Int) : CubieColor() {
        override fun isValid(): Boolean { return color1 in 0..5 }
        override fun containsColor(color: Int): Boolean { return color1 == color }
    }
    data class EdgeCubieColor(val color1: Int, val color2: Int) : CubieColor() {
        override fun isValid(): Boolean {
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
        override fun containsColor(color: Int): Boolean { return color1 == color || color2 == color }
        override fun equals(other: Any?): Boolean {
            if(this === other) return true
            if(other !is EdgeCubieColor) return false
            return (color1 == other.color1 && color2 == other.color2) ||
                   (color1 == other.color2 && color2 == other.color1)
        }
        override fun hashCode(): Int {
            var result = color1.hashCode()
            result = result * 31 + color2.hashCode()
            return result
        }
    }
    data class CornerCubieColor(val color1: Int, val color2: Int, val color3: Int) : CubieColor() {
        override fun isValid(): Boolean {
            return containsColors(4, 0, 2) ||
                   containsColors(4, 0, 3) ||
                   containsColors(4, 1, 2) ||
                   containsColors(4, 1, 3) ||
                   containsColors(5, 0, 2) ||
                   containsColors(5, 0, 3) ||
                   containsColors(5, 1, 2) ||
                   containsColors(5, 1, 3)
        }
        override fun containsColor(color: Int): Boolean { return color1 == color || color2 == color || color3 == color }
        override fun equals(other: Any?): Boolean {
            if(this === other) return true
            if(other !is CornerCubieColor) return false
            return (color1 == other.color1 && color2 == other.color2 && color3 == other.color3) ||
                   (color1 == other.color1 && color2 == other.color3 && color3 == other.color2) ||
                   (color1 == other.color2 && color2 == other.color1 && color3 == other.color3) ||
                   (color1 == other.color2 && color2 == other.color3 && color3 == other.color1) ||
                   (color1 == other.color3 && color2 == other.color2 && color3 == other.color1) ||
                   (color1 == other.color3 && color2 == other.color1 && color3 == other.color2)
        }
        override fun hashCode(): Int {
            var result = color1.hashCode()
            result = 31 * result + color2.hashCode()
            result = 31 * result + color3.hashCode()
            return result
        }
    }
    /** Returns whether a cubie with this coloration could exist on a standard cube */
    abstract fun isValid(): Boolean
    /** Returns whether this cubie has a tile with the provided color */
    abstract fun containsColor(color: Int): Boolean
    /** Returns whether this cubie has tiles with each of the provided colors */
    fun containsColors(vararg colors: Int): Boolean { return colors.all { color -> containsColor(color) } }

    companion object {
        /** Factory function for all variants of CubieColor */
        fun makeColor(vararg colors: Int): CubieColor {
            colors.sort()
            return when(colors.size) {
                1 -> CenterCubieColor(colors[0])
                2 -> EdgeCubieColor(colors[0], colors[1])
                3 -> CornerCubieColor(colors[0], colors[1], colors[2])
                else -> throw failInvalidNumberOfColors()
            }
        }
    }
}

/** Algebraic sum type representing a cubie on a 3x3 Rubik's cube */
sealed class OrientedCubie {
    /** A center cubie with a position and color; obviously doesn't have an orientation */
    data class OrientedCenterCubie(
        internal val position: CenterCubiePosition,
        internal val color: CenterCubieColor
    ) : OrientedCubie() {

        override fun getColor(): CubieColor { return color }
        override fun getPosition(): CubiePosition { return position }
        override fun getOrientation(): Int { return 0 }

        override fun positionEquals(other: OrientedCubie): Boolean { return getPosition() == other.getPosition() }
        override fun colorEquals(other: OrientedCubie): Boolean { return getColor() == other.getColor() }
        override fun orientationEquals(other: OrientedCubie): Boolean { return true }

        override fun isValid(): Boolean { return isValidIgnoreColor() && color.isValid() }
        override fun isValidIgnoreColor(): Boolean { return position.isValid() }
    }
    /** An edge cubie with a position, color, and orientation */
    data class OrientedEdgeCubie(
        internal val position: EdgeCubiePosition,
        internal val color: EdgeCubieColor,
        internal val orientation: Int
    ) : OrientedCubie() {

        override fun getColor(): CubieColor { return color }
        override fun getPosition(): CubiePosition { return position }
        override fun getOrientation(): Int { return orientation }

        override fun positionEquals(other: OrientedCubie): Boolean { return getPosition() == other.getPosition() }
        override fun colorEquals(other: OrientedCubie): Boolean { return getColor() == other.getColor() }
        override fun orientationEquals(other: OrientedCubie): Boolean { return getOrientation() == other.getOrientation() }

        override fun isValid(): Boolean { return isValidIgnoreColor() && color.isValid() }
        override fun isValidIgnoreColor(): Boolean { return position.isValid() && orientation in 0..1 }
    }
    /** A corner cubie with a position, color, and orientation */
    data class OrientedCornerCubie(
        internal val position: CornerCubiePosition,
        internal val color: CornerCubieColor,
        internal val orientation: Int
    ) : OrientedCubie() {

        override fun getColor(): CubieColor { return color }
        override fun getPosition(): CubiePosition { return position }
        override fun getOrientation(): Int { return orientation }

        override fun positionEquals(other: OrientedCubie): Boolean { return getPosition() == other.getPosition() }
        override fun colorEquals(other: OrientedCubie): Boolean { return getColor() == other.getColor() }
        override fun orientationEquals(other: OrientedCubie): Boolean { return getOrientation() == other.getOrientation() }

        override fun isValid(): Boolean { return isValidIgnoreColor() && color.isValid() }
        override fun isValidIgnoreColor(): Boolean { return position.isValid() && orientation in 0..2 }
    }

    /** gets the position of this cubie */
    abstract fun getPosition(): CubiePosition
    /** gets the orientation of this cubie */
    abstract fun getColor(): CubieColor
    /** gets the orientation value of this cubie */
    abstract fun getOrientation(): Int

    /** returns whether the position of this cubie equals that of the provided cubie */
    abstract fun positionEquals(other: OrientedCubie): Boolean
    /** returns whether the coloration of this cubie equals that of the provided cubie */
    abstract fun colorEquals(other: OrientedCubie): Boolean
    /** returns whether the orientation of this cubie equals that of the provided cubie */
    abstract fun orientationEquals(other: OrientedCubie): Boolean

    /** Returns whether this position lies on the provided face */
    fun containsFace(face: Twist.Face): Boolean { return getPosition().containsFace(face) }
    /** Returns whether this position lies on all of the provided faces */
    fun containsFaces(vararg faces: Twist.Face): Boolean { return faces.all { face -> containsFace(face) } }
    /** Returns whether this cubie has a tile with the provided color */
    fun containsColor(color: Int): Boolean { return getColor().containsColor(color) }
    /** Returns whether this cubie has tiles with each of the provided colors */
    fun containsColors(vararg colors: Int): Boolean { return colors.all { color -> containsColor(color) } }

    /** Returns whether this is a valid cubie */
    abstract fun isValid(): Boolean
    /** Returns whether the position and orientation of this cubie are valid */
    abstract fun isValidIgnoreColor(): Boolean

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is OrientedCubie) return false
        return positionEquals(other) && colorEquals(other) && orientationEquals(other)
    }
    override fun hashCode(): Int {
        var result = getPosition().hashCode()
        result = 31 * result + getColor().hashCode()
        result = 31 * result + getOrientation().hashCode()
        return result
    }
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
            val color = getCubieColorFromUnorientedCubie(center) as CenterCubieColor
            val position = getCubiePositionFromUnorientedCubie(center) as CenterCubiePosition
            retval.add(OrientedCenterCubie(position, color))
        }
        //builds the oriented edges
        for(edge in edges) {
            val color = getCubieColorFromUnorientedCubie(edge) as EdgeCubieColor
            val position = getCubiePositionFromUnorientedCubie(edge) as EdgeCubiePosition
            val orientation = SolvabilityUtils.getEdgeOrientation(edge, cube)
            retval.add(OrientedEdgeCubie(position, color, orientation))
        }
        //builds the oriented corners
        for(corner in corners) {
            val color = getCubieColorFromUnorientedCubie(corner) as CornerCubieColor
            val position = getCubiePositionFromUnorientedCubie(corner) as CornerCubiePosition
            val orientation = SolvabilityUtils.getCornerOrientation(corner)
            retval.add(OrientedCornerCubie(position, color, orientation))
        }
        //returns the array of all the oriented cubies
        return retval.toTypedArray()
    }
    fun getCubieColorFromUnorientedCubie(cubie: Cubie): CubieColor {
        return when(cubie) {
            is CenterCubie -> CubieColor.makeColor(cubie.tile1.color)
            is EdgeCubie -> CubieColor.makeColor(cubie.tile1.color, cubie.tile2.color)
            is CornerCubie -> CubieColor.makeColor(cubie.tile1.color, cubie.tile2.color, cubie.tile3.color)
        }
    }
    fun getCubiePositionFromUnorientedCubie(cubie: Cubie): CubiePosition {
        return when(cubie) {
            is CenterCubie -> CubiePosition.makePosition(cubie.tile1.pos.face)
            is EdgeCubie -> CubiePosition.makePosition(cubie.tile1.pos.face, cubie.tile2.pos.face)
            is CornerCubie -> CubiePosition.makePosition(cubie.tile1.pos.face, cubie.tile2.pos.face, cubie.tile3.pos.face)
        }
    }

}

private fun failInvalidNumberOfFaces(): IllegalArgumentException {
    return IllegalArgumentException("invalid args: invalid number of faces provided")
}
private fun failInvalidNumberOfColors(): IllegalArgumentException {
    return IllegalArgumentException("invalid args: invalid number of colors provided")
}