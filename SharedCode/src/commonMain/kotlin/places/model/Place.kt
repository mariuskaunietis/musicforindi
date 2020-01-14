package com.mediapark.saco.mpp.mobile.places.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Place(
    val name: String,
    @SerialName("life-span")
    val lifeSpan: LifeSpan? = null,
    val coordinates: Coordinates? = null
)