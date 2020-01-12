package com.example.marius.musicbrainzforindi.api.model

import com.mediapark.saco.mpp.mobile.places.model.Place

class PlacesResponse(
  val created: String, //could create moshi converter, but we're not using this date anywhere, might as well even ignore it
  val count: Int,
  val offset: Int,
  val places: List<Place>
)