package com.example.marius.musicbrainzforindi.state

import com.example.marius.musicbrainzforindi.api.model.MarkerPlace
import com.example.marius.musicbrainzforindi.api.model.Place
import com.example.marius.musicbrainzforindi.utils.UiEffect

data class PlacesState(
  val progressMessage: String? = null,
  val markers: List<MarkerPlace>,
  val effect: UiEffect<Effect>? = null
) {

  sealed class Event {
    class ShowProgress(val message: String?) : Event()
    object HideProgress : Event()
  }


  sealed class Effect {
    object OpenLegacyScan : Effect()
    object ShowPaymentFailed : Effect()
  }

  fun reduce(event: Event): PlacesState {
    return when (event) {
      is PlacesState.Event.ShowProgress -> copy(progressMessage = event.message)
      PlacesState.Event.HideProgress -> copy(progressMessage = null)
    }
  }

}

