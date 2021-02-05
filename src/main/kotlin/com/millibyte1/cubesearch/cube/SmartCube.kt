package com.millibyte1.cubesearch.cube

import com.millibyte1.cubesearch.util.ArrayCubeUtils
import com.millibyte1.cubesearch.util.SolvabilityUtils
import java.io.Serializable

/**
 * Class representing the configuration of a 3x3 Rubik's cube.
 * Uses an internal array, but keeps track of the orientation values of individual cubies to enable rapid analysis of
 * the state of the cube.
 *
 * @constructor constructs cube from a copy of the provided data
 * @param data the 6x9 array representing the desired cube. Format: (front, back, left, right, up, down)
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
 * @param edgePositions the array of position values of the 12 edge cubies, ordered by cubie number (coloration).
 * The enumeration of edge positions is as follows:
 * up-front, up-back, up-left, up-right,
 * down-front, down-back, down-left, down-right,
 * front-left, front-right, back-left, back-right
 * @param cornerPositions the array of position values of the 8 corner cubies, ordered by cubie number (coloration).
 * The enumeration of corner positions is as follows:
 * up-front-left, up-front-right, up-back-left, up-back-right,
 * down-front-left, down-front-right, down-back-left, down-back-right
 * @param edgeOrientations the array of orientation values of the 12 edge cubies, ordered by cubie number (coloration).
 * @param cornerOrientations the array of orientation values of the 8 corner cubies, ordered by cubie number (coloration).
 *
 */
class SmartCube internal constructor(
    var data: Array<IntArray>,
    var edgePositions: IntArray,
    var cornerPositions: IntArray,
    var edgeOrientations: IntArray,
    var cornerOrientations: IntArray
) : AnalyzableStandardCube, Serializable {

    /** Constructs the cube, computing initial orientations from the data array */
    internal constructor(data: Array<IntArray>) : this(data, IntArray(12), IntArray(8), IntArray(12), IntArray(8)) {
        //uses the existing array cube functionality to grab all the cubies
        val arrayCube = this.toArrayCube()
        val edges = ArrayCubeUtils.getEdges(arrayCube)
        val corners = ArrayCubeUtils.getCorners(arrayCube)
        //initializes edge positions
        edgePositions = arrayCube.getEdgePositionPermutation()
        //initializes corner positions
        cornerPositions = arrayCube.getCornerPositionPermutation()
        //initializes edge orientations
        for(i in 0 until 12) edgeOrientations[i] = SolvabilityUtils.getEdgeOrientation(edges[edgePositions[i]], arrayCube)
        //initializes corner orientations
        for(i in 0 until 8) cornerOrientations[i] = SolvabilityUtils.getCornerOrientation(corners[cornerPositions[i]])
    }

    override fun twist(twist: Twist): SmartCube {
        val newEP = getUpdatedEdgePositions(edgePositions, twist)
        val newCP = getUpdatedCornerPositions(cornerPositions, twist)
        val newEO = getUpdatedEdgeOrientations(edgeOrientations, edgePositions, twist)
        val newCO = getUpdatedCornerOrientations(cornerOrientations, cornerPositions, twist)
        return when(twist) {
            Twist.FRONT_90 -> SmartCube(twistFront90(data), newEP, newCP, newEO, newCO)
            Twist.FRONT_180 -> SmartCube(twistFront180(data), newEP, newCP, newEO, newCO)
            Twist.FRONT_270 -> SmartCube(twistFront270(data), newEP, newCP, newEO, newCO)
            Twist.BACK_90 -> SmartCube(twistBack90(data), newEP, newCP, newEO, newCO)
            Twist.BACK_180 -> SmartCube(twistBack180(data), newEP, newCP, newEO, newCO)
            Twist.BACK_270 -> SmartCube(twistBack270(data), newEP, newCP, newEO, newCO)
            Twist.LEFT_90 -> SmartCube(twistLeft90(data), newEP, newCP, newEO, newCO)
            Twist.LEFT_180 -> SmartCube(twistLeft180(data), newEP, newCP, newEO, newCO)
            Twist.LEFT_270 -> SmartCube(twistLeft270(data), newEP, newCP, newEO, newCO)
            Twist.RIGHT_90 -> SmartCube(twistRight90(data), newEP, newCP, newEO, newCO)
            Twist.RIGHT_180 -> SmartCube(twistRight180(data), newEP, newCP, newEO, newCO)
            Twist.RIGHT_270 -> SmartCube(twistRight270(data), newEP, newCP, newEO, newCO)
            Twist.UP_90 -> SmartCube(twistUp90(data), newEP, newCP, newEO, newCO)
            Twist.UP_180 -> SmartCube(twistUp180(data), newEP, newCP, newEO, newCO)
            Twist.UP_270 -> SmartCube(twistUp270(data), newEP, newCP, newEO, newCO)
            Twist.DOWN_90 -> SmartCube(twistDown90(data), newEP, newCP, newEO, newCO)
            Twist.DOWN_180 -> SmartCube(twistDown180(data), newEP, newCP, newEO, newCO)
            Twist.DOWN_270 -> SmartCube(twistDown270(data), newEP, newCP, newEO, newCO)
        }
    }

    override fun twistNoCopy(twist: Twist): SmartCube {
        //calculating edge orientations before positions is necessary here because orientations depend on old positions
        this.edgeOrientations = getUpdatedEdgeOrientations(edgeOrientations, edgePositions, twist)
        this.cornerOrientations = getUpdatedCornerOrientations(cornerOrientations, cornerPositions, twist)
        this.edgePositions = getUpdatedEdgePositions(edgePositions, twist)
        this.cornerPositions = getUpdatedCornerPositions(cornerPositions, twist)
        when(twist) {
            Twist.FRONT_90 -> { this.data = twistFront90(data); return this }
            Twist.FRONT_180 -> { this.data = twistFront180(data); return this }
            Twist.FRONT_270 -> { this.data = twistFront270(data); return this }
            Twist.BACK_90 -> { this.data = twistBack90(data); return this }
            Twist.BACK_180 -> { this.data = twistBack180(data); return this }
            Twist.BACK_270 -> { this.data = twistBack270(data); return this }
            Twist.LEFT_90 -> { this.data = twistLeft90(data); return this }
            Twist.LEFT_180 -> { this.data = twistLeft180(data); return this }
            Twist.LEFT_270 -> { this.data = twistLeft270(data); return this }
            Twist.RIGHT_90 -> { this.data = twistRight90(data); return this }
            Twist.RIGHT_180 -> { this.data = twistRight180(data); return this }
            Twist.RIGHT_270 -> { this.data = twistRight270(data); return this }
            Twist.UP_90 -> { this.data = twistUp90(data); return this }
            Twist.UP_180 -> { this.data = twistUp180(data); return this }
            Twist.UP_270 -> { this.data = twistUp270(data); return this }
            Twist.DOWN_90 -> { this.data = twistDown90(data); return this }
            Twist.DOWN_180 -> { this.data = twistDown180(data); return this }
            Twist.DOWN_270 -> { this.data = twistDown270(data); return this }
        }
    }

    override fun getTiles(): Array<IntArray> {
        return data
    }

    override fun getEdgePositionPermutation(): IntArray {
        return edgePositions
    }
    override fun getCornerPositionPermutation(): IntArray {
        return cornerPositions
    }
    override fun getEdgeOrientationPermutation(): IntArray {
        return edgeOrientations
    }
    override fun getCornerOrientationPermutation(): IntArray {
        return cornerOrientations
    }

    /** overridden equality to check whether the cubes have the same configuration */
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is SmartCube) return false
        if(data.contentDeepEquals(other.data) &&
           edgePositions.contentEquals(other.edgePositions) &&
           cornerPositions.contentEquals(other.cornerPositions) &&
           edgeOrientations.contentEquals(other.edgeOrientations) &&
           cornerOrientations.contentEquals(other.cornerOrientations)) return true
        return false
    }

    fun toArrayCube(): ArrayCube {
        return ArrayCube(data)
    }
    /** prints each face individually */
    override fun toString(): String {
        var retval = ""
        val faces = arrayOf("Front", "Back", "Left", "Right", "Up", "Down")
        //prints out each face
        for(i in 0 until 6) {
            retval += '\n' + faces[i] + " face:"
            for(j in 0 until 9) {
                if(j % 3 == 0) retval += '\n'
                retval += data[i][j]
                retval += ' '
            }
        }
        return retval
    }

    override fun hashCode(): Int {
        var result = data.contentDeepHashCode()
        result = 31 * result + edgeOrientations.contentHashCode()
        result = 31 * result + cornerOrientations.contentHashCode()
        result = 31 * result + edgePositions.contentHashCode()
        result = 31 * result + cornerPositions.contentHashCode()
        return result
    }

}

