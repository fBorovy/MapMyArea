package com.fborowy.mapmyarea.domain.states

data class SavingMapState(
    val saveMapSuccessful: Boolean = false,
    val saveMapError: String? = null
)
