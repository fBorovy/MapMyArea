package com.fborowy.mapmyarea.data.classes

data class UserData(
    val userId: String?,
    val username: String?,
    val savedMaps: List<MapData>?,
) {
    constructor() : this( null, null, null)
}
