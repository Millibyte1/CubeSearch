package com.millibyte1.cubesearch.cube

import com.millibyte1.cubesearch.util.OrientedCubie
import com.millibyte1.cubesearch.util.SmartCubeUtils

class SmartCubeFactory : AbstractCubeFactory<SmartCube>() {
    /**
     * Constructs a cube from the contents of [data]
     * @param data the 6xN array representing the faces of the cube
     * @return the constructed cube
     */
    override fun getCube(data: Array<IntArray>): SmartCube {
        return SmartCube(SmartCubeUtils.getCubies(data))
    }
    /**
     * Constructs a cube from the contents of [cubies]
     * @param cubies the array of cubies this cube should contain
     * @return the constructed cube
     */
    fun getCube(cubies: Array<OrientedCubie>): SmartCube {
        return SmartCube(cubies)
    }
    /** Returns a copy of this cube
     * @param cube the cube to copy
     * @return the constructed cube
     */
    override fun getCube(cube: SmartCube): SmartCube {
        return SmartCube(cube.getCubies())
    }

    /**
     * Returns the solved cube
     * @return the solved cube
     */
    override fun getSolvedCube(): SmartCube {
        return getCube(
            arrayOf(
                IntArray(9) { 0 }, //front face
                IntArray(9) { 1 }, //back face
                IntArray(9) { 2 }, //left face
                IntArray(9) { 3 }, //right face
                IntArray(9) { 4 }, //up face
                IntArray(9) { 5 }  //down face
            )
        )
    }
}