package com.millibyte1.cubesearch.cube

/**
 * Factory for instances of the immutable Cube class
 * TODO might have to use object pooling for performance
 */
class ArrayCubeFactory : AbstractCubeFactory<ArrayCube>() {

    override fun getCube(data: Array<IntArray>): ArrayCube {
        return ArrayCube(data.copy())
    }

    override fun getCube(cube: ArrayCube): ArrayCube {
        return ArrayCube(cube.data.copy())
    }

    override fun getSolvedCube(): ArrayCube {
        return ArrayCube(arrayOf(
                IntArray(9) { 0 }, //front face
                IntArray(9) { 1 }, //back face
                IntArray(9) { 2 }, //left face
                IntArray(9) { 3 }, //right face
                IntArray(9) { 4 }, //up face
                IntArray(9) { 5 }  //down face
        ))
    }

}