private fun twistFront90(data: Array<IntArray>) : Array<IntArray> {
    val copy = data.copy()
    //left face update
    copy[2][8] = data[5][2]
    copy[2][5] = data[5][1]
    copy[2][2] = data[5][0]
    //up face update
    copy[4][6] = data[2][8]
    copy[4][7] = data[2][5]
    copy[4][8] = data[2][2]
    //right face update
    copy[3][0] = data[4][6]
    copy[3][3] = data[4][7]
    copy[3][6] = data[4][8]
    //down face update
    copy[5][2] = data[3][0]
    copy[5][1] = data[3][3]
    copy[5][0] = data[3][6]
    //front face update
    copy[0][0] = data[0][6]
    copy[0][1] = data[0][3]
    copy[0][2] = data[0][0]
    copy[0][3] = data[0][7]
    copy[0][5] = data[0][1]
    copy[0][8] = data[0][2]
    copy[0][7] = data[0][5]
    copy[0][6] = data[0][8]
    //return the updated array
    return copy
}
private fun twistFront180(data: Array<IntArray>) : Array<IntArray> {
    val copy = data.copy()
    //left face update
    copy[2][8] = data[3][0]
    copy[2][5] = data[3][3]
    copy[2][2] = data[3][6]
    //up face update
    copy[4][6] = data[5][2]
    copy[4][7] = data[5][1]
    copy[4][8] = data[5][0]
    //right face update
    copy[3][0] = data[2][8]
    copy[3][3] = data[2][5]
    copy[3][6] = data[2][2]
    //down face update
    copy[5][2] = data[4][6]
    copy[5][1] = data[4][7]
    copy[5][0] = data[4][8]
    //front face update
    copy[0][0] = data[0][8]
    copy[0][1] = data[0][7]
    copy[0][2] = data[0][6]
    copy[0][3] = data[0][5]
    copy[0][5] = data[0][3]
    copy[0][8] = data[0][0]
    copy[0][7] = data[0][1]
    copy[0][6] = data[0][2]
    //return the updated array
    return copy
}
private fun twistFront270(data: Array<IntArray>) : Array<IntArray> {
    val copy = data.copy()
    //left face update
    copy[2][8] = data[4][6]
    copy[2][5] = data[4][7]
    copy[2][2] = data[4][8]
    //up face update
    copy[4][6] = data[3][0]
    copy[4][7] = data[3][3]
    copy[4][8] = data[3][6]
    //right face update
    copy[3][0] = data[5][2]
    copy[3][3] = data[5][1]
    copy[3][6] = data[5][0]
    //down face update
    copy[5][2] = data[2][8]
    copy[5][1] = data[2][5]
    copy[5][0] = data[2][2]
    //front face update
    copy[0][0] = data[0][2]
    copy[0][1] = data[0][5]
    copy[0][2] = data[0][8]
    copy[0][3] = data[0][1]
    copy[0][5] = data[0][7]
    copy[0][8] = data[0][6]
    copy[0][7] = data[0][3]
    copy[0][6] = data[0][0]
    //return the updated array
    return copy
}
private fun twistBack90(data: Array<IntArray>) : Array<IntArray> {
    val copy = data.copy()
    //right face update
    copy[3][2] = data[5][8]
    copy[3][5] = data[5][7]
    copy[3][8] = data[5][6]
    //up face update
    copy[4][0] = data[3][2]
    copy[4][1] = data[3][5]
    copy[4][2] = data[3][8]
    //left face update
    copy[2][6] = data[4][0]
    copy[2][3] = data[4][1]
    copy[2][0] = data[4][2]
    //down face update
    copy[5][8] = data[2][6]
    copy[5][7] = data[2][3]
    copy[5][6] = data[2][0]
    //back face update
    copy[1][0] = data[1][6]
    copy[1][1] = data[1][3]
    copy[1][2] = data[1][0]
    copy[1][3] = data[1][7]
    copy[1][5] = data[1][1]
    copy[1][8] = data[1][2]
    copy[1][7] = data[1][5]
    copy[1][6] = data[1][8]
    //return the updated array
    return copy
}
private fun twistBack180(data: Array<IntArray>) : Array<IntArray> {
    val copy = data.copy()
    //right face update
    copy[3][2] = data[2][6]
    copy[3][5] = data[2][3]
    copy[3][8] = data[2][0]
    //up face update
    copy[4][0] = data[5][8]
    copy[4][1] = data[5][7]
    copy[4][2] = data[5][6]
    //left face update
    copy[2][6] = data[3][2]
    copy[2][3] = data[3][5]
    copy[2][0] = data[3][8]
    //down face update
    copy[5][8] = data[4][0]
    copy[5][7] = data[4][1]
    copy[5][6] = data[4][2]
    //back face update
    copy[1][0] = data[1][8]
    copy[1][1] = data[1][7]
    copy[1][2] = data[1][6]
    copy[1][3] = data[1][5]
    copy[1][5] = data[1][3]
    copy[1][8] = data[1][0]
    copy[1][7] = data[1][1]
    copy[1][6] = data[1][2]
    //return the updated array
    return copy
}
private fun twistBack270(data: Array<IntArray>) : Array<IntArray> {
    val copy = data.copy()
    //right face update
    copy[3][2] = data[4][0]
    copy[3][5] = data[4][1]
    copy[3][8] = data[4][2]
    //up face update
    copy[4][0] = data[2][6]
    copy[4][1] = data[2][3]
    copy[4][2] = data[2][0]
    //left face update
    copy[2][6] = data[5][8]
    copy[2][3] = data[5][7]
    copy[2][0] = data[5][6]
    //down face update
    copy[5][8] = data[3][2]
    copy[5][7] = data[3][5]
    copy[5][6] = data[3][8]
    //back face update
    copy[1][0] = data[1][2]
    copy[1][1] = data[1][5]
    copy[1][2] = data[1][8]
    copy[1][3] = data[1][1]
    copy[1][5] = data[1][7]
    copy[1][8] = data[1][6]
    copy[1][7] = data[1][3]
    copy[1][6] = data[1][0]
    //return the updated array
    return copy
}
private fun twistLeft90(data: Array<IntArray>) : Array<IntArray> {
    val copy = data.copy()
    //back face update
    copy[1][8] = data[5][0]
    copy[1][5] = data[5][3]
    copy[1][2] = data[5][6]
    //up face update
    copy[4][0] = data[1][8]
    copy[4][3] = data[1][5]
    copy[4][6] = data[1][2]
    //front face update
    copy[0][0] = data[4][0]
    copy[0][3] = data[4][3]
    copy[0][6] = data[4][6]
    //down face update
    copy[5][0] = data[0][0]
    copy[5][3] = data[0][3]
    copy[5][6] = data[0][6]
    //left face update
    copy[2][0] = data[2][6]
    copy[2][1] = data[2][3]
    copy[2][2] = data[2][0]
    copy[2][3] = data[2][7]
    copy[2][5] = data[2][1]
    copy[2][8] = data[2][2]
    copy[2][7] = data[2][5]
    copy[2][6] = data[2][8]
    //return the updated array
    return copy
}
private fun twistLeft180(data: Array<IntArray>) : Array<IntArray> {
    val copy = data.copy()
    //back face update
    copy[1][8] = data[0][0]
    copy[1][5] = data[0][3]
    copy[1][2] = data[0][6]
    //up face update
    copy[4][0] = data[5][0]
    copy[4][3] = data[5][3]
    copy[4][6] = data[5][6]
    //front face update
    copy[0][0] = data[1][8]
    copy[0][3] = data[1][5]
    copy[0][6] = data[1][2]
    //down face update
    copy[5][0] = data[4][0]
    copy[5][3] = data[4][3]
    copy[5][6] = data[4][6]
    //left face update
    copy[2][0] = data[2][8]
    copy[2][1] = data[2][7]
    copy[2][2] = data[2][6]
    copy[2][3] = data[2][5]
    copy[2][5] = data[2][3]
    copy[2][8] = data[2][0]
    copy[2][7] = data[2][1]
    copy[2][6] = data[2][2]
    //return the updated array
    return copy
}
private fun twistLeft270(data: Array<IntArray>) : Array<IntArray> {
    val copy = data.copy()
    //back face update
    copy[1][8] = data[4][0]
    copy[1][5] = data[4][3]
    copy[1][2] = data[4][6]
    //up face update
    copy[4][0] = data[0][0]
    copy[4][3] = data[0][3]
    copy[4][6] = data[0][6]
    //front face update
    copy[0][0] = data[5][0]
    copy[0][3] = data[5][3]
    copy[0][6] = data[5][6]
    //down face update
    copy[5][0] = data[1][8]
    copy[5][3] = data[1][5]
    copy[5][6] = data[1][2]
    //left face update
    copy[2][0] = data[2][2]
    copy[2][1] = data[2][5]
    copy[2][2] = data[2][8]
    copy[2][3] = data[2][1]
    copy[2][5] = data[2][7]
    copy[2][8] = data[2][6]
    copy[2][7] = data[2][3]
    copy[2][6] = data[2][0]
    return copy
}
private fun twistRight90(data: Array<IntArray>) : Array<IntArray> {
    val copy = data.copy()
    //front face update
    copy[0][8] = data[5][8]
    copy[0][5] = data[5][5]
    copy[0][2] = data[5][2]
    //up face update
    copy[4][8] = data[0][8]
    copy[4][5] = data[0][5]
    copy[4][2] = data[0][2]
    //back face update
    copy[1][0] = data[4][8]
    copy[1][3] = data[4][5]
    copy[1][6] = data[4][2]
    //down face update
    copy[5][8] = data[1][0]
    copy[5][5] = data[1][3]
    copy[5][2] = data[1][6]
    //right face update
    copy[3][0] = data[3][6]
    copy[3][1] = data[3][3]
    copy[3][2] = data[3][0]
    copy[3][3] = data[3][7]
    copy[3][5] = data[3][1]
    copy[3][8] = data[3][2]
    copy[3][7] = data[3][5]
    copy[3][6] = data[3][8]
    //return the updated array
    return copy
}
private fun twistRight180(data: Array<IntArray>) : Array<IntArray> {
    val copy = data.copy()
    //front face update
    copy[0][8] = data[1][0]
    copy[0][5] = data[1][3]
    copy[0][2] = data[1][6]
    //up face update
    copy[4][8] = data[5][8]
    copy[4][5] = data[5][5]
    copy[4][2] = data[5][2]
    //back face update
    copy[1][0] = data[0][8]
    copy[1][3] = data[0][5]
    copy[1][6] = data[0][2]
    //down face update
    copy[5][8] = data[4][8]
    copy[5][5] = data[4][5]
    copy[5][2] = data[4][2]
    //right face update
    copy[3][0] = data[3][8]
    copy[3][1] = data[3][7]
    copy[3][2] = data[3][6]
    copy[3][3] = data[3][5]
    copy[3][5] = data[3][3]
    copy[3][8] = data[3][0]
    copy[3][7] = data[3][1]
    copy[3][6] = data[3][2]
    //return the updated array
    return copy
}
private fun twistRight270(data: Array<IntArray>) : Array<IntArray> {
    val copy = data.copy()
    //front face update
    copy[0][8] = data[4][8]
    copy[0][5] = data[4][5]
    copy[0][2] = data[4][2]
    //up face update
    copy[4][8] = data[1][0]
    copy[4][5] = data[1][3]
    copy[4][2] = data[1][6]
    //back face update
    copy[1][0] = data[5][8]
    copy[1][3] = data[5][5]
    copy[1][6] = data[5][2]
    //down face update
    copy[5][8] = data[0][8]
    copy[5][5] = data[0][5]
    copy[5][2] = data[0][2]
    //right face update
    copy[3][0] = data[3][2]
    copy[3][1] = data[3][5]
    copy[3][2] = data[3][8]
    copy[3][3] = data[3][1]
    copy[3][5] = data[3][7]
    copy[3][8] = data[3][6]
    copy[3][7] = data[3][3]
    copy[3][6] = data[3][0]
    //return the updated array
    return copy
}
private fun twistUp90(data: Array<IntArray>) : Array<IntArray> {
    val copy = data.copy()
    //left face update
    copy[2][2] = data[0][2]
    copy[2][1] = data[0][1]
    copy[2][0] = data[0][0]
    //back face update
    copy[1][2] = data[2][2]
    copy[1][1] = data[2][1]
    copy[1][0] = data[2][0]
    //right face update
    copy[3][2] = data[1][2]
    copy[3][1] = data[1][1]
    copy[3][0] = data[1][0]
    //front face update
    copy[0][2] = data[3][2]
    copy[0][1] = data[3][1]
    copy[0][0] = data[3][0]
    //up face update
    copy[4][0] = data[4][6]
    copy[4][1] = data[4][3]
    copy[4][2] = data[4][0]
    copy[4][3] = data[4][7]
    copy[4][5] = data[4][1]
    copy[4][8] = data[4][2]
    copy[4][7] = data[4][5]
    copy[4][6] = data[4][8]
    //return the updated array
    return copy
}
private fun twistUp180(data: Array<IntArray>) : Array<IntArray> {
    val copy = data.copy()
    //left face update
    copy[2][2] = data[3][2]
    copy[2][1] = data[3][1]
    copy[2][0] = data[3][0]
    //back face update
    copy[1][2] = data[0][2]
    copy[1][1] = data[0][1]
    copy[1][0] = data[0][0]
    //right face update
    copy[3][2] = data[2][2]
    copy[3][1] = data[2][1]
    copy[3][0] = data[2][0]
    //front face update
    copy[0][2] = data[1][2]
    copy[0][1] = data[1][1]
    copy[0][0] = data[1][0]
    //up face update
    copy[4][0] = data[4][8]
    copy[4][1] = data[4][7]
    copy[4][2] = data[4][6]
    copy[4][3] = data[4][5]
    copy[4][5] = data[4][3]
    copy[4][8] = data[4][0]
    copy[4][7] = data[4][1]
    copy[4][6] = data[4][2]
    //return the updated array
    return copy
}
private fun twistUp270(data: Array<IntArray>) : Array<IntArray> {
    val copy = data.copy()
    //left face update
    copy[2][2] = data[1][2]
    copy[2][1] = data[1][1]
    copy[2][0] = data[1][0]
    //back face update
    copy[1][2] = data[3][2]
    copy[1][1] = data[3][1]
    copy[1][0] = data[3][0]
    //right face update
    copy[3][2] = data[0][2]
    copy[3][1] = data[0][1]
    copy[3][0] = data[0][0]
    //front face update
    copy[0][2] = data[2][2]
    copy[0][1] = data[2][1]
    copy[0][0] = data[2][0]
    //up face update
    copy[4][0] = data[4][2]
    copy[4][1] = data[4][5]
    copy[4][2] = data[4][8]
    copy[4][3] = data[4][1]
    copy[4][5] = data[4][7]
    copy[4][8] = data[4][6]
    copy[4][7] = data[4][3]
    copy[4][6] = data[4][0]
    //return the updated array
    return copy
}
private fun twistDown90(data: Array<IntArray>) : Array<IntArray> {
    val copy = data.copy()
    //left face update
    copy[2][6] = data[1][6]
    copy[2][7] = data[1][7]
    copy[2][8] = data[1][8]
    //front face update
    copy[0][6] = data[2][6]
    copy[0][7] = data[2][7]
    copy[0][8] = data[2][8]
    //right face update
    copy[3][6] = data[0][6]
    copy[3][7] = data[0][7]
    copy[3][8] = data[0][8]
    //back face update
    copy[1][6] = data[3][6]
    copy[1][7] = data[3][7]
    copy[1][8] = data[3][8]
    //down face update
    copy[5][0] = data[5][6]
    copy[5][1] = data[5][3]
    copy[5][2] = data[5][0]
    copy[5][3] = data[5][7]
    copy[5][5] = data[5][1]
    copy[5][8] = data[5][2]
    copy[5][7] = data[5][5]
    copy[5][6] = data[5][8]
    //return the updated array
    return copy
}
private fun twistDown180(data: Array<IntArray>) : Array<IntArray> {
    val copy = data.copy()
    //left face update
    copy[2][6] = data[3][6]
    copy[2][7] = data[3][7]
    copy[2][8] = data[3][8]
    //front face update
    copy[0][6] = data[1][6]
    copy[0][7] = data[1][7]
    copy[0][8] = data[1][8]
    //right face update
    copy[3][6] = data[2][6]
    copy[3][7] = data[2][7]
    copy[3][8] = data[2][8]
    //back face update
    copy[1][6] = data[0][6]
    copy[1][7] = data[0][7]
    copy[1][8] = data[0][8]
    //down face update
    copy[5][0] = data[5][8]
    copy[5][1] = data[5][7]
    copy[5][2] = data[5][6]
    copy[5][3] = data[5][5]
    copy[5][5] = data[5][3]
    copy[5][8] = data[5][0]
    copy[5][7] = data[5][1]
    copy[5][6] = data[5][2]
    //return the updated array
    return copy
}
private fun twistDown270(data: Array<IntArray>) : Array<IntArray> {
    val copy = data.copy()
    //left face update
    copy[2][6] = data[0][6]
    copy[2][7] = data[0][7]
    copy[2][8] = data[0][8]
    //front face update
    copy[0][6] = data[3][6]
    copy[0][7] = data[3][7]
    copy[0][8] = data[3][8]
    //right face update
    copy[3][6] = data[1][6]
    copy[3][7] = data[1][7]
    copy[3][8] = data[1][8]
    //back face update
    copy[1][6] = data[2][6]
    copy[1][7] = data[2][7]
    copy[1][8] = data[2][8]
    //down face update
    copy[5][0] = data[5][2]
    copy[5][1] = data[5][5]
    copy[5][2] = data[5][8]
    copy[5][3] = data[5][1]
    copy[5][5] = data[5][7]
    copy[5][8] = data[5][6]
    copy[5][7] = data[5][3]
    copy[5][6] = data[5][0]
    //return the updated array
    return copy
}

