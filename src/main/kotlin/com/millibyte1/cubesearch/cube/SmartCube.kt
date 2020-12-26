package com.millibyte1.cubesearch.cube

import com.millibyte1.cubesearch.util.*
import java.io.Serializable

/**
 * A cube implementation that directly stores cubie positions and orientations in order to rapidly compute
 * important information about the cube (solvability, configuration index, etc.)
 *
 * @constructor constructs a SmartCube directly from an array of oriented cubies
 * @param cubies the array of cubies for this cube
 */
class SmartCube internal constructor(private var cubies: Array<OrientedCubie>) : MutableStandardCube<SmartCube>, Serializable {

    /**
     * returns the cube resulting from applying the given twist, without modifying this cube.
     * @param twist the twist we are applying to this cube
     * @return the cube resulting from applying the given twist
     */
    override fun twist(twist: Twist): SmartCube {
        return when(twist) {
            Twist.FRONT_90 -> SmartCube(twistFront90(cubies))
            Twist.FRONT_180 -> SmartCube(twistFront180(cubies))
            Twist.FRONT_270 -> SmartCube(twistFront270(cubies))
            Twist.BACK_90 -> SmartCube(twistBack90(cubies))
            Twist.BACK_180 -> SmartCube(twistBack180(cubies))
            Twist.BACK_270 -> SmartCube(twistBack270(cubies))
            Twist.LEFT_90 -> SmartCube(twistLeft90(cubies))
            Twist.LEFT_180 -> SmartCube(twistLeft180(cubies))
            Twist.LEFT_270 -> SmartCube(twistLeft270(cubies))
            Twist.RIGHT_90 -> SmartCube(twistRight90(cubies))
            Twist.RIGHT_180 -> SmartCube(twistRight180(cubies))
            Twist.RIGHT_270 -> SmartCube(twistRight270(cubies))
            Twist.UP_90 -> SmartCube(twistUp90(cubies))
            Twist.UP_180 -> SmartCube(twistUp180(cubies))
            Twist.UP_270 -> SmartCube(twistUp270(cubies))
            Twist.DOWN_90 -> SmartCube(twistDown90(cubies))
            Twist.DOWN_180 -> SmartCube(twistDown180(cubies))
            Twist.DOWN_270 -> SmartCube(twistDown270(cubies))
        }
    }

    /**
     * Applies the given twist to this cube. Modifies this, but returns this as well.
     * @param twist the twist we are applying to this cube
     * @return this cube
     */
    override fun twistNoCopy(twist: Twist): SmartCube {
        when(twist) {
            Twist.FRONT_90 -> { this.cubies = twistFront90(cubies); return this }
            Twist.FRONT_180 -> { this.cubies = twistFront180(cubies); return this }
            Twist.FRONT_270 -> { this.cubies = twistFront270(cubies); return this }
            Twist.BACK_90 -> { this.cubies = twistBack90(cubies); return this }
            Twist.BACK_180 -> { this.cubies = twistBack180(cubies); return this }
            Twist.BACK_270 -> { this.cubies = twistBack270(cubies); return this }
            Twist.LEFT_90 -> { this.cubies = twistLeft90(cubies); return this }
            Twist.LEFT_180 -> { this.cubies = twistLeft180(cubies); return this }
            Twist.LEFT_270 -> { this.cubies = twistLeft270(cubies); return this }
            Twist.RIGHT_90 -> { this.cubies = twistRight90(cubies); return this }
            Twist.RIGHT_180 -> { this.cubies = twistRight180(cubies); return this }
            Twist.RIGHT_270 -> { this.cubies = twistRight270(cubies); return this }
            Twist.UP_90 -> { this.cubies = twistUp90(cubies); return this }
            Twist.UP_180 -> { this.cubies = twistUp180(cubies); return this }
            Twist.UP_270 -> { this.cubies = twistUp270(cubies); return this }
            Twist.DOWN_90 -> { this.cubies = twistDown90(cubies); return this }
            Twist.DOWN_180 -> { this.cubies = twistDown180(cubies); return this }
            Twist.DOWN_270 -> { this.cubies = twistDown270(cubies); return this }
        }
    }

    /** Gets the list of cubies, ordered by position */
    private fun getCubies(): List<OrientedCubie> {
        return arrayListOf(*cubies)
    }
    /** Gets the list of centers, ordered by position */
    private fun getCenters(): List<OrientedCenterCubie> {
        val retval = ArrayList<OrientedCenterCubie>()
        for(i in 0..5) retval.add(cubies[i] as OrientedCenterCubie)
        return retval
    }
    /** Gets the list of edges, ordered by position */
    private fun getEdges(): List<OrientedEdgeCubie> {
        val retval = ArrayList<OrientedEdgeCubie>()
        for(i in 6..17) retval.add(cubies[i] as OrientedEdgeCubie)
        return retval
    }
    /** Gets the list of corners, ordered by position */
    private fun getCorners(): List<OrientedCornerCubie> {
        val retval = ArrayList<OrientedCornerCubie>()
        for(i in 18..25) retval.add(cubies[i] as OrientedCornerCubie)
        return retval
    }
}

private fun twistFront90(cubies: Array<OrientedCubie>) : Array<OrientedCubie> {
    TODO()
}
private fun twistFront180(cubies: Array<OrientedCubie>) : Array<OrientedCubie> {
    TODO()
}
private fun twistFront270(cubies: Array<OrientedCubie>) : Array<OrientedCubie> {
    TODO()
}
private fun twistBack90(cubies: Array<OrientedCubie>) : Array<OrientedCubie> {
    TODO()
}
private fun twistBack180(cubies: Array<OrientedCubie>) : Array<OrientedCubie> {
    TODO()
}
private fun twistBack270(cubies: Array<OrientedCubie>) : Array<OrientedCubie> {
    TODO()
}
private fun twistLeft90(cubies: Array<OrientedCubie>) : Array<OrientedCubie> {
    TODO()
}
private fun twistLeft180(cubies: Array<OrientedCubie>) : Array<OrientedCubie> {
    TODO()
}
private fun twistLeft270(cubies: Array<OrientedCubie>) : Array<OrientedCubie> {
    TODO()
}
private fun twistRight90(cubies: Array<OrientedCubie>) : Array<OrientedCubie> {
    TODO()
}
private fun twistRight180(cubies: Array<OrientedCubie>) : Array<OrientedCubie> {
    TODO()
}
private fun twistRight270(cubies: Array<OrientedCubie>) : Array<OrientedCubie> {
    TODO()
}
private fun twistUp90(cubies: Array<OrientedCubie>) : Array<OrientedCubie> {
    TODO()
}
private fun twistUp180(cubies: Array<OrientedCubie>) : Array<OrientedCubie> {
    TODO()
}
private fun twistUp270(cubies: Array<OrientedCubie>) : Array<OrientedCubie> {
    TODO()
}
private fun twistDown90(cubies: Array<OrientedCubie>) : Array<OrientedCubie> {
    TODO()
}
private fun twistDown180(cubies: Array<OrientedCubie>) : Array<OrientedCubie> {
    TODO()
}
private fun twistDown270(cubies: Array<OrientedCubie>) : Array<OrientedCubie> {
    TODO()
}