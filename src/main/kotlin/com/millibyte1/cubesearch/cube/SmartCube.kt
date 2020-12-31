package com.millibyte1.cubesearch.cube

import com.millibyte1.cubesearch.util.*
import java.io.Serializable

/**
 * A cube implementation that directly stores cubie positions and orientations in order to rapidly compute
 * important information about the cube (solvability, configuration index, etc.)
 *
 * @constructor constructs a SmartCube directly from an array of oriented cubies
 * @param cubies the array of all 26 cubies for this cube. Centers followed by edges followed by corners.
 *
 * Required order of center positions (0-5):
 * FRONT, BACK, LEFT, RIGHT, UP, DOWN.
 *
 * Required order of edge positions (6-17):
 * UP-FRONT, UP-BACK, UP-LEFT, UP-RIGHT,
 * DOWN-FRONT, DOWN-BACK, DOWN-LEFT, DOWN-RIGHT,
 * FRONT-LEFT, FRONT-RIGHT, BACK-LEFT, BACK-RIGHT,
 *
 * Required order of corner positions (18-25):
 * UP-FRONT-LEFT, UP-FRONT-RIGHT, UP-BACK-LEFT, UP-BACK-RIGHT,
 * DOWN-FRONT-LEFT, DOWN-FRONT-RIGHT, DOWN-BACK-LEFT, DOWN-BACK-RIGHT.
 *
 */
class SmartCube internal constructor(private var cubies: Array<OrientedCubie>) : MutableStandardCube<SmartCube>, Serializable {

    /**
     * returns the cube resulting from applying the given twist, without modifying this cube.
     * @param twist the twist we are applying to this cube
     * @return the cube resulting from applying the given twist
     */
    override fun twist(twist: Twist): SmartCube {
        return when(Twist.getEquivalentNumClockwiseQuarterTurns(twist)) {
            1 -> SmartCube(twist90(cubies, twist))
            2 -> SmartCube(twist90(SmartCube(twist90(cubies, twist)).cubies, twist))
            else -> SmartCube(twist90(SmartCube(twist90(SmartCube(twist90(cubies, twist)).cubies, twist)).cubies, twist))
        }
    }
    /**
     * Applies the given twist to this cube. Modifies this, but returns this as well.
     * @param twist the twist we are applying to this cube
     * @return this cube
     */
    override fun twistNoCopy(twist: Twist): SmartCube {
        for(i in 0 until Twist.getEquivalentNumClockwiseQuarterTurns(twist)) {
            cubies = twist90(cubies, twist)
        }
        return this
    }

    /** Gets a copy of the cubies, ordered by position */
    fun getCubies(): Array<OrientedCubie> {
        return cubies.copyOf()
    }
    /** Gets a copy of the centers, ordered by position */
    fun getCenters(): Array<OrientedCenterCubie> {
        val retval = ArrayList<OrientedCenterCubie>()
        for(i in 0..5) retval.add(cubies[i] as OrientedCenterCubie)
        return retval.toTypedArray()
    }
    /** Gets a copy of the edges, ordered by position */
    fun getEdges(): Array<OrientedEdgeCubie> {
        val retval = ArrayList<OrientedEdgeCubie>()
        for(i in 6..17) retval.add(cubies[i] as OrientedEdgeCubie)
        return retval.toTypedArray()
    }
    /** Gets a copy of the corners, ordered by position */
    fun getCorners(): Array<OrientedCornerCubie> {
        val retval = ArrayList<OrientedCornerCubie>()
        for(i in 18..25) retval.add(cubies[i] as OrientedCornerCubie)
        return retval.toTypedArray()
    }

