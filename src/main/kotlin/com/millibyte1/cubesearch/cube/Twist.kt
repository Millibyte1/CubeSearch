package com.millibyte1.cubesearch.cube

/**
 * Describes all valid twists in the standard half-turn metric for the Rubik's Cube
 *
 *
 */
enum class Twist {

    FRONT_90, FRONT_180, FRONT_270,
    BACK_90, BACK_180, BACK_270,
    LEFT_90, LEFT_180, LEFT_270,
    RIGHT_90, RIGHT_180, RIGHT_270,
    UP_90, UP_180, UP_270,
    DOWN_90, DOWN_180, DOWN_270;

    enum class Face {
        FRONT, BACK, LEFT, RIGHT, UP, DOWN
    }

    companion object {
        /**
         * Returns the face associated with this twist
         * @param twist the twist in question
         * @return the face that the given twist is acting on
         */
        fun getFace(twist: Twist): Face {
            return when {
                (twist == FRONT_90 || twist == FRONT_180 || twist == FRONT_270) -> Face.FRONT
                (twist == BACK_90 || twist == BACK_180 || twist == BACK_270) -> Face.BACK
                (twist == LEFT_90 || twist == LEFT_180 || twist == LEFT_270) -> Face.LEFT
                (twist == RIGHT_90 || twist == RIGHT_180 || twist == RIGHT_270) -> Face.RIGHT
                (twist == UP_90 || twist == UP_180 || twist == UP_270) -> Face.UP
                else -> Face.DOWN
            }
        }

        /**
         * Gets the face opposite of this one
         * @param face the face in question
         * @return the face opposite of [face]
         */
        fun getOppositeFace(face: Face): Face {
            return when(face) {
                Face.FRONT -> Face.BACK
                Face.BACK -> Face.FRONT
                Face.LEFT -> Face.RIGHT
                Face.RIGHT -> Face.LEFT
                Face.UP -> Face.DOWN
                Face.DOWN -> Face.UP
            }
        }
    }
}

