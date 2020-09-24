package com.millibyte1.cubesearch.cube

import java.io.Serializable

/**
 * Immutable class representing the configuration of a 3x3 Rubik's cube
 *
 * @property data a minimal representation of the state of the cube
 *
 * @constructor constructs cube from a copy of the provided data
 * @param data the 6x9 array representing the desired cube. Format: (front, back, left, right, up, down)
 *
 * Flattened cube:
 *
 *         0 1 2
 *         3 U 5
 *         6 7 8
 *  0 1 2  0 1 2  0 1 2  0 1 2
 *  3 L 5  3 F 5  3 R 5  3 B 5
 *  6 7 8  6 7 8  6 7 8  6 7 8
 *         0 1 2
 *         3 D 5
 *         6 7 8
 */
//TODO: make constructors private and force the use of factories
class Cube internal constructor(data: Array<IntArray>) : AbstractCube<Cube>(data), Serializable {

    /**
     * takes a twist and returns the cube that results from it
     * pre: N/A
     * post: this cube is unchanged
     * @param twist the twist to be performed
     * @return the cube that results from applying this twist
     */
    override fun twist(twist: Twist): Cube {
        return when(twist) {
            Twist.FRONT_90 -> twistFront90()
            Twist.FRONT_180 -> twistFront180()
            Twist.FRONT_270 -> twistFront270()
            Twist.BACK_90 -> twistBack90()
            Twist.BACK_180 -> twistBack180()
            Twist.BACK_270 -> twistBack270()
            Twist.LEFT_90 -> twistLeft90()
            Twist.LEFT_180 -> twistLeft180()
            Twist.LEFT_270 -> twistLeft270()
            Twist.RIGHT_90 -> twistRight90()
            Twist.RIGHT_180 -> twistRight180()
            Twist.RIGHT_270 -> twistRight270()
            Twist.UP_90 -> twistUp90()
            Twist.UP_180 -> twistUp180()
            Twist.UP_270 -> twistUp270()
            Twist.DOWN_90 -> twistDown90()
            Twist.DOWN_180 -> twistDown180()
            Twist.DOWN_270 -> twistDown270()
        }
    }
    private fun twistFront90() : Cube {
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
        copy[0][5] = data[0][1]
        copy[0][8] = data[0][2]
        copy[0][7] = data[0][5]
        copy[0][6] = data[0][8]
        //wrap and return
        return Cube(copy)
    }
    private fun twistFront180() : Cube {
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
        copy[0][5] = data[0][3]
        copy[0][8] = data[0][0]
        copy[0][7] = data[0][1]
        copy[0][6] = data[0][2]
        //wrap and return
        return Cube(copy)
    }
    private fun twistFront270() : Cube {
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
        copy[0][5] = data[0][7]
        copy[0][8] = data[0][6]
        copy[0][7] = data[0][3]
        copy[0][6] = data[0][0]
        //wrap and return
        return Cube(copy)
    }
    private fun twistBack90() : Cube {
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
        copy[1][5] = data[1][1]
        copy[1][8] = data[1][2]
        copy[1][7] = data[1][5]
        copy[1][6] = data[1][8]
        //wrap and return
        return Cube(copy)
    }
    private fun twistBack180() : Cube {
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
        copy[1][5] = data[1][3]
        copy[1][8] = data[1][0]
        copy[1][7] = data[1][1]
        copy[1][6] = data[1][2]
        //wrap and return
        return Cube(copy)
    }
    private fun twistBack270() : Cube {
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
        copy[1][5] = data[1][7]
        copy[1][8] = data[1][6]
        copy[1][7] = data[1][3]
        copy[1][6] = data[1][0]
        //wrap and return
        return Cube(copy)
    }
    private fun twistLeft90() : Cube {
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
        copy[2][5] = data[2][1]
        copy[2][8] = data[2][2]
        copy[2][7] = data[2][5]
        copy[2][6] = data[2][8]
        //wrap and return
        return Cube(copy)
    }
    private fun twistLeft180() : Cube {
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
        copy[2][5] = data[2][3]
        copy[2][8] = data[2][0]
        copy[2][7] = data[2][1]
        copy[2][6] = data[2][2]
        //wrap and return
        return Cube(copy)
    }
    private fun twistLeft270() : Cube {
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
        copy[2][5] = data[2][7]
        copy[2][8] = data[2][6]
        copy[2][7] = data[2][3]
        copy[2][6] = data[2][0]
        return Cube(copy)
    }
    private fun twistRight90() : Cube {
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
        copy[3][5] = data[3][1]
        copy[3][8] = data[3][2]
        copy[3][7] = data[3][5]
        copy[3][6] = data[3][8]
        //wrap and return
        return Cube(copy)
    }
    private fun twistRight180() : Cube {
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
        copy[3][5] = data[3][3]
        copy[3][8] = data[3][0]
        copy[3][7] = data[3][1]
        copy[3][6] = data[3][2]
        //wrap and return
        return Cube(copy)
    }
    private fun twistRight270() : Cube {
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
        copy[3][5] = data[3][7]
        copy[3][8] = data[3][6]
        copy[3][7] = data[3][3]
        copy[3][6] = data[3][0]
        //wrap and return
        return Cube(copy)
    }
    private fun twistUp90() : Cube {
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
        copy[4][5] = data[4][1]
        copy[4][8] = data[4][2]
        copy[4][7] = data[4][5]
        copy[4][6] = data[4][8]
        //wrap and return
        return Cube(copy)
    }
    private fun twistUp180() : Cube {
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
        copy[4][5] = data[4][3]
        copy[4][8] = data[4][0]
        copy[4][7] = data[4][1]
        copy[4][6] = data[4][2]
        //wrap and return
        return Cube(copy)
    }
    private fun twistUp270() : Cube {
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
        copy[4][5] = data[4][7]
        copy[4][8] = data[4][6]
        copy[4][7] = data[4][3]
        copy[4][6] = data[4][0]
        //wrap and return
        return Cube(copy)
    }
    private fun twistDown90() : Cube {
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
        copy[5][5] = data[5][1]
        copy[5][8] = data[5][2]
        copy[5][7] = data[5][5]
        copy[5][6] = data[5][8]
        //wrap and return
        return Cube(copy)
    }
    private fun twistDown180() : Cube {
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
        copy[5][5] = data[5][3]
        copy[5][8] = data[5][0]
        copy[5][7] = data[5][1]
        copy[5][6] = data[5][2]
        //wrap and return
        return Cube(copy)
    }
    private fun twistDown270() : Cube {
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
        copy[5][5] = data[5][7]
        copy[5][8] = data[5][6]
        copy[5][7] = data[5][3]
        copy[5][6] = data[5][0]
        //wrap and return
        return Cube(copy)
    }

    /**
     * overridden equality to check whether the cubes have the same configuration
     */
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is Cube) return false
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

}

//extension function to deep copy array
//fun Array<IntArray>.copy() = Array(size) { i -> get(i).clone() }

