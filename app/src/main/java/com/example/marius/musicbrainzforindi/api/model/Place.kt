package com.example.marius.musicbrainzforindi.api.model

import com.mediapark.saco.mpp.mobile.places.model.Coordinates
import com.mediapark.saco.mpp.mobile.places.model.LifeSpan
import com.squareup.moshi.Json

class Place(
  val name: String,
  @Json(name = "life-span") val lifeSpan: LifeSpan,
  val address: String?,
  val coordinates: Coordinates?
)