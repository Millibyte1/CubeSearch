package com.millibyte1.cubesearch.cube

/**
 * Factory for instances of the immutable Cube class
 * TODO might have to use object pooling for performance
 */
class CubeFactory : AbstractCubeFactory<Cube>() {

    override fun getCube(data: Array<IntArray>): Cube {
        return Cube(data.copy())
    }

    override fun getCube(cube: Cube): Cube {
        return Cube(cube.data.copy())
    }

    override fun getSolvedCube(): Cube {
        return Cube(arrayOf(
                IntArray(9) { 0 }, //front face
                IntArray(9) { 1 }, //back face
                IntArray(9) { 2 }, //left face
                IntArray(9) { 3 }, //right face
                IntArray(9) { 4 }, //up face
                IntArray(9) { 5 }  //down face
        ))
    }

}