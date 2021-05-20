package io.github.millibyte1.cubesearch.cube

/** Abstract interface for factories of different implementations of AnalyzableStandardCube */
interface AnalyzableStandardCubeFactory : CubeFactory {
    override fun getCube(data: Array<IntArray>): AnalyzableStandardCube
    override fun getCube(cube: Cube): AnalyzableStandardCube
    override fun getSolvedCube(): AnalyzableStandardCube
}