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
         * @param twist the twist in question
         * @return the face that the given twist is acting on
         */
        fun getFace(twist: Twist): Face {
            return if(twist == FRONT_90 || twist == FRONT_180 || twist == FRONT_270) Face.FRONT
              else if(twist
                    == BACK_90 || twist == BACK_180 || twist == BACK_270) Face.BACK
              else if(twist == LEFT_90 || twist == LEFT_180 || twist == LEFT_270) Face.LEFT
              else if(twist == RIGHT_90 || twist == RIGHT_180 || twist == RIGHT_270) Face.RIGHT
              else if(twist == UP_90 || twist == UP_180 || twist == UP_270) Face.UP
              else Face.DOWN
        }
    }
}

