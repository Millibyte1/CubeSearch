package io.github.millibyte1.cubesearch.cube

/**
 * Interface for a standard 3x3 Rubik's cube that implements both mutable and immutable twists and functions for
 * analyzing its configuration.
 */
interface AnalyzableStandardCube : StandardCube {
    /**
     * Gets the permutation of edge positions as an array of integers, sorted by the solved positions of the pieces
     * (i.e. the orientation of the piece that is in the up-front edge position when solved is first).
     *
     * The enumeration of edge positions is as follows:
     * up-front, up-back, up-left, up-right,
     * down-front, down-back, down-left, down-right,
     * front-left, front-right, back-left, back-right
     *
     * @return The permutation of edge positions as an array of integers. Depending on the implementation, modifying
     * this without copying may or may not modify the cube.
     */
    fun getEdgePositionPermutation(): IntArray
    /**
     * Gets the permutation of corner positions as an array of integers, sorted by the solved positions of the pieces
     * (i.e. the orientation of the piece that is in the up-front-left corner position when solved is first).
     *
     * The enumeration of corner positions is as follows:
     * up-front-left, up-front-right, up-back-left, up-back-right,
     * down-front-left, down-front-right, down-back-left, down-back-right
     *
     * @return The permutation of corner positions as an array of integers. Depending on the implementation, modifying
     * this without copying may or may not modify the cube.
     */
    fun getCornerPositionPermutation(): IntArray
    /**
     * Gets the permutation of edge orientations as an array of integers, sorted by the solved positions of the pieces
     * (i.e. the orientation of the piece that is in the up-front edge position when solved is first).
     *
     * The enumeration of edge orientations is as follows:
     * up-front, up-back, up-left, up-right,
     * down-front, down-back, down-left, down-right,
     * front-left, front-right, back-left, back-right
     *
     * @return The permutation of edge orientations as an array of integers. Depending on the implementation, modifying
     * this without copying may or may not modify the cube.
     */
    fun getEdgeOrientationPermutation(): IntArray
    /**
     * Gets the permutation of corner orientations as an array of integers, sorted by the solved positions of the pieces
     * (i.e. the orientation of the piece that is in the up-front-left corner position when solved is first).
     * @return the permutation of corner orientations as an array of integers.
     *
     * The enumeration of corner orientations is as follows:
     * up-front-left, up-front-right, up-back-left, up-back-right,
     * down-front-left, down-front-right, down-back-left, down-back-right
     *
     * @return The permutation of corner orientations as an array of integers. Depending on the implementation, modifying
     * this without copying may or may not modify the cube.
     */
    fun getCornerOrientationPermutation(): IntArray

    override fun twist(twist: Twist): AnalyzableStandardCube
    override fun twistNoCopy(twist: Twist): AnalyzableStandardCube
}