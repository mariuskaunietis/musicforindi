package com.example.marius.musicbrainzforindi.state

import com.example.marius.musicbrainzforindi.api.model.MarkerPlace
import com.example.marius.musicbrainzforindi.utils.UiEffect

data class PlacesState(
  val progressCount: Int? = null,
  val markers: List<MarkerPlace> = emptyList(),
  val effect: UiEffect<Effect>? = null
) {

  sealed class Event {
    class ShowProgress(val count: Int?) : Event()
    object HideProgress : Event()
    class MarkersChanged(val markers: List<MarkerPlace>) : Event()
  }


  sealed class Effect {
    object OpenLegacyScan : Effect()
    object ShowPaymentFailed : Effect()
  }

  fun reduce(event: Event): PlacesState {
    return when (event) {
      is Event.ShowProgress -> copy(progressCount = event.count)
      Event.HideProgress -> copy(progressCount = null)
      is Event.MarkersChanged -> copy(markers = event.markers)
    }
  }

}