    /**
     * Gets the cubie on this cube with the following position, if such a position exists
     * @param position the position of the desired cubie
     * @return the desired cubie
     * @throws IllegalArgumentException if an invalid position is provided
     */
    @Throws(IllegalArgumentException::class)
    fun getCubieWithPosition(position: CubiePosition): OrientedCubie {
        for(cubie in cubies) if(cubie.getPosition() == position) return cubie
        throw failInvalidPosition()
    }
    /**
     * Gets the cubie on this cube that lies on the following faces, if there is such a position.
     * @param faces the faces the desired cubie lies on
     * @return the desired cubie
     * @throws IllegalArgumentException if an invalid position is provided
     */
    @Throws(IllegalArgumentException::class)
    fun getCubieOnFaces(vararg faces: Twist.Face): OrientedCubie {
        return getCubieWithPosition(CubiePosition.makePosition(*faces))
    }
    /**
     * Gets the cubie on this cube with the following color, if any cubie with this color is present
     * @param color the color of the desired cubie
     * @return the desired cubie
     * @throws IllegalArgumentException if no cubie on this cube has this color
     */
    @Throws(IllegalArgumentException::class)
    fun getCubieWithColor(color: CubieColor): OrientedCubie {
        for(cubie in cubies) if(cubie.getColor() == color) return cubie
        throw failColorNotPresent()
    }

    fun toArrayCube(): ArrayCube {
        val data: Array<IntArray> = Array(6) { IntArray(9) { -1 } }
        /*
         * Required layout of data:
         *  . . . 0 1 2 . . . . . .
         *  . . . 3 U 5 . . . . . .
         *  . . . 6 7 8 . . . . . .
         *  0 1 2 0 1 2 0 1 2 0 1 2
         *  3 L 5 3 F 5 3 R 5 3 B 5
         *  6 7 8 6 7 8 6 7 8 6 7 8
         *  . . . 0 1 2 . . . . . .
         *  . . . 3 D 5 . . . . . .
         *  . . . 6 7 8 . . . . . .
         */
        //populates data from centers
        val centers = getCenters()
        data[0][4] = centers[0].color.color1
        data[1][4] = centers[1].color.color1
        data[2][4] = centers[2].color.color1
        data[3][4] = centers[3].color.color1
        data[4][4] = centers[4].color.color1
        data[5][4] = centers[5].color.color1
        //populates data from edges
        val edges = getEdges()
        
        //populates data from corners
        val corners = getCorners()
        TODO()
    }

    override fun equals(other: Any?): Boolean {
        if(other === this) return true
        if(other !is SmartCube) return false
        return cubies.contentEquals(other.cubies)
    }
    override fun hashCode(): Int {
        return cubies.contentHashCode()
    }
    override fun toString(): String { return toArrayCube().toString() }
}

/*
 * The corner orientation group is closed under arithmetic modulo 3 since each corner falls on 3 axes.
 * Let's define a corner's orientation as the number of clockwise rotations it takes to orient its up- or down-colored tile up or down.
 *
 * Let O1 be the corner originally in the top left of the face, O2 in the top right, O3 in the bottom right, O4 in the bottom left.
 * When we twist this face clockwise,
 * O1 becomes N2 and orientation increases by 2.
 * O2 becomes N3 and orientation increases by 1.
 * O3 becomes N4 and orientation increases by 2.
 * O4 becomes N1 and orientation increases by 1.
 * If this face is UP or DOWN, all orientations remain the same.
 *
 * The edge orientation group is closed under arithmetic modulo 2 since each edge falls on 2 axes.
 * Let's define an edge's orientation as follows:
 * if an edge can be correctly positioned and oriented via only UP, DOWN, LEFT, and RIGHT twists, its orientation is 0,
 * otherwise its orientation is 1.
 *
 * Let O5 be the edge originally at the top of a face, O6 at the right, O7 at the bottom, O8 at the left.
 * When we twist this face clockwise,
 * O5 becomes N6, O6 becomes N7, O7 becomes N8, O8 becomes N6.
 * If this face is FRONT or BACK, all orientations are incremented, otherwise all orientations remain the same.
 */
