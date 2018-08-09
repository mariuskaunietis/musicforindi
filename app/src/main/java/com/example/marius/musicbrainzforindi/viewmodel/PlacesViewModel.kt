package com.example.marius.musicbrainzforindi.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.example.marius.musicbrainzforindi.api.ApiConfig
import com.example.marius.musicbrainzforindi.api.model.MarkerPlace
import com.example.marius.musicbrainzforindi.api.model.Place
import com.example.marius.musicbrainzforindi.appService
import com.example.marius.musicbrainzforindi.state.PlacesState
import com.example.marius.musicbrainzforindi.utils.scheduleAsNetworkCall
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class PlacesViewModel(application: Application) : AndroidViewModel(application) {

  private val appService = application.appService

  private val _state = BehaviorSubject.create<PlacesState>()
  val state: Observable<PlacesState> = _state.hide()
  private val disposable = CompositeDisposable()

  private val _places = mutableListOf<MarkerPlace>()

  init {
    _state.onNext(PlacesState())

    Observable
      .interval(0, 1L, TimeUnit.SECONDS)
      .subscribe {
        onTimeChanged()
      }
      .addTo(disposable)
  }


  override fun onCleared() {
    super.onCleared()
    disposable.dispose()
  }

  fun textQueryChanged(newQuery: CharSequence) {
    if (newQuery.isBlank()) {
      updateState(PlacesState.Event.MarkersChanged(emptyList()))
    } else {
      Observable.create<List<Place>> { emitter ->
        val places = mutableListOf<Place>()

        val response = appService
          .getPlaces(query = newQuery.toString())
          .blockingFirst()
        places.addAll(response.places)

        val pages =
          Math.ceil(response.count.toDouble() / ApiConfig.queryLimit.toDouble()).roundToInt()
        for (i in 1 until pages) {
          updateState(PlacesState.Event.ShowProgress(i))
          val pageResponse = appService
            .getPlaces(query = newQuery.toString(), offset = i * ApiConfig.queryLimit)
            .blockingFirst()
          places.addAll(pageResponse.places)
        }
        emitter.onNext(places)
        emitter.onComplete()
      }
        .scheduleAsNetworkCall()
        .doOnSubscribe { updateState(PlacesState.Event.ShowProgress(0)) }
        .doOnComplete { updateState(PlacesState.Event.HideProgress) }
        .subscribe({ places ->
          updatePlaces(places)
        }, Throwable::printStackTrace)
        .addTo(disposable)
    }
  }

  private fun updatePlaces(newPlaces: List<Place>) {


    val newMarkers = newPlaces
      .filter { it.coordinates != null }
      .filter { it.lifeSpan.begin != null }
      .map {
        val coordinates = it.coordinates!!

        //most of the dates are before 1990, so very few markers would appear on map, so I use absolute values

        val lifeDuration = Math.abs(it.lifeSpan.getBeginYear()!! - 1990)
        MarkerPlace(
          name = it.name,
          coordinates = LatLng(coordinates.latitude, coordinates.longitude),
          expiresAt = System.currentTimeMillis() + (lifeDuration * 1000)
        )
      }
    _places.clear()
    _places.addAll(newMarkers)
    updateState(PlacesState.Event.ZoomEvent(newMarkers))
    updateState(PlacesState.Event.MarkersChanged(newMarkers))

  }

  private fun onTimeChanged() {
    val newMarkers = _places.filter { !it.isExpired() }
    val currentMarkers = _state.value.markers
    if (newMarkers != currentMarkers) {
      //micro optimisation, don't filter same values many times
      _places.clear()
      _places.addAll(newMarkers)
      //update only if something has changed
      updateState(PlacesState.Event.MarkersChanged(newMarkers))
    }
  }


  private fun updateState(vararg events: PlacesState.Event) {
    var newState = _state.value
    events.forEach {
      newState = newState.reduce(it)
    }
    _state.onNext(newState)
  }


}