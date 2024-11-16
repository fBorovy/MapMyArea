package com.fborowy.mapmyarea.data.classes

import com.google.gson.annotations.SerializedName

class DirectionsResponse (
    val routes: List<Route>
)

data class Route (
    val legs: List<Leg>,
    @SerializedName("overview_polyline")
    val overviewPolyline: Polyline?,
)

data class Leg (
    val distance: Distance,
    val duration: Duration
)

data class Duration(
    val text: String,
    val value: Int, //seconds
)

data class Distance(
    val text: String,
    val value: Int, // meters
)

data class Polyline (
    val points: String
)