/*
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
private fun getUpdatedEdgeOrientations(oldOrientations: IntArray, oldPositions: IntArray, twist: Twist): IntArray {
    val orientations = oldOrientations.copyOf()
    val numQuarterTurns = Twist.getEquivalentNumClockwiseQuarterTurns(twist)
    //gets edge indices
    val o5Index = getO5Index(oldPositions, twist)
    val o6Index = getO6Index(oldPositions, twist)
    val o7Index = getO7Index(oldPositions, twist)
    val o8Index = getO8Index(oldPositions, twist)
    //computes new orientation values
    val orientationIncrement = twistChangesEdgeOrientations(twist)
    for(i in 0 until numQuarterTurns) {
        orientations[o5Index] = (orientations[o5Index] + orientationIncrement) % 2
        orientations[o6Index] = (orientations[o6Index] + orientationIncrement) % 2
        orientations[o7Index] = (orientations[o7Index] + orientationIncrement) % 2
        orientations[o8Index] = (orientations[o8Index] + orientationIncrement) % 2
    }
    //returns
    return orientations
}
/*
 * The corner orientation group is closed under arithmetic modulo 3 since each corner falls on 3 axes.
 * Let's define a corner's orientation as the number of clockwise rotations it takes to orient its up- or down-colored tile up or down.
 *
 * Let O1 be the corner originally in the top left of the face, O2 in the top right, O3 in the bottom right, O4 in the bottom left.
 * When we twist this face clockwise 90 degrees,
 * O1 becomes N2 and orientation increases by 2.
 * O2 becomes N3 and orientation increases by 1.
 * O3 becomes N4 and orientation increases by 2.
 * O4 becomes N1 and orientation increases by 1.
 * If this face is UP or DOWN, all orientations remain the same.
 */
