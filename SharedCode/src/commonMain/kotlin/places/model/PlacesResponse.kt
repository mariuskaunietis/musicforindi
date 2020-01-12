package com.mediapark.saco.mpp.mobile.places.model

import kotlinx.serialization.Serializable

@Serializable
class PlacesResponse(
    val created: String,
    val count: Int,
    val offset: Int,
    val places: List<Place>
) {
    fun markerPlaces(timestamp: Long): List<ExpiringPlace> {
        return places
            .filter { it.coordinates  != null && it.lifeSpan?.begin != null }
            .map {
                val duration = it.lifeSpan?.getBeginYear()!! - 1990
                val expiresAt = timestamp + duration

                ExpiringPlace(
                    name = it.name,
                    coordinates = it.coordinates!!,
                    expiresAt = expiresAt
                )
            }
    }
}