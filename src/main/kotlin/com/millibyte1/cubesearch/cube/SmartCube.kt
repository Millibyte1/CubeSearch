package com.millibyte1.cubesearch.cube

import com.millibyte1.cubesearch.util.Cubie
import com.millibyte1.cubesearch.util.OrientedCornerCubie
import com.millibyte1.cubesearch.util.OrientedEdgeCubie
import com.millibyte1.cubesearch.util.OrientedCenterCubie

/**
 * A cube implementation that directly stores cubie positions and orientations in order to rapidly compute
 * important information about the cube (solvability, configuration index, etc.)
 *
 */
class SmartCube : MutableStandardCube<SmartCube> {

    val corners: Array<OrientedCornerCubie> = TODO()
    val edges: Array<OrientedEdgeCubie> = TODO()
    val centers: Array<OrientedCenterCubie> = TODO()

    /**
     * returns the cube resulting from applying the given twist, without modifying this cube.
     * @param twist the twist we are applying to this cube
     * @return the cube resulting from applying the given twist
     */
    override fun twist(twist: Twist): SmartCube {
        TODO("Not yet implemented")
    }

    /**
     * Applies the given twist to this cube. Modifies this, but returns this as well.
     * @param twist the twist we are applying to this cube
     * @return this cube
     */
    override fun twistNoCopy(twist: Twist): SmartCube {
        TODO("Not yet implemented")
    }
}