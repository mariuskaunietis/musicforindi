package com.example.marius.musicbrainzforindi.api

import com.mediapark.saco.mpp.mobile.places.model.PlacesResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface AppService {

  @GET("place")
  fun getPlaces(
    @Query("query") query: String,
    @Query("fmt") format: String = "json",
    @Query("limit") limit: Int = ApiConfig.queryLimit,
    @Query("offset") offset: Int = 0
  ): Observable<PlacesResponse>


}