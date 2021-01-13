package com.millibyte1.cubesearch.cube

/**
 * Object that produces factories for the directly supported Cube implementations
 */
object CubeFactoryProducer {
    /**
     * Returns a factory for the cube implementation type with the given name
     * @throws IllegalArgumentException if the implementation type can't be resolved
     */
    @Throws(IllegalArgumentException::class)
    fun getFactory(implementationName: String): CubeFactory {
        return when(implementationName) {
            "ArrayCube" -> ArrayCubeFactory()
            "SmartCube" -> SmartCubeFactory()
            else -> throw failNoSuchImplementation()
        }
    }
}
private fun failNoSuchImplementation(): IllegalArgumentException {
    return IllegalArgumentException("Error: no implementation of the 'Cube' interface exists with the given name")
}