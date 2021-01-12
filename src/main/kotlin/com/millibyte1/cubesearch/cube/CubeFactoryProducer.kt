package com.millibyte1.cubesearch.cube

/**
 * Object that produces
 */
object CubeFactoryProducer {

    fun getFactory(implementationName: String) {
        TODO()
    }
}
private fun failNoSuchImplementation(): IllegalArgumentException {
    return IllegalArgumentException("Error: no implementation of the 'Cube' interface exists with the given name")
}