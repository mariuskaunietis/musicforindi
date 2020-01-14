package com.mediapark.saco.mpp.mobile.places.model

class ExpiringPlace(val name: String, val coordinates: Coordinates, private val expiresAt: Long) {
    fun isExpired(currentTime: Long): Boolean {
        return currentTime >= expiresAt
    }
}
