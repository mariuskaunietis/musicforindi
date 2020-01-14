package com.mediapark.saco.mpp.mobile.places

import com.mediapark.saco.mpp.mobile.ViewModel
import com.mediapark.saco.mpp.mobile.api.Constants
import com.mediapark.saco.mpp.mobile.printStackTrace
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.logging.SIMPLE
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

class PlacesViewModel : ViewModel<PlacesState, PlacesState.Event>(PlacesState()) {
    // should have shared instance - but there's only one vm in this app
    private val client by lazy {
        HttpClient {
            install(JsonFeature) {
                @UnstableDefault
                serializer = KotlinxSerializer(Json.nonstrict)
            }
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.BODY
            }
        }
    }

    override fun handleState(newState: PlacesState) {
        // we only care about requests, rest of state is purely for UI
        when (val request = newState.request) {
            is PlacesState.Request.LoadPage -> {
                MainScope().launch {
                    loadPage(request.page, request.query)
                }
            }
        }
    }

    private suspend fun loadPage(page: Int, query: String) {
        val builder = HttpRequestBuilder()

        // todo figure out builder style to add query params
        builder.url("${Constants.API_ROOT}place?fmt=json&query=$query&limit=${Constants.PER_PAGE}&offset=${page * Constants.PER_PAGE}")
        builder.method = HttpMethod.Get
        try {
            machine.transition(PlacesState.Event.FetchedPage(client.get(builder)))
        } catch (e: Throwable) {
            e.printStackTrace()
            machine.transition(PlacesState.Event.RequestFailed)
        }
    }

    // private val appService = application.appService
    //
    // private val _state = BehaviorSubject.create<PlacesState>()
    // val state: Observable<PlacesState> = _state.hide()
    // private val disposable = CompositeDisposable()
    //
    // private val _places = mutableListOf<MarkerPlace>()
    //
    // init {
    //   _state.onNext(PlacesState())
    //
    //   Observable
    //     .interval(0, 1L, TimeUnit.SECONDS)
    //     .subscribe {
    //       onTimeChanged()
    //     }
    //     .addTo(disposable)
    // }
    //
    //
    // override fun onCleared() {
    //   super.onCleared()
    //   disposable.dispose()
    // }
    //
    // fun textQueryChanged(newQuery: CharSequence) {
    //   if (newQuery.isBlank()) {
    //     updateState(PlacesState.Event.MarkersChanged(emptyList()))
    //   } else {
    //     Observable.create<List<Place>> { emitter ->
    //       val places = mutableListOf<Place>()
    //
    //       val response = appService
    //         .getPlaces(query = newQuery.toString())
    //         .blockingFirst()
    //       places.addAll(response.places)
    //
    //       val pages =
    //         Math.ceil(response.count.toDouble() / ApiConfig.queryLimit.toDouble()).roundToInt()
    //       for (i in 1 until pages) {
    //         updateState(PlacesState.Event.ShowProgress(i))
    //         val pageResponse = appService
    //           .getPlaces(query = newQuery.toString(), offset = i * ApiConfig.queryLimit)
    //           .blockingFirst()
    //         places.addAll(pageResponse.places)
    //       }
    //       emitter.onNext(places)
    //       emitter.onComplete()
    //     }
    //       .scheduleAsNetworkCall()
    //       .doOnSubscribe { updateState(PlacesState.Event.ShowProgress(0)) }
    //       .doOnComplete { updateState(PlacesState.Event.HideProgress) }
    //       .subscribe({ places ->
    //         updatePlaces(places)
    //       }, Throwable::printStackTrace)
    //       .addTo(disposable)
    //   }
    // }
    //
    // private fun updatePlaces(newPlaces: List<Place>) {
    //
    //
    //   val newMarkers = newPlaces
    //     .filter { it.coordinates != null }
    //     .filter { it.lifeSpan.begin != null }
    //     .map {
    //       val coordinates = it.coordinates!!
    //
    //       //most of the dates are before 1990, so very few markers would appear on map, so I use absolute values
    //
    //       val lifeDuration = Math.abs(it.lifeSpan.getBeginYear()!! - 1990)
    //       MarkerPlace(
    //         name = it.name,
    //         coordinates = LatLng(coordinates.latitude, coordinates.longitude),
    //         expiresAt = System.currentTimeMillis() + (lifeDuration * 1000)
    //       )
    //     }
    //   _places.clear()
    //   _places.addAll(newMarkers)
    //   updateState(PlacesState.Event.ZoomEvent(newMarkers))
    //   updateState(PlacesState.Event.MarkersChanged(newMarkers))
    //
    // }
    //
    // private fun onTimeChanged() {
    //   val newMarkers = _places.filter { !it.isExpired() }
    //   val currentMarkers = _state.value.markers
    //   if (newMarkers != currentMarkers) {
    //     //micro optimisation, don't filter same values many times
    //     _places.clear()
    //     _places.addAll(newMarkers)
    //     //update only if something has changed
    //     updateState(PlacesState.Event.MarkersChanged(newMarkers))
    //   }
    // }
    //
    //
    // private fun updateState(vararg events: PlacesState.Event) {
    //   var newState = _state.value
    //   events.forEach {
    //     newState = newState.reduce(it)
    //   }
    //   _state.onNext(newState)
    // }

}
