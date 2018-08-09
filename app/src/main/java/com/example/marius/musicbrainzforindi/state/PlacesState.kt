package com.example.marius.musicbrainzforindi.state

import com.example.marius.musicbrainzforindi.api.model.MarkerPlace
import com.example.marius.musicbrainzforindi.utils.UiEffect
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

data class PlacesState(
  val progressCount: Int? = null,
  val markers: List<MarkerPlace> = emptyList(),
  val effect: UiEffect<Effect>? = null
) {

  sealed class Event {
    class ShowProgress(val count: Int) : Event()
    object HideProgress : Event()
    class MarkersChanged(val markers: List<MarkerPlace>) : Event()
    class ZoomEvent(val markers: List<MarkerPlace>) : Event()
  }


  sealed class Effect {
    class ZoomToLatLng(val latLng: LatLng, val zoom: Float = 15f) : Effect()
    class ZoomToLatLngBounds(val bounds: LatLngBounds) : Effect()
  }

  fun reduce(event: Event): PlacesState {
    return when (event) {
      is Event.ShowProgress -> copy(progressCount = event.count)
      Event.HideProgress -> copy(progressCount = null)
      is Event.MarkersChanged -> copy(markers = event.markers)
      is Event.ZoomEvent -> {
        when {
          event.markers.size > 1 -> {
            val bounds = LatLngBounds.builder()
            event.markers.forEach { bounds.include(it.coordinates) }
            copy(effect = UiEffect(Effect.ZoomToLatLngBounds(bounds.build())))
          }
          event.markers.size == 1 -> {
            val latLng = event.markers[0].coordinates
            copy(effect = UiEffect(Effect.ZoomToLatLng(latLng)))
          }
          else -> this
        }
      }
    }
  }

}

