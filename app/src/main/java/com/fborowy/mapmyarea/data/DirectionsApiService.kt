package com.fborowy.mapmyarea.data

import com.fborowy.mapmyarea.data.classes.DirectionsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApiService {

    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String,
        @Query("units") units: String = "metric",
        @Query("alternatives") alternatives: Boolean = false,
        @Query("key") apiKey: String,
    ): DirectionsResponse

    //https://maps.googleapis.com/maps/api/directions/json?origin=37.773972,-122.431297&destination=37.774159,-122.426583&mode=walking&key=AIzaSyD8qT-Y_kwq6xba4u8EfWCRym1QQGKfQqQ
}