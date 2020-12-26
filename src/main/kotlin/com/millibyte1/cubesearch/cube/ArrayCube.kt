package com.millibyte1.cubesearch.cube

import java.io.Serializable

/**
 * Class representing the configuration of a 3x3 Rubik's cube using an internal 2D array.
 *
 * @property data a minimal representation of the state of the cube
 *
 * @constructor constructs cube from a copy of the provided data
 * @param data the 6x9 array representing the desired cube. Format: (front, back, left, right, up, down)
 *
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
class ArrayCube internal constructor(var data: Array<IntArray>) : MutableStandardCube<ArrayCube>, Serializable {

    override fun twist(twist: Twist): ArrayCube {
        return when(twist) {
            Twist.FRONT_90 -> ArrayCube(twistFront90(data))
            Twist.FRONT_180 -> ArrayCube(twistFront180(data))
            Twist.FRONT_270 -> ArrayCube(twistFront270(data))
            Twist.BACK_90 -> ArrayCube(twistBack90(data))
            Twist.BACK_180 -> ArrayCube(twistBack180(data))
            Twist.BACK_270 -> ArrayCube(twistBack270(data))
            Twist.LEFT_90 -> ArrayCube(twistLeft90(data))
            Twist.LEFT_180 -> ArrayCube(twistLeft180(data))
            Twist.LEFT_270 -> ArrayCube(twistLeft270(data))
            Twist.RIGHT_90 -> ArrayCube(twistRight90(data))
            Twist.RIGHT_180 -> ArrayCube(twistRight180(data))
            Twist.RIGHT_270 -> ArrayCube(twistRight270(data))
            Twist.UP_90 -> ArrayCube(twistUp90(data))
            Twist.UP_180 -> ArrayCube(twistUp180(data))
            Twist.UP_270 -> ArrayCube(twistUp270(data))
            Twist.DOWN_90 -> ArrayCube(twistDown90(data))
            Twist.DOWN_180 -> ArrayCube(twistDown180(data))
            Twist.DOWN_270 -> ArrayCube(twistDown270(data))
        }
    }

    override fun twistNoCopy(twist: Twist): ArrayCube {
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

    /**
     * overridden equality to check whether the cubes have the same configuration
     */
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is ArrayCube) return false
        if(data.contentDeepEquals(other.data)) return true
        return false
    }

    /**
     * prints each face individually
     */
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
        return data[0].plus(data[1]).plus(data[2]).plus(data[3]).plus(data[4]).plus(data[5]).contentHashCode()
    }
}

//extension function to deep copy array
internal fun Array<IntArray>.copy() = Array(size) { i -> get(i).clone() }

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

