package com.millibyte1.cubesearch.cube

class SmartCubeFactory : CubeFactory {
    /**
     * Constructs a cube from the contents of [data]
     * @param data the 6xN array representing the faces of the cube
     * @return the constructed cube
     */
    override fun getCube(data: Array<IntArray>): SmartCube {
        return SmartCube(data)
    }
    /**
     * Constructs a cube from the contents of the data and orientation arrays
     * @param data the 6xN array representing the faces of the cube
     * @param edgePositions the 12-array representing the positions of the edges ordered by cubie number (coloration)
     * @param cornerPositions the 8-array representing the positions of the corners ordered by cubie number (coloration)
     * @param edgeOrientations the 12-array representing the orientations of the edges ordered by position
     * @param cornerOrientations the 8-array representing the orientations of the corners ordered by position
     * @return the constructed cube
     */
    fun getCube(
        data: Array<IntArray>,
        edgePositions: IntArray,
        cornerPositions: IntArray,
        edgeOrientations: IntArray,
        cornerOrientations: IntArray
    ): SmartCube {
        return SmartCube(data, edgePositions, cornerPositions, edgeOrientations, cornerOrientations)
    }

    override fun getCube(cube: Cube): SmartCube {
        if(cube is SmartCube) {
            return SmartCube(
                cube.data,
                cube.edgePositions,
                cube.cornerPositions,
                cube.edgeOrientations,
                cube.cornerOrientations
            )
        }
        if(cube is ArrayCube) {
            return SmartCube(cube.data)
        }
        throw failWrongImplementation()
    }
    /** Returns a SmartCube from this ArrayCube
     * @param cube the cube to copy
     * @return the constructed cube
     */
    fun getCube(cube: ArrayCube): SmartCube {
        return SmartCube(cube.data)
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
private fun failWrongImplementation(): IllegalArgumentException {
    return IllegalArgumentException("Error: factory cannot produce an SmartCube from an instance of the provided cube's type")
}