private fun getUpdatedCornerOrientations(oldOrientations: IntArray, oldPositions: IntArray, twist: Twist): IntArray {
    val orientations = oldOrientations.copyOf()
    val numQuarterTurns = Twist.getEquivalentNumClockwiseQuarterTurns(twist)
    //gets corner indices
    val o1Index = getO1Index(oldPositions, twist)
    val o2Index = getO2Index(oldPositions, twist)
    val o3Index = getO3Index(oldPositions, twist)
    val o4Index = getO4Index(oldPositions, twist)
    //computes new orientation values
    val orientationIncrement = twistChangesCornerOrientations(twist)
    if(numQuarterTurns != 2) {
        orientations[o1Index] = (orientations[o1Index] + (2 * orientationIncrement)) % 3
        orientations[o2Index] = (orientations[o2Index] + (1 * orientationIncrement)) % 3
        orientations[o3Index] = (orientations[o3Index] + (2 * orientationIncrement)) % 3
        orientations[o4Index] = (orientations[o4Index] + (1 * orientationIncrement)) % 3
    }
    //returns
    return orientations
    //TODO: this is only for 90-degree turns. Fix.
}
private fun getUpdatedEdgePositions(oldPositions: IntArray, twist: Twist): IntArray {
    val positions = oldPositions.copyOf()
    /*
     * up-front, up-back, up-left, up-right,
     * down-front, down-back, down-left, down-right,
     * front-left, front-right, back-left, back-right
     */
    when(Twist.getFace(twist)) {
        Twist.Face.FRONT -> {
            //grabs the indices of the cubies currently in the front edge positions
            val frontUpIndex = oldPositions.indexOf(0)
            val frontRightIndex = oldPositions.indexOf(9)
            val frontDownIndex = oldPositions.indexOf(4)
            val frontLeftIndex = oldPositions.indexOf(8)
            //updates the positions of these cubies
            when(twist) {
                Twist.FRONT_90 -> {
                    positions[frontUpIndex] = 9
                    positions[frontRightIndex] = 4
                    positions[frontDownIndex] = 8
                    positions[frontLeftIndex] = 0
                }
                Twist.FRONT_180 -> {
                    positions[frontUpIndex] = 4
                    positions[frontRightIndex] = 8
                    positions[frontDownIndex] = 0
                    positions[frontLeftIndex] = 9
                }
                else -> {
                    positions[frontUpIndex] = 8
                    positions[frontRightIndex] = 0
                    positions[frontDownIndex] = 9
                    positions[frontLeftIndex] = 4
                }
            }
        }
        Twist.Face.BACK -> {
            //grabs the indices of the cubies currently in the back edge positions
            val backUpIndex = oldPositions.indexOf(1)
            val backLeftIndex = oldPositions.indexOf(10)
            val backDownIndex = oldPositions.indexOf(5)
            val backRightIndex = oldPositions.indexOf(11)
            //updates the positions of these cubies
            when(twist) {
                Twist.BACK_90 -> {
                    positions[backUpIndex] = 10
                    positions[backLeftIndex] = 5
                    positions[backDownIndex] = 11
                    positions[backRightIndex] = 1
                }
                Twist.BACK_180 -> {
                    positions[backUpIndex] = 5
                    positions[backLeftIndex] = 11
                    positions[backDownIndex] = 1
                    positions[backRightIndex] = 10
                }
                else -> {
                    positions[backUpIndex] = 11
                    positions[backLeftIndex] = 1
                    positions[backDownIndex] = 10
                    positions[backRightIndex] = 5
                }
            }
        }
        Twist.Face.LEFT -> {
            //grabs the indices of the cubies currently in the left edge positions
            val leftUpIndex = oldPositions.indexOf(2)
            val leftFrontIndex = oldPositions.indexOf(8)
            val leftDownIndex = oldPositions.indexOf(6)
            val leftBackIndex = oldPositions.indexOf(10)
            //updates the positions of these cubies
            when(twist) {
                Twist.LEFT_90 -> {
                    positions[leftUpIndex] = 8
                    positions[leftFrontIndex] = 6
                    positions[leftDownIndex] = 10
                    positions[leftBackIndex] = 2
                }
                Twist.LEFT_180 -> {
                    positions[leftUpIndex] = 6
                    positions[leftFrontIndex] = 10
                    positions[leftDownIndex] = 2
                    positions[leftBackIndex] = 8
                }
                else -> {
                    positions[leftUpIndex] = 10
                    positions[leftFrontIndex] = 2
                    positions[leftDownIndex] = 8
                    positions[leftBackIndex] = 6
                }
            }
        }
        Twist.Face.RIGHT -> {
            //grabs the indices of the cubies currently in the right edge positions
            val rightUpIndex = oldPositions.indexOf(3)
            val rightBackIndex = oldPositions.indexOf(11)
            val rightDownIndex = oldPositions.indexOf(7)
            val rightFrontIndex = oldPositions.indexOf(9)
            //updates the positions of these cubies
            when(twist) {
                Twist.RIGHT_90 -> {
                    positions[rightUpIndex] = 11
                    positions[rightBackIndex] = 7
                    positions[rightDownIndex] = 9
                    positions[rightFrontIndex] = 3
                }
                Twist.RIGHT_180 -> {
                    positions[rightUpIndex] = 7
                    positions[rightBackIndex] = 9
                    positions[rightDownIndex] = 3
                    positions[rightFrontIndex] = 11
                }
                else -> {
                    positions[rightUpIndex] = 9
                    positions[rightBackIndex] = 3
                    positions[rightDownIndex] = 11
                    positions[rightFrontIndex] = 7
                }
            }
        }
        Twist.Face.UP -> {
            //grabs the indices of the cubies currently in the up edge positions
            val upBackIndex = oldPositions.indexOf(1)
            val upRightIndex = oldPositions.indexOf(3)
            val upFrontIndex = oldPositions.indexOf(0)
            val upLeftIndex = oldPositions.indexOf(2)
            //updates the positions of these cubies
            when(twist) {
                Twist.UP_90 -> {
                    positions[upBackIndex] = 3
                    positions[upRightIndex] = 0
                    positions[upFrontIndex] = 2
                    positions[upLeftIndex] = 1
                }
                Twist.UP_180 -> {
                    positions[upBackIndex] = 0
                    positions[upRightIndex] = 2
                    positions[upFrontIndex] = 1
                    positions[upLeftIndex] = 3
                }
                else -> {
                    positions[upBackIndex] = 2
                    positions[upRightIndex] = 1
                    positions[upFrontIndex] = 3
                    positions[upLeftIndex] = 0
                }
            }
        }
        Twist.Face.DOWN -> {
            //grabs the indices of the cubies currently in the down edge positions
            val downFrontIndex = oldPositions.indexOf(4)
            val downRightIndex = oldPositions.indexOf(7)
            val downBackIndex = oldPositions.indexOf(5)
            val downLeftIndex = oldPositions.indexOf(6)
            //updates the positions of these cubies
            when(twist) {
                Twist.DOWN_90 -> {
                    positions[downFrontIndex] = 7
                    positions[downRightIndex] = 5
                    positions[downBackIndex] = 6
                    positions[downLeftIndex] = 4
                }
                Twist.DOWN_180 -> {
                    positions[downFrontIndex] = 5
                    positions[downRightIndex] = 6
                    positions[downBackIndex] = 4
                    positions[downLeftIndex] = 7
                }
                else -> {
                    positions[downFrontIndex] = 6
                    positions[downRightIndex] = 4
                    positions[downBackIndex] = 7
                    positions[downLeftIndex] = 5
                }
            }
        }
    }
    return positions
}
private fun getUpdatedCornerPositions(oldPositions: IntArray, twist: Twist): IntArray {
    val positions = oldPositions.copyOf()
    /*
     * up-front-left, up-front-right, up-back-left, up-back-right,
     * down-front-left, down-front-right, down-back-left, down-back-right
     */
    when(Twist.getFace(twist)) {
        Twist.Face.FRONT -> {
            //grabs the indices of the cubies currently in the front corner positions
            val frontUpLeftIndex = oldPositions.indexOf(0)
            val frontUpRightIndex = oldPositions.indexOf(1)
            val frontDownRightIndex = oldPositions.indexOf(5)
            val frontDownLeftIndex = oldPositions.indexOf(4)
            //updates the positions of these cubies
            when(twist) {
                Twist.FRONT_90 -> {
                    positions[frontUpLeftIndex] = 1
                    positions[frontUpRightIndex] = 5
                    positions[frontDownRightIndex] = 4
                    positions[frontDownLeftIndex] = 0
                }
                Twist.FRONT_180 -> {
                    positions[frontUpLeftIndex] = 5
                    positions[frontUpRightIndex] = 4
                    positions[frontDownRightIndex] = 0
                    positions[frontDownLeftIndex] = 1
                }
                else -> {
                    positions[frontUpLeftIndex] = 4
                    positions[frontUpRightIndex] = 0
                    positions[frontDownRightIndex] = 1
                    positions[frontDownLeftIndex] = 5
                }
            }
        }
        Twist.Face.BACK -> {
            //grabs the indices of the cubies currently in the back edge positions
            val backUpRightIndex = oldPositions.indexOf(3)
            val backUpLeftIndex = oldPositions.indexOf(2)
            val backDownLeftIndex = oldPositions.indexOf(6)
            val backDownRightIndex = oldPositions.indexOf(7)
            //updates the positions of these cubies
            when(twist) {
                Twist.BACK_90 -> {
                    positions[backUpRightIndex] = 2
                    positions[backUpLeftIndex] = 6
                    positions[backDownLeftIndex] = 7
                    positions[backDownRightIndex] = 3
                }
                Twist.BACK_180 -> {
                    positions[backUpRightIndex] = 6
                    positions[backUpLeftIndex] = 7
                    positions[backDownLeftIndex] = 3
                    positions[backDownRightIndex] = 2
                }
                else -> {
                    positions[backUpRightIndex] = 7
                    positions[backUpLeftIndex] = 3
                    positions[backDownLeftIndex] = 2
                    positions[backDownRightIndex] = 6
                }
            }
        }
        Twist.Face.LEFT -> {
            //grabs the indices of the cubies currently in the left edge positions
            val leftUpBackIndex = oldPositions.indexOf(2)
            val leftUpFrontIndex = oldPositions.indexOf(0)
            val leftDownFrontIndex = oldPositions.indexOf(4)
            val leftDownBackIndex = oldPositions.indexOf(6)
            //updates the positions of these cubies
            when(twist) {
                Twist.LEFT_90 -> {
                    positions[leftUpBackIndex] = 0
                    positions[leftUpFrontIndex] = 4
                    positions[leftDownFrontIndex] = 6
                    positions[leftDownBackIndex] = 2
                }
                Twist.LEFT_180 -> {
                    positions[leftUpBackIndex] = 4
                    positions[leftUpFrontIndex] = 6
                    positions[leftDownFrontIndex] = 2
                    positions[leftDownBackIndex] = 0
                }
                else -> {
                    positions[leftUpBackIndex] = 6
                    positions[leftUpFrontIndex] = 2
                    positions[leftDownFrontIndex] = 0
                    positions[leftDownBackIndex] = 4
                }
            }
        }
        Twist.Face.RIGHT -> {
            //grabs the indices of the cubies currently in the right edge positions
            val rightUpFrontIndex = oldPositions.indexOf(1)
            val rightUpBackIndex = oldPositions.indexOf(3)
            val rightDownBackIndex = oldPositions.indexOf(7)
            val rightDownFrontIndex = oldPositions.indexOf(5)
            //updates the positions of these cubies
            when(twist) {
                Twist.RIGHT_90 -> {
                    positions[rightUpFrontIndex] = 3
                    positions[rightUpBackIndex] = 7
                    positions[rightDownBackIndex] = 5
                    positions[rightDownFrontIndex] = 1
                }
                Twist.RIGHT_180 -> {
                    positions[rightUpFrontIndex] = 7
                    positions[rightUpBackIndex] = 5
                    positions[rightDownBackIndex] = 1
                    positions[rightDownFrontIndex] = 3
                }
                else -> {
                    positions[rightUpFrontIndex] = 5
                    positions[rightUpBackIndex] = 1
                    positions[rightDownBackIndex] = 3
                    positions[rightDownFrontIndex] = 7
                }
            }
        }
        Twist.Face.UP -> {
            //grabs the indices of the cubies currently in the up edge positions
            val upBackLeftIndex = oldPositions.indexOf(2)
            val upBackRightIndex = oldPositions.indexOf(3)
            val upFrontRightIndex = oldPositions.indexOf(1)
            val upFrontLeftIndex = oldPositions.indexOf(0)
            //updates the positions of these cubies
            when(twist) {
                Twist.UP_90 -> {
                    positions[upBackLeftIndex] = 3
                    positions[upBackRightIndex] = 1
                    positions[upFrontRightIndex] = 0
                    positions[upFrontLeftIndex] = 2
                }
                Twist.UP_180 -> {
                    positions[upBackLeftIndex] = 1
                    positions[upBackRightIndex] = 0
                    positions[upFrontRightIndex] = 2
                    positions[upFrontLeftIndex] = 3
                }
                else -> {
                    positions[upBackLeftIndex] = 0
                    positions[upBackRightIndex] = 2
                    positions[upFrontRightIndex] = 3
                    positions[upFrontLeftIndex] = 1
                }
            }
        }
        Twist.Face.DOWN -> {
            //grabs the indices of the cubies currently in the down edge positions
            val downFrontLeftIndex = oldPositions.indexOf(4)
            val downFrontRightIndex = oldPositions.indexOf(5)
            val downBackRightIndex = oldPositions.indexOf(7)
            val downBackLeftIndex = oldPositions.indexOf(6)
            //updates the positions of these cubies
            when(twist) {
                Twist.DOWN_90 -> {
                    positions[downFrontLeftIndex] = 5
                    positions[downFrontRightIndex] = 7
                    positions[downBackRightIndex] = 6
                    positions[downBackLeftIndex] = 4
                }
                Twist.DOWN_180 -> {
                    positions[downFrontLeftIndex] = 7
                    positions[downFrontRightIndex] = 6
                    positions[downBackRightIndex] = 4
                    positions[downBackLeftIndex] = 5
                }
                else -> {
                    positions[downFrontLeftIndex] = 6
                    positions[downFrontRightIndex] = 4
                    positions[downBackRightIndex] = 5
                    positions[downBackLeftIndex] = 7
                }
            }
        }
    }
    return positions
}
/** Gets whether this twist changes edge orientation values (0 or 1) */
private fun twistChangesEdgeOrientations(twist: Twist): Int {
    return if(Twist.getFace(twist) == Twist.Face.FRONT || Twist.getFace(twist) == Twist.Face.BACK) 1 else 0
}
/** Gets whether this twist changes corner orientation values or not (0 or 1) */
private fun twistChangesCornerOrientations(twist: Twist): Int {
    return if(Twist.getFace(twist) == Twist.Face.UP || Twist.getFace(twist) == Twist.Face.DOWN) 0 else 1
}
private fun getO1Index(positions: IntArray, twist: Twist): Int {
    return when(Twist.getFace(twist)) {
        Twist.Face.FRONT -> positions.indexOf(0) //up-front-left
        Twist.Face.BACK -> positions.indexOf(3) //up-back-right
        Twist.Face.LEFT -> positions.indexOf(2) //up-back-left
        Twist.Face.RIGHT -> positions.indexOf(1) //up-front-right
        Twist.Face.UP -> positions.indexOf(2) //up-back-left
        Twist.Face.DOWN -> positions.indexOf(4) //down-front-left
    }
}
private fun getO2Index(positions: IntArray, twist: Twist): Int {
    return when(Twist.getFace(twist)) {
        Twist.Face.FRONT -> positions.indexOf(1) //up-front-right
        Twist.Face.BACK -> positions.indexOf(2) // up-back-left
        Twist.Face.LEFT -> positions.indexOf(0) // up-front-left
        Twist.Face.RIGHT -> positions.indexOf(3) //up-back-right
        Twist.Face.UP -> positions.indexOf(3) //up-back-right
        Twist.Face.DOWN -> positions.indexOf(5) //down-front-right
    }
}
private fun getO3Index(positions: IntArray, twist: Twist): Int {
    return when(Twist.getFace(twist)) {
        Twist.Face.FRONT -> positions.indexOf(5) //down-front-right
        Twist.Face.BACK -> positions.indexOf(6) //down-back-left
        Twist.Face.LEFT -> positions.indexOf(4) //down-front-left
        Twist.Face.RIGHT -> positions.indexOf(7) //down-back-right
        Twist.Face.UP -> positions.indexOf(1) //up-front-right
        Twist.Face.DOWN -> positions.indexOf(7) //down-back-right
    }
}
private fun getO4Index(positions: IntArray, twist: Twist): Int {
    return when(Twist.getFace(twist)) {
        Twist.Face.FRONT -> positions.indexOf(4) //down-front-left
        Twist.Face.BACK -> positions.indexOf(7) //down-back-right
        Twist.Face.LEFT -> positions.indexOf(6) //down-back-left
        Twist.Face.RIGHT -> positions.indexOf(5) //down-front-right
        Twist.Face.UP -> positions.indexOf(0) //up-front-left
        Twist.Face.DOWN -> positions.indexOf(6) //down-back-left
    }
}
private fun getO5Index(positions: IntArray, twist: Twist): Int {
    return when(Twist.getFace(twist)) {
        Twist.Face.FRONT -> positions.indexOf(0) //up-front
        Twist.Face.BACK -> positions.indexOf(1) //up-back
        Twist.Face.LEFT -> positions.indexOf(2) //up-left
        Twist.Face.RIGHT -> positions.indexOf(3) //up-right
        Twist.Face.UP -> positions.indexOf(1) //up-back
        Twist.Face.DOWN -> positions.indexOf(4) //down-front
    }
}
private fun getO6Index(positions: IntArray, twist: Twist): Int {
    return when(Twist.getFace(twist)) {
        Twist.Face.FRONT -> positions.indexOf(9) //front-right
        Twist.Face.BACK ->  positions.indexOf(10) //back-left
        Twist.Face.LEFT -> positions.indexOf(8) //front-left
        Twist.Face.RIGHT ->  positions.indexOf(11) //back-right
        Twist.Face.UP -> positions.indexOf(3) //up-right
        Twist.Face.DOWN -> positions.indexOf(7) //down-right
    }
}
private fun getO7Index(positions: IntArray, twist: Twist): Int {
    return when(Twist.getFace(twist)) {
        Twist.Face.FRONT -> positions.indexOf(4) //down-front
        Twist.Face.BACK -> positions.indexOf(5) //down-back
        Twist.Face.LEFT -> positions.indexOf(6) //down-left
        Twist.Face.RIGHT -> positions.indexOf(7) //down-right
        Twist.Face.UP -> positions.indexOf(0) //up-front
        Twist.Face.DOWN -> positions.indexOf(5) //down-back
    }
}
private fun getO8Index(positions: IntArray, twist: Twist): Int {
    return when(Twist.getFace(twist)) {
        Twist.Face.FRONT -> positions.indexOf(8) //front-left
        Twist.Face.BACK ->  positions.indexOf(11) //back-right
        Twist.Face.LEFT ->  positions.indexOf(10) //back-left
        Twist.Face.RIGHT -> positions.indexOf(9) //front-right
        Twist.Face.UP -> positions.indexOf(2) //up-left
        Twist.Face.DOWN -> positions.indexOf(6) //down-left
    }
}