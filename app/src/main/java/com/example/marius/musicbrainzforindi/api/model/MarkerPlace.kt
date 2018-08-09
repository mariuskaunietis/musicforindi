package com.example.marius.musicbrainzforindi.api.model

import com.google.android.gms.maps.model.LatLng

class MarkerPlace(val name: String, val coordinates: LatLng, val expiresAt: Long) {
  fun isExpired(): Boolean {
    val currentTime = System.currentTimeMillis()
    return currentTime >= expiresAt
  }
}
