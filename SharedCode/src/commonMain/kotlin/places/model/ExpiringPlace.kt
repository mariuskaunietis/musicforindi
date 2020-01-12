package com.mediapark.saco.mpp.mobile.places.model

import com.mediapark.saco.mpp.mobile.getTimestamp

class ExpiringPlace(val name: String, val coordinates: Coordinates, val expiresAt: Long) {
    fun isExpired(): Boolean {
        val currentTime = getTimestamp()
        return currentTime >= expiresAt
    }
}
