package com.mediapark.saco.mpp.mobile.places.model

import kotlinx.serialization.Serializable

@Serializable
class Place(
    val name: String,
    val lifeSpan: LifeSpan? = null,
    val address: String? = null,
    val coordinates: Coordinates? = null
)