/** Returns the cubie array that results from a single 90 degree twist */
private fun twist90(cubies: Array<OrientedCubie>, twist: Twist): Array<OrientedCubie> {
    val copy = cubies.copyOf()
    //gets corner indices
    val o1Index = getO1Index(twist)
    val o2Index = getO2Index(twist)
    val o3Index = getO3Index(twist)
    val o4Index = getO4Index(twist)
    //grabs the corners
    val o1 = cubies[o1Index] as OrientedCornerCubie
    val o2 = cubies[o2Index] as OrientedCornerCubie
    val o3 = cubies[o3Index] as OrientedCornerCubie
    val o4 = cubies[o4Index] as OrientedCornerCubie
    //gets edge indices
    val o5Index = getO5Index(twist)
    val o6Index = getO6Index(twist)
    val o7Index = getO7Index(twist)
    val o8Index = getO8Index(twist)
    //grabs the edges
    val o5 = cubies[o5Index] as OrientedEdgeCubie
    val o6 = cubies[o6Index] as OrientedEdgeCubie
    val o7 = cubies[o7Index] as OrientedEdgeCubie
    val o8 = cubies[o8Index] as OrientedEdgeCubie

    //transforms and updates the corners
    val cornerOrientationIncrement = twistChangesCornerOrientations(twist)
    copy[o2Index] = OrientedCornerCubie(o2.position, o1.color, (o1.orientation + (2 * cornerOrientationIncrement)) % 3)
    copy[o3Index] = OrientedCornerCubie(o3.position, o2.color, (o2.orientation + (1 * cornerOrientationIncrement)) % 3)
    copy[o4Index] = OrientedCornerCubie(o4.position, o3.color, (o3.orientation + (2 * cornerOrientationIncrement)) % 3)
    copy[o5Index] = OrientedCornerCubie(o1.position, o4.color, (o4.orientation + (1 * cornerOrientationIncrement)) % 3)
    //transforms and updates the edges
    val edgeOrientationIncrement = twistChangesEdgeOrientations(twist)
    copy[o6Index] = OrientedEdgeCubie(o6.position, o5.color, (o5.orientation + edgeOrientationIncrement) % 2)
    copy[o7Index] = OrientedEdgeCubie(o7.position, o6.color, (o6.orientation + edgeOrientationIncrement) % 2)
    copy[o8Index] = OrientedEdgeCubie(o8.position, o7.color, (o7.orientation + edgeOrientationIncrement) % 2)
    copy[o5Index] = OrientedEdgeCubie(o5.position, o8.color, (o8.orientation + edgeOrientationIncrement) % 2)
    //returns
    return copy
}
/** Gets whether this twist changes edge orientation values (0 or 1) */
private fun twistChangesEdgeOrientations(twist: Twist): Int {
    return if(Twist.getFace(twist) == Twist.Face.FRONT || Twist.getFace(twist) == Twist.Face.BACK) 1 else 0
}
/** Gets whether this twist changes corner orientation values or not (0 or 1) */
private fun twistChangesCornerOrientations(twist: Twist): Int {
    return if(Twist.getFace(twist) == Twist.Face.UP || Twist.getFace(twist) == Twist.Face.DOWN) 0 else 1
}
/*
 * Required order of center positions (0-5):
 * FRONT, BACK, LEFT, RIGHT, UP, DOWN.
 *
 * Required order of edge positions (6-17):
 * UP-FRONT, UP-BACK, UP-LEFT, UP-RIGHT,
 * DOWN-FRONT, DOWN-BACK, DOWN-LEFT, DOWN-RIGHT,
 * FRONT-LEFT, FRONT-RIGHT, BACK-LEFT, BACK-RIGHT,
 *
 * Required order of corner positions (18-25):
 * UP-FRONT-LEFT, UP-FRONT-RIGHT, UP-BACK-LEFT, UP-BACK-RIGHT,
 * DOWN-FRONT-LEFT, DOWN-FRONT-RIGHT, DOWN-BACK-LEFT, DOWN-BACK-RIGHT
*/
private fun getO1Index(twist: Twist): Int {
    return when(Twist.getFace(twist)) {
        Twist.Face.FRONT -> 18 //up-front-left
        Twist.Face.BACK -> 21 //up-back-right
        Twist.Face.LEFT -> 20 //up-back-left
        Twist.Face.RIGHT -> 19 //up-front-right
        Twist.Face.UP -> 20 //up-back-left
        Twist.Face.DOWN -> 22 //down-front-left
    }
}
private fun getO2Index(twist: Twist): Int {
    return when(Twist.getFace(twist)) {
        Twist.Face.FRONT -> 19 //up-front-right
        Twist.Face.BACK -> 20 // up-back-left
        Twist.Face.LEFT -> 18 // up-front-left
        Twist.Face.RIGHT -> 21 //up-back-right
        Twist.Face.UP -> 21 //up-back-right
        Twist.Face.DOWN -> 23 //down-front-right
    }
}
private fun getO3Index(twist: Twist): Int {
    return when(Twist.getFace(twist)) {
        Twist.Face.FRONT -> 18 //down-front-right
        Twist.Face.BACK -> 24 //down-back-left
        Twist.Face.LEFT -> 22 //down-front-left
        Twist.Face.RIGHT -> 25 //down-back-right
        Twist.Face.UP -> 19 //up-front-right
        Twist.Face.DOWN -> 25 //down-back-right
    }
}
private fun getO4Index(twist: Twist): Int {
    return when(Twist.getFace(twist)) {
        Twist.Face.FRONT -> 18 //down-front-left
        Twist.Face.BACK -> 25 //down-back-right
        Twist.Face.LEFT -> 24 //down-back-left
        Twist.Face.RIGHT -> 23 //down-front-right
        Twist.Face.UP -> 18 //up-front-left
        Twist.Face.DOWN -> 24 //down-back-left
    }
}
private fun getO5Index(twist: Twist): Int {
    return when(Twist.getFace(twist)) {
        Twist.Face.FRONT -> 6 //up-front
        Twist.Face.BACK -> 7 //up-back
        Twist.Face.LEFT -> 8 //up-left
        Twist.Face.RIGHT -> 9 //up-right
        Twist.Face.UP -> 7 //up-back
        Twist.Face.DOWN -> 10 //down-front
    }
}
private fun getO6Index(twist: Twist): Int {
    return when(Twist.getFace(twist)) {
        Twist.Face.FRONT -> 15 //front-right
        Twist.Face.BACK -> 16 //back-left
        Twist.Face.LEFT -> 14 //front-left
        Twist.Face.RIGHT -> 17 //back-right
        Twist.Face.UP -> 9 //up-right
        Twist.Face.DOWN -> 13 //down-right
    }
}
private fun getO7Index(twist: Twist): Int {
    return when(Twist.getFace(twist)) {
        Twist.Face.FRONT -> 10 //down-front
        Twist.Face.BACK -> 11 //down-back
        Twist.Face.LEFT -> 12 //down-left
        Twist.Face.RIGHT -> 13 //down-right
        Twist.Face.UP -> 6 //up-front
        Twist.Face.DOWN -> 11 //down-back
    }
}
private fun getO8Index(twist: Twist): Int {
    return when(Twist.getFace(twist)) {
        Twist.Face.FRONT -> 14 //front-left
        Twist.Face.BACK -> 17 //back-right
        Twist.Face.LEFT -> 16 //back-left
        Twist.Face.RIGHT -> 15 //front-right
        Twist.Face.UP -> 8 //up-left
        Twist.Face.DOWN -> 12 //down-left
    }
}

/* =============================================== ERROR FUNCTIONS =================================================  */

private fun failInvalidPosition(): IllegalArgumentException {
    return IllegalArgumentException("invalid args: the position provided is geometrically impossible */")
}
private fun failColorNotPresent(): IllegalArgumentException {
    return IllegalArgumentException("invalid args: no cubie with the provided coloration is present on this cube")
}