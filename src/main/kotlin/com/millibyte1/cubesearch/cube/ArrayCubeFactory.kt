package com.millibyte1.cubesearch.cube

/**
 * Factory for ArrayCubes
 */
class ArrayCubeFactory : AnalyzableStandardCubeFactory {

    override fun getCube(data: Array<IntArray>): ArrayCube {
        return ArrayCube(data.copy())
    }

    override fun getCube(cube: Cube): ArrayCube {
        if(cube !is ArrayCube) throw failWrongImplementation()
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
private fun failWrongImplementation(): IllegalArgumentException {
    return IllegalArgumentException("Error: factory cannot produce an ArrayCube from an instance of the provided cube's type")
}